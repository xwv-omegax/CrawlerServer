(function () {
    var html = "<div id='login_area' class='modal fade ' tabindex='-1'>\n" +
        "    <div class='modal-dialog width_300 margin-top_100' style='margin: auto;margin-top: 100px'>\n" +
        "        <div class=' modal-content '>\n" +
        "            <div class='modal-body'>\n" +
        "                <div style='margin-bottom: 5px'>\n" +
        "                    <button type='button' class='close' data-dismiss='modal'>\n" +
        "                        &times;\n" +
        "                    </button>\n" +
        "                    <ul class='nav nav-tabs'>\n" +
        "                        <li class='active'><a style='padding: 5px;padding-left: 10px;padding-right: 10px'\n" +
        "                                              href='#login_tab' data-toggle='tab'>登陆</a>\n" +
        "                        </li>\n" +
        "                        <li><a class='' style='padding: 5px;padding-left: 10px;padding-right: 10px'\n" +
        "                               href='#reg_tab'\n" +
        "                               data-toggle='tab'>注册</a>\n" +
        "                        </li>\n" +
        "                    </ul>\n" +
        "                </div>\n" +
        "                <div style='padding-top: 2px;padding-bottom: 8px'>\n" +
        "                    <div id='dialog_loading' class='width_100p text-center bg-info radius-5 '\n" +
        "                         style='display: none'>\n" +
        "                        <span class='font-bold'>\n" +
        "                            <span class='animate_1'>·</span>\n" +
        "                            <span class='animate_2'>·</span>\n" +
        "                            <span class='animate_3'>·</span>\n" +
        "                            <span class='animate_4'>·</span>\n" +
        "                            <span class='animate_5'>·</span>\n" +
        "                        </span>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "\n" +
        "\n" +
        "                <div class='tab-content'>\n" +
        "                    <div class='tab-pane fade in active' id='login_tab'>\n" +
        "\n" +
        "                        <div class='form-group'>\n" +
        "                            <input class='form-control' style='width: 100%' type='text' placeholder='用户名'\n" +
        "                                   id='username'>\n" +
        "                        </div>\n" +
        "                        <div class='form-group no_margin'>\n" +
        "                            <input class='form-control' style='width: 100%' type='password' placeholder='密码'\n" +
        "                                   id='password'>\n" +
        "                        </div>\n" +
        "                        <div style='padding-top: 7px;padding-bottom: 8px'>\n" +
        "                            <div id='login_alert' class='alert  alert-danger no_margin '\n" +
        "                                 style='padding: 5px;display:none'>\n" +
        "                                <button type='button' class='close'>\n" +
        "                                    &times;\n" +
        "                                </button>\n" +
        "                                <span class='alert_msg'></span>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                        <div class='form-group'>\n" +
        "                            <button class='btn btn-default width_100p' id='login'>登陆</button>\n" +
        "                        </div>\n" +
        "                    </div>\n" +
        "\n" +
        "                    <div class='tab-pane fade' id='reg_tab'>\n" +
        "\n" +
        "                        <div class='form-group'>\n" +
        "                            <input class='form-control' style='width: 100%' type='text' placeholder='用户名'\n" +
        "                                   id='reg_username'>\n" +
        "                        </div>\n" +
        "                        <div class='form-group'>\n" +
        "                            <input class='form-control' style='width: 100%' type='password' placeholder='密码'\n" +
        "                                   id='reg_password'>\n" +
        "                        </div>\n" +
        "                        <div class='form-group no_margin'>\n" +
        "                            <input class='form-control' style='width: 100%' type='password' placeholder='再次输入密码'\n" +
        "                                   id='reg_check_password'>\n" +
        "                        </div>\n" +
        "                        <div style='padding-top: 7px;padding-bottom: 8px'>\n" +
        "                            <div id='reg_alert' class='alert  alert-danger no_margin '\n" +
        "                                 style='padding: 5px;display:none'>\n" +
        "                                <button type='button' class='close'>\n" +
        "                                    &times;\n" +
        "                                </button>\n" +
        "                                <span class='alert_msg'></span>\n" +
        "                            </div>\n" +
        "                        </div>\n" +
        "                        <div class='form-group'>\n" +
        "                            <button class='btn btn-default width_100p' id='reg'>注册</button>\n" +
        "                        </div>\n" +
        "                    </div>\n" +
        "                    <div>\n" +
        "                        <hr class='no_margin margin-top_20' style='width: 100%;'>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>\n" +
        "\n" +
        "<div id='modify_modal' class='modal fade ' tabindex='-1'>\n" +
        "    <div class='modal-dialog width_300 margin-top_100' style='margin: auto;margin-top: 100px'>\n" +
        "        <div class=' modal-content '>\n" +
        "            <div class='modal-body'>\n" +
        "                <div style='margin-bottom: 5px'>\n" +
        "                    <button type='button' class='close' data-dismiss='modal'>\n" +
        "                        &times;\n" +
        "                    </button>\n" +
        "                    <strong>修改密码</strong>\n" +
        "                </div>\n" +
        "                <div style='padding-top: 2px;padding-bottom: 8px'>\n" +
        "                    <div id='modify_loading' class='width_100p text-center bg-info radius-5 '\n" +
        "                         style='display: none'>\n" +
        "                        <span class='font-bold'>\n" +
        "                            <span class='animate_1'>·</span>\n" +
        "                            <span class='animate_2'>·</span>\n" +
        "                            <span class='animate_3'>·</span>\n" +
        "                            <span class='animate_4'>·</span>\n" +
        "                            <span class='animate_5'>·</span>\n" +
        "                        </span>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "\n" +
        "\n" +
        "                <div class='form-group'>\n" +
        "                    <input class='form-control' style='width: 100%' type='password' placeholder='原密码'\n" +
        "                           id='old_password'>\n" +
        "                </div>\n" +
        "                <div class='form-group'>\n" +
        "                    <input class='form-control' style='width: 100%' type='password' placeholder='新密码'\n" +
        "                           id='new_password'>\n" +
        "                </div>\n" +
        "                <div class='form-group no_margin'>\n" +
        "                    <input class='form-control' style='width: 100%' type='password' placeholder='再次输入新密码'\n" +
        "                           id='new_check_password'>\n" +
        "                </div>\n" +
        "                <div style='padding-top: 7px;padding-bottom: 8px'>\n" +
        "                    <div id='modify_alert' class='alert  alert-danger no_margin '\n" +
        "                         style='padding: 5px;display:none'>\n" +
        "                        <button type='button' class='close'>\n" +
        "                            &times;\n" +
        "                        </button>\n" +
        "                        <span class='alert_msg'></span>\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "                <div class='form-group'>\n" +
        "                    <button class='btn btn-default width_100p' id='modify_password'>提交</button>\n" +
        "                </div>\n" +
        "            </div>\n" +
        "            <div>\n" +
        "                <hr class='no_margin margin-top_20' style='width: 100%;'>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</div>\n" +
        "\n" +
        "\n" +
        "<div id='loading' class='width_100p text-center bg-info radius-5' style='display: none'>\n" +
        "            <span class='font-bold'>\n" +
        "                <span class='animate_1'>·</span>\n" +
        "                <span class='animate_2'>·</span>\n" +
        "                <span class='animate_3'>·</span>\n" +
        "                <span class='animate_4'>·</span>\n" +
        "                <span class='animate_5'>·</span>\n" +
        "            </span>\n" +
        "</div>\n" +
        "\n" +
        "<div class='inline-table width_100p'>\n" +
        "\n" +
        "    <div class='font-size_40 table-cell  width_150' style='color:#0096FF;font-weight: 300 '>\n" +
        "        <span id='logo' style='cursor: pointer'>CPN</span>\n" +
        "    </div>\n" +
        "    <div class='table-cell valign_middle width_min' id='login_btn_area'>\n" +
        "        <div class='inline-block pull-right '>\n" +
        "                    <span>\n" +
        "                        <button class='btn btn-sm btn-info pull-right ' data-toggle='modal' data-target='#login_area'\n" +
        "                                id='btn_login'>登陆</button>\n" +
        "                    </span>\n" +
        "\n" +
        "        </div>\n" +
        "    </div>\n" +
        "    <div class='table-cell valign_middle width_min  display_none' id='user_info'>\n" +
        "\n" +
        "        <div class='pull-right'>\n" +
        "            <div class='table-cell valign_middle dropdown'>\n" +
        "\n" +
        "                <div class='inline-block text-right' data-toggle='dropdown'>\n" +
        "                    <div class=''>\n" +
        "                        <div class='inline-block' style='cursor: pointer;font-size:16px;'>\n" +
        "                            <span id='user'></span>\n" +
        "                            <b class='caret'></b>\n" +
        "                        </div>\n" +
        "\n" +
        "                    </div>\n" +
        "                </div>\n" +
        "                <ul class='dropdown-menu pull-right'>\n" +
        "                    <li>\n" +
        "                        <a href='banlist.html' style=\"color: #666666\">屏蔽列表</a>\n" +
        "                    </li>\n" +
        "                    <li>\n" +
        "                        <a href='javascript:;' style=\"color: #666666\" id='a_switch'>\n" +
        "                            <span>奇怪的开关</span>\n" +
        "                            <div class='inline-block margin-left_20'>\n" +
        "                                <div id='switch' class='display_none ui-slider inline-block'\n" +
        "                                     style='border-radius: 6px;height: 10px;width: 16px;'>\n" +
        "                                    <div class='ui-slider-handle ' id='handle'\n" +
        "                                         style='height: 14px;width: 14px;margin-top: 1px;margin-left:-7px;border-radius:50%'></div>\n" +
        "                                </div>\n" +
        "                            </div>\n" +
        "                        </a>\n" +
        "                    </li>\n" +
        "                    <li data-toggle='modal' data-target='#modify_modal'>\n" +
        "                        <a href='javascript:;' style=\"color: #666666\">修改密码</a>\n" +
        "                    </li>\n" +
        "                    <li>\n" +
        "                        <a href='javascript:;' id='logout' style=\"color: #666666\">注销</a>\n" +
        "                    </li>\n" +
        "                </ul>\n" +
        "            </div>\n" +
        "        </div>\n" +
        "\n" +
        "    </div>\n" +
        "\n" +
        "</div>\n" +
        "<hr style='width: 100%' class='no_margin'>";

    // $.ajax({
    //     type: "get",
    //     url: "header.html",
    //     async: false,
    //     success: function (data) {
    //         html = data;
    //     }
    // });

    var onRefresh;

    var refresh;

    var loading;


    function init(element) {
        $(element).after(html);
        $(element).remove();

        loading = $("#loading");
        var dialog_loading = $("#dialog_loading");
        var modify_loading = $("#modify_loading");
        var login_area = $('#login_area');
        var modify_modal = $('#modify_modal');

        var reg_alert = $("#reg_alert");
        var login_alert = $("#login_alert");
        var modify_alert = $("#modify_alert");


        function RegAlert(msg) {
            DialogAlert(reg_alert, msg);
            reg_alert.children(".close").unbind("click").click(function () {
                RegAlert();
            });
        }

        function LoginAlert(msg) {
            DialogAlert(login_alert, msg);
            login_alert.children(".close").unbind("click").click(function () {
                LoginAlert();
            });
        }

        function DialogAlert(alert, msg) {
            var d_msg = alert.children(".alert_msg");
            d_msg.empty();
            d_msg.text(msg);
            if (msg && msg.trim()) {
                alert.slideDown();
            } else {
                alert.slideUp();
            }
        }

        login_area.on('hidden.bs.modal', function () {
            var username = $("#username");
            var password = $("#password");
            var reg_username = $("#reg_username");
            var reg_password = $("#reg_password");
            var reg_check_password = $("#reg_check_password");
            username.val("");
            password.val("");
            reg_username.val("");
            reg_password.val("");
            reg_check_password.val("");
            LoginAlert();
            RegAlert();
        });

        modify_modal.on('hidden.bs.modal', function () {
            var old_password = $("#old_password");
            var new_password = $("#new_password");
            var new_check_password = $("#new_check_password");

            old_password.val("");
            new_password.val("");
            new_check_password.val("");
            ModifyAlert();
        });

        function StopPropagation(event) {
            event.cancelBubble = true;
            event.stopPropagation();
        }


        var sw = $("#switch").slider({
            range: "min",
            min: 0,
            max: 1,
            value: $.cookie("R18"),
            change: function (event, ui) {
                var r18 = $.cookie("R18");
                if (r18 != ui.value) {
                    $.cookie("R18", ui.value);
                    refresh();
                }
            }
        });


        $("#handle").unbind("click").click(function () {
            var v = sw.slider("value");
            if (!v) {
                v = 0;
            }
            sw.slider("value", 1 - v);
        });

        $("#a_switch").unbind("click").click(function (event) {
            StopPropagation(event);
            var id = event.target.id;
            if (id != "handle" && id != "switch") {
                $("#handle").click();
            }
        });


        function Logout() {
            $.cookie("UID", "");
            $.cookie("USERNAME", "");
            $.cookie("R18", "");
            refresh();
        }

        function BindAction() {


            $("#login").unbind("click").click(function (event) {
                StopPropagation(event);
                var username = $("#username").val();
                var password = $("#password").val();
                if (!username.trim() || !password.trim()) {
                    LoginAlert("用户名、密码不能为空");
                    return;
                }
                LoginAlert();
                Login(username, password);
            });

            $("#logout").unbind("click").click(function (event) {
                StopPropagation(event);
                Logout();
            });

            $("#reg").unbind("click").click(function (event) {
                StopPropagation(event);
                var username = $("#reg_username").val();
                var password = $("#reg_password").val();
                var ck_password = $("#reg_check_password").val();

                if (!username.trim() || !password.trim()) {
                    RegAlert("用户名、密码不能为空");
                    return;
                }

                if (ck_password == password) {
                    RegAlert();
                    Register(username, password);
                } else {
                    RegAlert("两次输入的密码不一致");
                }
            });

            $("#modify_password").unbind("click").click(function (event) {
                StopPropagation(event);
                var old_password = $("#old_password").val();
                var new_password = $("#new_password").val();
                var ck_password = $("#new_check_password").val();

                if (!old_password.trim() || !new_password.trim()) {
                    ModifyAlert("密码不能为空");
                    return;
                }

                if (ck_password == new_password) {
                    ModifyAlert();
                    ModifyPassword(old_password, new_password);
                } else {
                    ModifyAlert("两次输入的密码不一致");
                }
                return false;
            });

            $("#logo").unbind("click").click(function (event) {
                StopPropagation(event);
                location.href = "index.html";
                return false;
            });

        }

        function ModifyAlert(msg) {
            DialogAlert(modify_alert, msg);
            modify_alert.children(".close").unbind("click").click(function () {
                ModifyAlert();
            });
        }


        function ModifyPassword(old_password, new_password) {
            modify_loading.slideDown();
            $.ajax({
                type: "post",
                url: "user?task=ModifyPassword",
                data: {
                    old_password: old_password,
                    new_password: new_password
                },
                dataType: "json",
                success: function (data) {
                    if (data.success) {
                        $("#modify_modal").modal("hide");
                        Logout();
                    } else {
                        ModifyAlert(data.msg);
                    }

                },
                complete: function () {
                    modify_loading.slideUp();
                }
            });
        }


        function Register(username, password) {
            dialog_loading.slideDown();
            $.ajax({
                type: "post",
                url: "user?task=Register",
                data: {
                    username: username,
                    password: password,
                },
                dataType: "json",
                success: function (data) {
                    if (data.success) {
                        $("#login_area").modal("hide");
                        refresh();
                    } else {
                        RegAlert(data.msg);
                    }


                },
                complete: function () {
                    dialog_loading.slideUp();
                }
            });
        }


        function Login(username, password) {
            dialog_loading.slideDown();
            $.ajax({
                type: "post",
                url: "user?task=Login",
                data: {
                    username: username,
                    password: password
                },
                dataType: "json",
                success: function (data) {
                    if (data.success) {
                        $("#login_area").modal("hide");
                        refresh();
                    } else {

                        LoginAlert(data.msg);
                    }
                },
                complete: function () {
                    dialog_loading.slideUp();
                }
            });
        }


        function RefreshLoginArea() {
            if ($.cookie("UID") && $.cookie("UID") > 0) {
                $("#login_btn_area").hide();
                $("#user_info").show();
                $("#user").text($.cookie("USERNAME") ? $.cookie("USERNAME") : "");
                $(sw).show();
                sw.slider("value", $.cookie("R18") ? $.cookie("R18") : 0);

            } else {
                $(sw).hide();
                $("#user_info").hide();
                $("#login_btn_area").show();
            }
        }

        refresh = function (func) {

            if (typeof func === "function") {
                onRefresh = func;
            } else {
                if (onRefresh && typeof onRefresh === "function") {
                    (function () {
                        onRefresh();
                    })();
                }
            }
            BindAction();
            RefreshLoginArea();
        };

        BindAction();
        RefreshLoginArea();

        function AppendCookie(key, value) {
            var values = String(value).split(/[ ]/);
            var old = $.cookie(key) || "";

            for (var i in values) {
                var find = new RegExp(values[i] + "[ ]+");
                var find_end = new RegExp(values[i] + "$");
                if (!find.test(old) && !find_end.test(old)) {
                    if (old && old != "") {
                        old += " ";
                    }
                    old += values[i];
                }
            }

            $.cookie(key, old, {
                expires: 30
            });
            return $.cookie(key);
        }


        function RemoveCookie(key, value) {
            var values = String(value).split(/[ ]/);
            var old = $.cookie(key) || "";

            for (var i in values) {
                var find = new RegExp(values[i] + "[ ]+");
                var find_end = new RegExp(values[i] + "$");
                old = old.replace(find, "");
                old = old.replace(find_end, "");
            }

            $.cookie(key, old, {
                expires: 30
            });
            return $.cookie(key);
        }


    }

    window.xwv = {
        Header: {
            init: init,
            refresh: function (element) {
                refresh(element);
            },
            getLoading: function () {
                return loading;
            }
        }
    };
})
();