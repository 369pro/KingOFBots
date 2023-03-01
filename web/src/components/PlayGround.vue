<template>
    <div class="playground">        <!-- playground里面套了一层GameMap的样式-->
        <GameMap/>
    </div>
</template>


<script >
import GameMap from "./GameMap";
export default{
    components:{                    // components表示需要引入的组件
        GameMap
    }
}
</script>

<style scoped>
div.playground{
    width: 60vw;                    /**表示占用浏览器的宽度比例 */
    height: 70vh;
    margin: 50px auto;
}

</style>