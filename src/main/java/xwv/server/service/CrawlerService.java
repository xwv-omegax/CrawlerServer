package xwv.server.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import xwv.bean.Response;
import xwv.database.AutoCloseDataSource;
import xwv.database.DataSourceCallBack;
import xwv.database.DataSourceExecutor;
import xwv.proxy.Proxy;
import xwv.util.ExceptionUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static xwv.crawler.novel.pixiv.*;

@Service
public class CrawlerService implements DisposableBean {


    @Autowired
    @Qualifier("sqlite")
    AutoCloseDataSource sqlite;

    public boolean StartPixiv(int start_id) {
        return start(false, false, start_id);
    }

    public boolean StartForwardPixiv(int start_id) {
        return start(false, true, start_id);
    }


    public void StopPixiv() {
        stop();
    }

    private boolean isDestroying = false;

    @Scheduled(cron = "0 0 16 * * *")
    @Async
    public void RefreshAllInfo() {
        String sql = "SELECT user_id FROM pixiv_novel_info GROUP BY user_id";
        List<Integer> list = sqlite.query(new DataSourceCallBack<List<Integer>>() {
            @Override
            public List<Integer> callback(ResultSet rs) {
                ArrayList<Integer> result = new ArrayList<>();
                try {
                    while (rs.next() && !isDestroying) {
                        result.add(rs.getInt("user_id"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return result;
            }
        }, sql);
        for (int uid : list) {
            RefreshInfo(uid);
        }

    }


    @Async
    public void RefreshInfo(int user_id) {
        try {

            if (!sqlite.execute(new DataSourceExecutor<Boolean>() {
                @Override
                public Boolean execute(Statement s) {
                    String sql_table_list = "SELECT name FROM sqlite_master WHERE type='table'";
                    List<String> table_list = new ArrayList<String>();
                    try (ResultSet rs = s.executeQuery(sql_table_list)) {

                        while (rs.next()) {
                            table_list.add(rs.getString("name"));
                        }


                    } catch (SQLException e) {
                        ExceptionUtil.printSingleStackTrace(e);
                    }
                    try {
                        if (!table_list.contains("refresh_mark")) {
                            String sql_create_table = "CREATE TABLE refresh_mark(" +
                                    "'user_id' INT(10)" + "," +
                                    "'time' INT(10)" + "," +
                                    "PRIMARY KEY('user_id')" +
                                    ")";
                            s.executeUpdate(sql_create_table);
                        }
                    } catch (SQLException e) {
                        ExceptionUtil.printSingleStackTrace(e);
                    }
                    table_list.clear();
                    try (ResultSet rs = s.executeQuery(sql_table_list)) {

                        while (rs.next()) {
                            table_list.add(rs.getString("name"));
                        }


                    } catch (SQLException e) {
                        ExceptionUtil.printSingleStackTrace(e);
                    }
                    return table_list.contains("refresh_mark");
                }
            })) {
                return;
            }


            String sql_get_mark = "SELECT time FROM refresh_mark WHERE user_id='" + user_id + "'";
            Long time = sqlite.query(new DataSourceCallBack<Long>() {
                @Override
                public Long callback(ResultSet rs) {
                    try {
                        if (rs.next()) {
                            return rs.getLong("time");
                        }
                    } catch (SQLException e) {
                        ExceptionUtil.printSingleStackTrace(e);
                    }
                    return 0L;
                }
            }, sql_get_mark);

            if (System.currentTimeMillis() - time > 10 * 60 * 1000) {


                String update_sql = CreateInfoPreparedUpdateSql();
                System.out.println("RefreshNovelInfo:AuthorID - " + user_id);
                Map<String, Map<String, String>> map = ParseUserNovelList(user_id, Proxy.NULL);
                for (Map<String, String> m : map.values()) {
                    String id = m.get("id");
                    String date_sql = "SELECT reupload_date FROM pixiv_novel_info WHERE id='" + id + "'";
                    String reupload_date = sqlite.query(new DataSourceCallBack<String>() {
                        @Override
                        public String callback(ResultSet rs) {
                            try {
                                if (rs.next()) {
                                    return rs.getString("reupload_date");
                                }
                            } catch (SQLException e) {
                                ExceptionUtil.printSingleStackTrace(e);
                            }
                            return null;
                        }
                    }, date_sql);

                    if (reupload_date == null || !reupload_date.equals(m.get("reupload_date"))) {
                        RefreshContent(Integer.parseInt(id));
                    }
                }

                sqlite.execute(update_sql, pre_stmt -> {
                    try {
                        for (Map<String, String> m : map.values()) {
                            SetInfoUpdatePreparedParams(pre_stmt, m);
                            pre_stmt.execute();
                        }
                    } catch (SQLException e) {
                        ExceptionUtil.printSingleStackTrace(e);
                    }
                    return null;
                });

                String sql_set_mark = "REPLACE INTO refresh_mark(user_id,time) VALUES('" + user_id + "','" + System.currentTimeMillis() + "')";
                sqlite.update(sql_set_mark);
            }
        } catch (Exception e) {
            ExceptionUtil.printSingleStackTrace(e);
        }

    }

    public void RefreshContent(int PixivID) {
        String html = null;
        boolean retry = false;
        String url = buildUrl(PixivID);
        while (html == null) {
            Response response = Proxy.NULL.PostForResponse(url, getHeaders(url));
            if (response != null) {
                if (retry) {
                    System.out.println("Retry Success:" + url);
                }
                if (response.code() < 500) {


                    if (response.code() == 200) {
                        html = response.body();
                    } else {
                        return;
                    }

                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {

                    }
                }
            } else {
                retry = true;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignored) {
                }
            }
        }
        if (html.isEmpty()) {
            return;
        }
        Document document = Jsoup.parse(html);
        PixivNovel novel = CreateNovelFromDocument(PixivID, document);
        if (novel != null) {
            SaveNovel(novel);
        }
    }

    @Override
    public void destroy() throws Exception {
        isDestroying = true;
        StopPixiv();
    }
}
