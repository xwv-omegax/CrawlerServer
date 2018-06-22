package xwv.server.service;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import xwv.crawler.novel.pixiv;
import xwv.database.AutoCloseDataSource;
import xwv.database.DataSourceCallBack;
import xwv.database.DataSourceExecutor;
import xwv.jedis.AutoCloseJedisPool;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import static xwv.util.ExceptionUtil.printSingleStackTrace;

@Service
public class NovelService {

    @Autowired
    private AutoCloseJedisPool jedis;

    @Autowired
    @Qualifier("sqlite")
    private AutoCloseDataSource sqlite;


    @Autowired
    @Qualifier("mysql")
    private AutoCloseDataSource mysql;


    private static final int page_size = 20;


    private static BigDecimal SimpleStringHash(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new BigDecimal("0");
        }

        char[] char_arr = str.toCharArray();
        StringBuilder int_str = new StringBuilder();

        for (int i : char_arr) {
            int_str.append(String.valueOf(i));
        }
        return new BigDecimal(int_str.toString());
    }


    public JSONObject last(int id) {
        String sql = "" +
                " SELECT id,title " +
                " FROM pixiv_novel_info " +
                " WHERE series_id " +
                " IN ( " +
                " SELECT series_id " +
                " FROM pixiv_novel_info " +
                " WHERE id='" + id + "' " +
                " ) " +
                " AND id<'" + id + "' " +
                " ORDER BY id DESC " +
                " LIMIT 1 ";
        return sqlite.query(new DataSourceCallBack<JSONObject>() {
            @Override
            public JSONObject callback(ResultSet rs) {
                JSONObject result = new JSONObject();
                try {
                    if (rs.next()) {
                        result.put("id", rs.getInt("id"));
                        result.put("title", rs.getString("title"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return result;
            }
        }, sql);


    }

    public JSONObject next(int id) {
        String sql = "" +
                " SELECT id,title " +
                " FROM pixiv_novel_info " +
                " WHERE series_id " +
                " IN ( " +
                " SELECT series_id " +
                " FROM pixiv_novel_info " +
                " WHERE id='" + id + "' " +
                " ) " +
                " AND id>'" + id + "' " +
                " ORDER BY id " +
                " LIMIT 1 ";
        return sqlite.query(new DataSourceCallBack<JSONObject>() {
            @Override
            public JSONObject callback(ResultSet rs) {
                JSONObject result = new JSONObject();
                try {
                    if (rs.next()) {
                        result.put("id", rs.getInt("id"));
                        result.put("title", rs.getString("title"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return result;
            }
        }, sql);
    }

    public JSONObject list(int page, Cookie[] cookies) {
        return list(page, cookies, null, 0);
    }

    public JSONObject list(int page, Cookie[] cookies, String filter, int fid) {
        if (page < 1) {
            page = 1;
        }
        int start = (page - 1) * page_size;
        int end = page * page_size;
        JSONObject result = new JSONObject();

        String banTag = null;
        String banAuthor = null;
        int uid = 0;
        int r18 = 0;

        String sort = "HOT";

        if (cookies != null) {
            for (Cookie c : cookies) {

                if (c.getName().equals("UID")) {
                    try {
                        uid = Integer.parseInt(c.getValue());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }


                if (c.getName().equals("R18")) {
                    try {
                        r18 = Integer.parseInt(c.getValue());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                if (c.getName().equals("SORT")) {
                    sort = c.getValue();
                }
            }
        }

        if (r18 > 1 || uid < 1) {
            r18 = 0;
        }

        if (uid > 0) {
            int finalUid = uid;
            String[] res = mysql.execute(new DataSourceExecutor<String[]>() {
                @Override
                public String[] execute(Statement s) {
                    String[] result = new String[2];
                    String sql = "SELECT * FROM user WHERE UID='" + finalUid + "'";
                    try (ResultSet rs = s.executeQuery(sql)) {
                        if (rs.next()) {
                            result[0] = rs.getString("UBanedTag");
                            result[1] = rs.getString("UBanedAuthor");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    return result;
                }
            });
            banTag = res[0];
            banAuthor = res[1];
        }

        StringBuilder tag_con = new StringBuilder();
        if (banTag != null && !banTag.trim().isEmpty()) {
            for (String tag : banTag.split("[ ]+")) {
                tag_con.append(" AND tag NOT LIKE '%").append(tag).append("%' ");
            }
        }


        StringBuilder author_con = new StringBuilder();
        if (banAuthor != null && !banAuthor.trim().isEmpty()) {
            for (String id : banAuthor.split("[ ]+")) {
                author_con.append(" AND pixiv_novel_info.user_id !='").append(id).append("' ");
            }
        }

        String order_sql = "" +
                " ORDER BY " +
                " hot DESC, " +
                " pixiv_novel_info.user_id , " +
                " series_id ";

        if (sort != null && sort.equalsIgnoreCase("TIME")) {
            order_sql = "" +
                    " ORDER BY " +
                    " upload_time DESC ";

        }

        String series_con = "";

        if (filter != null && !filter.trim().isEmpty()) {
            boolean isApply = false;

            if (filter.equals("series") && fid > 0) {
                series_con = " AND pixiv_novel_info.series_id ='" + fid + "' ";
                isApply = true;

            } else if (filter.equals("author")) {
                author_con.delete(0, author_con.length());
                author_con.append(" AND pixiv_novel_info.user_id ='").append(fid).append("' ");
                isApply = true;
            }


            if (isApply) {
                order_sql = " ORDER BY id ";
            }


        }


        String sql_list = "" +
                " SELECT " +
                " *, " +
                " (count + bookmark_count) AS hot " +
                " FROM pixiv_novel_info " +
//                " INNER JOIN ( " +
//                "   SELECT " +
//                "   MAX(count + bookmark_count) AS hot, " +
//                "   user_id " +
//                "   FROM pixiv_novel_info " +
//                "   WHERE r18 = 1 " +
//                "   GROUP BY user_id " +
//                " ) h " +
//                " ON pixiv_novel_info.user_id = h.user_id " +
                " WHERE r18='" + r18 + "' " +
                tag_con + author_con + series_con +
                order_sql +
                " LIMIT " + start + "," + page_size;

        String sql_count = "" +
                " SELECT " +
                " COUNT(1) AS count " +
                " FROM pixiv_novel_info " +
//                " INNER JOIN ( " +
//                "   SELECT " +
//                "   MAX(count + bookmark_count) AS hot, " +
//                "   user_id " +
//                "   FROM pixiv_novel_info " +
//                "   WHERE r18 = 1 " +
//                "   GROUP BY user_id " +
//                " ) h " +
//                " ON pixiv_novel_info.user_id = h.user_id " +
                " WHERE r18='" + r18 + "' " +
                tag_con + author_con + series_con;
        JSONObject array = sqlite.execute(new DataSourceExecutor<JSONObject>() {
            @Override
            public JSONObject execute(Statement s) {
                JSONObject result = new JSONObject();
                try (ResultSet rs = s.executeQuery(sql_list)) {

                    int seq = 0;

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    while (rs.next()) {
                        JSONObject info = new JSONObject();
                        for (int index = 0; index < rs.getMetaData().getColumnCount(); index++) {
                            info.put(rs.getMetaData().getColumnName(index + 1).toLowerCase(), rs.getString(index + 1));
                        }
                        info.put("tag_a", info.getString("tag").split(" "));
                        long time = info.getLong("upload_time");
                        info.put("time", dateFormat.format(new Date(time * 1000)));
                        result.put(seq, info);
                        seq++;

                    }


                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                    return null;
                }

                try (ResultSet rs = s.executeQuery(sql_count)) {
                    while (rs.next()) {
                        int count = rs.getInt("count");
                        int maxPage = count / page_size;
                        if (maxPage % page_size > 0) {
                            maxPage++;
                        }
                        result.put("max_page", maxPage);
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                    return null;
                }
                return result;
            }
        });

        result.put("list", array);
        return result;

    }

    public JSONObject view(int id) {
//        pixiv.TextStoragePath

        JSONObject result = new JSONObject();


//        Map<String, String> map = jedis.call(new JedisCaller<Map<String, String>>() {
//            @Override
//            public Map<String, String> call(Jedis jedis) {
//
//
//                return jedis.hgetAll("pixiv.novel.info:" + id);
//
//            }
//        });


        String sql_list = "SELECT * FROM pixiv_novel_info WHERE id=" + id;
        JSONObject info = sqlite.query(new DataSourceCallBack<JSONObject>() {
            @Override
            public JSONObject callback(ResultSet rs) {
                try {
                    if (rs.next()) {
                        JSONObject info = new JSONObject();
                        for (int index = 0; index < rs.getMetaData().getColumnCount(); index++) {
                            info.put(rs.getMetaData().getColumnName(index + 1).toLowerCase(), rs.getString(index + 1));
                        }
                        return info;
                    } else {
                        return null;
                    }
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                    return null;
                }
            }
        }, sql_list);

        result.put("novel", info);


        try (FileReader reader = new FileReader(pixiv.TextStoragePath + File.separator + id + ".txt")) {

            StringBuilder builder = new StringBuilder();
            char[] buffer = new char[1024];
            int len;
            while ((len = reader.read(buffer)) > -1) {
                builder.append(buffer, 0, len);
            }
            result.getJSONObject("novel").put("content", builder.toString());

        } catch (IOException e) {
            printSingleStackTrace(e);
            result.getJSONObject("novel").put("content", "&lt;&lt;Content Not Found&gt;&gt;");
        }

        return result;
    }

    public static void main(String[] args) {
        BigDecimal h1 = SimpleStringHash("蜂蜜柚子茶(yezinod)");
        BigDecimal h2 = SimpleStringHash("冬青棗子糕(yezinod)");

        System.out.println(h1.compareTo(h2));
    }


}
