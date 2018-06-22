package xwv.server.controller;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import xwv.server.service.CrawlerService;
import xwv.server.service.NovelService;
import xwv.server.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@Controller
public class RequestController {

    @Autowired
    CrawlerService crawlerService;

    @Autowired
    NovelService novelService;

    @Autowired
    UserService userService;


    @RequestMapping(value = "/novel", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String DoNovelRequest(@RequestParam(name = "task", required = false) String task, @RequestParam Map<String, String> params, HttpServletRequest request) {

        if (task != null) {
            if (task.equalsIgnoreCase("list")) {
                if (params != null) {

                    String page = params.get("page");
                    String filter = params.get("filter");
                    String fid = params.get("fid");

                    int p = 0;
                    int f = 0;


                    try {
                        p = Integer.parseInt(page);

                    } catch (Exception e) {
                        e.printStackTrace(System.out);
                    }
                    try {
                        f = Integer.parseInt(fid);

                    } catch (Exception e) {
//                        ExceptionUtil.printSingleStackTrace(e, System.err);
                    }
                    return novelService.list(p, request.getCookies(), filter, f).toString();
                }
                return "{}";

            }

            if (task.equalsIgnoreCase("view")) {
                if (params != null) {

                    String id_str = params.get("id");

                    if (id_str != null) {

                        try {
                            int id = Integer.parseInt(id_str);
                            JSONObject result = novelService.view(id);
                            int user_id = result.getJSONObject("novel").getInt("user_id");
                            crawlerService.RefreshInfo(user_id);
                            return result.toString();
                        } catch (NumberFormatException e) {
                            e.printStackTrace(System.out);
                        }
                    }
                }
                return "{}";
            }


            if (task.equalsIgnoreCase("next")) {
                if (params != null) {

                    String id_str = params.get("id");

                    if (id_str != null) {

                        try {
                            int id = Integer.parseInt(id_str);
                            return novelService.next(id).toString();
                        } catch (NumberFormatException e) {
                            e.printStackTrace(System.out);
                        }
                    }
                }
                return "{}";
            }


            if (task.equalsIgnoreCase("last")) {
                if (params != null) {

                    String id_str = params.get("id");

                    if (id_str != null) {

                        try {
                            int id = Integer.parseInt(id_str);
                            return novelService.last(id).toString();
                        } catch (NumberFormatException e) {
                            e.printStackTrace(System.out);
                        }
                    }
                }
                return "{}";
            }


        }

        return "{}";
    }


    @RequestMapping(value = "/user", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String DoUserRequest(@RequestParam(name = "task", required = false) String task, @RequestParam Map<String, String> params, HttpServletRequest request, HttpServletResponse response) {

        final JSONObject resp = new JSONObject();
        if (task != null) {
            if (task.equalsIgnoreCase("Register")) {
                String username = params.get("username");
                String password = params.get("password");
                int id = userService.doRegister(username, password);
                if (id > 0) {
                    resp.put("success", true);
                    response.addCookie(new Cookie("UID", String.valueOf(id)));
                    response.addCookie(new Cookie("USERNAME", username.toLowerCase()));
                } else {
                    if (id == -1) {
                        resp.put("msg", "该用户已存在");
                    }
                    resp.put("success", false);
                }
                return resp.toString();

            }

            if (task.equalsIgnoreCase("Login")) {
                String username = params.get("username");
                String password = params.get("password");
                int id = userService.doLogin(username, password);
                if (id > 0) {
                    resp.put("success", true);
                    response.addCookie(new Cookie("UID", String.valueOf(id)));
                    response.addCookie(new Cookie("USERNAME", username.toLowerCase()));
                } else {
                    resp.put("success", false);
                    resp.put("msg", "用户名或密码错误");
                }

                return resp.toString();
            }

            if (task.equalsIgnoreCase("ModifyPassword")) {
                String old_password = params.get("old_password");
                String new_password = params.get("new_password");
                int uid = GetUidFromCookies(request.getCookies());
                int res = userService.doModify(uid, old_password, new_password);
                if (res > 0) {
                    resp.put("success", true);
                } else {
                    resp.put("success", false);
                    if (res == -1) {
                        resp.put("msg", "密码错误");
                    } else {
                        resp.put("msg", "修改出错");
                    }
                }

                return resp.toString();
            }

            if (task.equalsIgnoreCase("AppendBan")) {
                int result = userService.AppendBan(params.get("value"), params.get("type"), request.getCookies());
                if (result > 0) {
                    resp.put("success", true);
                } else {
                    resp.put("success", false);
                }
                return resp.toString();
            }

            if (task.equalsIgnoreCase("GetBanList")) {
                JSONObject result = userService.getBanList(GetUidFromCookies(request.getCookies()));

                resp.put("success", true);
                resp.put("ban_list", result);
                return resp.toString();
            }

            if (task.equalsIgnoreCase("RemoveBan")) {
                int result = userService.RemoveBan(params.get("value"), params.get("type"), request.getCookies());
                if (result > 0) {
                    resp.put("success", true);
                } else {
                    resp.put("success", false);
                }
                return resp.toString();
            }

            if (task.equalsIgnoreCase("AppendBanAuthor")) {
                return resp.toString();
            }

            if (task.equalsIgnoreCase("RemoveBanAuthor")) {
                return resp.toString();
            }
        }

        return resp.toString();
    }


    public int GetUidFromCookies(Cookie[] cookies) {
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
        return uid;
    }


    @RequestMapping(value = "/crawler", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String DoRequest(@RequestParam Map<String, String> param, @RequestParam(name = "task", required = false) String task, HttpServletRequest request) {
        if (task != null) {
            if (task.equalsIgnoreCase("StartPixiv")) {

                int start_id = 0;
                try {
                    start_id = Integer.parseInt(param.get("id"));
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                    System.err.println(e.toString() + "(" + e.getStackTrace()[2].getFileName() + ":" + e.getStackTrace()[2].getLineNumber() + ")");
                }

                return String.valueOf(crawlerService.StartPixiv(start_id));
            }

            if (task.equalsIgnoreCase("StartForwardPixiv")) {

                int start_id = 0;
                try {
                    start_id = Integer.parseInt(param.get("id"));
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                    System.err.println(e.toString() + "(" + e.getStackTrace()[2].getFileName() + ":" + e.getStackTrace()[2].getLineNumber() + ")");
                }

                return String.valueOf(crawlerService.StartForwardPixiv(start_id));
            }

            if (task.equalsIgnoreCase("StopPixiv")) {
                crawlerService.StopPixiv();
                return "Stopped";
            }

            if (task.equalsIgnoreCase("RefreshAllInfo")) {
                crawlerService.RefreshAllInfo();
                return "Refreshing";
            }


            return "Task=\"" + task + "\"";

        } else {
            try (InputStream is = request.getInputStream()) {
                if (is != null) {
                    String line;
                    if ((line = new BufferedReader(new InputStreamReader(is)).readLine()) != null) {
                        System.out.println(line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(param);
            return param.toString();
        }
    }


}
