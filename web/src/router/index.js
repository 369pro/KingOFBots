import { createRouter, createWebHistory } from 'vue-router'
import PKIndexView from "../views/pk/PKIndexView"
import RecordIndexView from "../views/record/RecordIndexView"
import RankListIndexView from "../views/ranklist/RankListIndexView"
import UserBotIndexView from "../views/user/bot/UserBotIndexView"
import UserAccountLoginView from "../views/user/account/UserAccountLoginView"
import UserAccountRegisterView from "../views/user/account/UserAccountRegisterView"
import NotFoundView from "../views/error/NotFoundView"
import store from '../store'
const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/",
    meta:{
      requestAuth:true
    }
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PKIndexView,
    meta:{
      requestAuth:true
    }
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta:{
      requestAuth:true
    }
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RankListIndexView,
    meta:{
      requestAuth:true
    }
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta:{
      requestAuth:true
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
  },
  {
    path: "/404/",
    name: "404",
    component: NotFoundView,
  },
  {
    path: "/:catchAll(.*)",
    redirect: "/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
router.beforeEach((to, from, next) => {
  if(to.meta.requestAuth && !store.state.user.is_login)
  {
    next({name: "user_account_login"});
  }else{
    next();
  }
})

export default router
