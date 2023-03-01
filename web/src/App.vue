<template>
  <NavBar />
  <router-view></router-view>
</template>

<script>
import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
// import $ from "jquery"
export default{
  components:{
    NavBar
  },

  setup()
  {
    
  }

}
</script>


<style>
body {
  /* background-image: url("@/assets/background.png");
  background-size: cover; */

  background-image:
  url("./assets/images/background.jpg");            /**这里用相对路径 */
  background-size: cover;
}
</style>
