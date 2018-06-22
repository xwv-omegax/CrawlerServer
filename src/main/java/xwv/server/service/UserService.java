package xwv.server.service;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import xwv.database.AutoCloseDataSource;
import xwv.database.DataSourceExecutor;

import javax.servlet.http.Cookie;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    @Qualifier("mysql")
    private AutoCloseDataSource mysql;


    @Autowired
    @Qualifier("sqlite")
    private AutoCloseDataSource sqlite;


    public int doLogin(String username, String password) {
        return mysql.execute(new DataSourceExecutor<Integer>() {
            @Override
            public Integer execute(Statement s) {
                String sql_find = "SELECT * FROM user WHERE UName='" + username + "'";
                try (ResultSet rs = s.executeQuery(sql_find)) {
                    if (rs.next()) {
                        String pas = rs.getString("UPassword");

                        if (password.equals(pas)) {
                            return rs.getInt("UID");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }


    public Integer doRegister(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return 0;
        }
        return mysql.execute(new DataSourceExecutor<Integer>() {
            @Override
            public Integer execute(Statement s) {
                try {
                    s.getConnection().setAutoCommit(false);
                    String sql_find = "SELECT * FROM user WHERE UName='" + username + "'";
                    try (ResultSet rs = s.executeQuery(sql_find)) {
                        if (rs.next()) {
                            return -1;
                        } else {
                            String sql_insert = "INSERT INTO user(UName,UPassword) VALUES('" + username + "','" + password + "')";

                            if (s.executeUpdate(sql_insert) > 0) {
                                String id_sql = "SELECT UID FROM user WHERE UName='" + username + "'";
                                try (ResultSet r = s.executeQuery(id_sql)) {
                                    if (r.next()) {
                                        return r.getInt("UID");
                                    }
                                }

                            } else {
                                return 0;
                            }

                        }


                    } finally {
                        s.getConnection().commit();
                    }
                } catch (SQLException e) {
                    try {
                        s.getConnection().rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                } finally {
                    try {
                        s.getConnection().setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                return 0;
            }
        });

    }

    public Integer doModify(int uid, String oldPassword, String newPassword) {
        return mysql.execute((s) -> {
            if (oldPassword != null && newPassword != null && !newPassword.trim().isEmpty()) {
                String find_sql = "SELECT * FROM user WHERE UID='" + uid + "'";
                try (ResultSet rs = s.executeQuery(find_sql)) {
                    if (rs.next()) {
                        String password = rs.getString("UPassword");
                        if (password != null && password.equals(oldPassword)) {
                            String update_sql = "UPDATE user SET UPassword='" + newPassword + "' WHERE UID='" + uid + "'";
                            return s.executeUpdate(update_sql);

                        } else {
                            return -1;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            return 0;
        });
    }

    public JSONObject getBanList(int uid) {

        JSONObject res = new JSONObject();

        Map<String, String[]> map = mysql.execute((s) -> {
            String sql_banlist = "SELECT * FROM user WHERE UID='" + uid + "'";
            String[] baned_authors = null;
            String[] baned_tags = null;
            try (ResultSet rs = s.executeQuery(sql_banlist)) {
                if (rs.next()) {
                    String ban_author = rs.getString("UBanedAuthor");
                    String ban_tag = rs.getString("UBanedTag");
                    if (ban_author != null) {
                        baned_authors = ban_author.split("[ ]+");
                    }

                    if (ban_tag != null) {
                        baned_tags = ban_tag.split("[ ]+");
                    }


                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Map<String, String[]> result = new HashMap<>();

            result.put("authors", baned_authors);
            result.put("tags", baned_tags);

            return result;
        });

        Map<String, String> authorMap = sqlite.execute((s) -> {
            String[] authors = map.get("authors");
            Map<String, String> result = new HashMap<>();
            String con;
            if (authors == null || authors.length < 1) {
                return result;
            } else {
                con = Arrays.toString(authors).replaceAll("[\\[\\]]", "");
            }


            String sql_author_list = "SELECT user_id,user_name FROM pixiv_novel_info WHERE user_id IN (" + con + ") GROUP BY user_id";
            try (ResultSet rs = s.executeQuery(sql_author_list)) {
                while (rs.next()) {
                    result.put(rs.getString("user_id"), rs.getString("user_name"));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return result;
        });

        res.put("baned_tags", map.get("tags"));
        res.put("baned_authors", authorMap);


        return res;
    }

    public Integer RemoveBan(String value, String type, Cookie[] cookies) {

        String colName = null;
        if (type != null) {
            if (type.equals("tag")) {
                colName = "UBanedTag";
            } else if (type.equals("author")) {
                colName = "UBanedAuthor";
            }

        }
        if (colName == null) {
            return 0;
        }

        int uid = 0;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("UID")) {
                    try {
                        uid = Integer.parseInt(c.getValue());
                        break;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (uid < 1) {
            return 0;
        }

        int finalUid = uid;
        String finalColName = colName;
        return mysql.execute(new DataSourceExecutor<Integer>() {
            @Override
            public Integer execute(Statement s) {
                try {
                    s.getConnection().setAutoCommit(false);
                    String sql_get = "SELECT " + finalColName + " FROM user WHERE UID='" + finalUid + "'";
                    try (ResultSet rs = s.executeQuery(sql_get)) {
                        if (rs.next()) {
                            String old_values = rs.getString(finalColName);

                            String new_values = Remove(old_values, value);

                            String sql_update = "UPDATE  user SET " + finalColName + "='" + new_values + "' WHERE UID='" + finalUid + "'";

                            return s.executeUpdate(sql_update);
                        }


                    } finally {
                        s.getConnection().commit();
                    }
                } catch (SQLException e) {
                    try {
                        s.getConnection().rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                } finally {
                    try {
                        s.getConnection().setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                return 0;
            }
        });

    }

    public Integer AppendBan(String value, String type, Cookie[] cookies) {

        String colName = null;
        if (type != null) {
            if (type.equals("tag")) {
                colName = "UBanedTag";
            } else if (type.equals("author")) {
                colName = "UBanedAuthor";
            }

        }
        if (colName == null) {
            return 0;
        }

        int uid = 0;
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equals("UID")) {
                    try {
                        uid = Integer.parseInt(c.getValue());
                        break;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (uid < 1) {
            return 0;
        }

        int finalUid = uid;
        String finalColName = colName;
        return mysql.execute(new DataSourceExecutor<Integer>() {
            @Override
            public Integer execute(Statement s) {
                try {
                    s.getConnection().setAutoCommit(false);
                    String sql_get = "SELECT " + finalColName + " FROM user WHERE UID='" + finalUid + "'";
                    try (ResultSet rs = s.executeQuery(sql_get)) {
                        if (rs.next()) {
                            String old_values = rs.getString(finalColName);

                            String new_values = Append(old_values, value);

                            String sql_update = "UPDATE  user SET " + finalColName + "='" + new_values + "' WHERE UID='" + finalUid + "'";

                            return s.executeUpdate(sql_update);
                        }


                    } finally {
                        s.getConnection().commit();
                    }
                } catch (SQLException e) {
                    try {
                        s.getConnection().rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                } finally {
                    try {
                        s.getConnection().setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                return 0;
            }
        });

    }


    public static String Append(String old, String value) {
        if (value == null) {
            value = "";
        }

        if (old == null) {
            old = "";
        } else {
            old = " " + old + " ";
        }


        String[] values = value.split("[ ]+");

        for (String v : values) {
            Pattern find = Pattern.compile("[ ]+" + v + "[ ]+");
            if (!find.matcher(old).find()) {
                old += (" " + v);
            }
        }

        Pattern clean = Pattern.compile("[ ]{2,}");
        old = clean.matcher(old).replaceAll(" ");
        return old.trim();
    }


    public static String Remove(String old, String value) {
        if (old == null) {
            return "";
        } else {
            old = " " + old + " ";
        }
        String[] values = value.split("[ ]+");


        for (String v : values) {
//            v = v.replaceAll("\\\\u[0]*", "\\\\x");
            Pattern find = Pattern.compile("[ ]+" + v + "[ ]+");
            Matcher matcher = find.matcher(old);
            if (matcher.find()) {
                old = matcher.replaceAll(" ");
            }
        }
        Pattern clean = Pattern.compile("[ ]{2,}");
        old = clean.matcher(old).replaceAll(" ");
        return old.trim();
    }
}
