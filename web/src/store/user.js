import $ from 'jquery'

export default {
    state: {
        id: "",              // 注意此处是字符串类型
        username: "",
        photo: "",
        token: "",
        is_login: false,
        pulling_info: true,  // 正在从后端获取信息
    },
    getters: {
    },
    mutations: { // 同步函数(没有访问带链接的)用commit函数调用
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        logout(state) {
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
        updatePullingInfo(state, pulling_info)
        {
            state.pulling_info = pulling_info;
        }
    },
    actions: {  // 异步函数(访问带链接的) 用dispatch调用
        login(context, data) {
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/token/",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp) {
                    if (resp.error_message === "success") {  // 此处血的教训是resp里面的 error_message
                        context.commit("updateToken", resp.token); // 后端的内容就是要包含 error_message这个关键词
                        localStorage.setItem('jwt_token',resp.token);
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            });
        },
        getinfo(context, data) {
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/info/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + context.state.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        context.commit("updateUser", {
                            ...resp,
                            is_login: true,
                        });
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            })
        },
        logout(context) {
            localStorage.removeItem("jwt_token");
            context.commit("logout");
        }
    },
    modules: {
    }
}