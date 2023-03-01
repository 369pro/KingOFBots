<template>
    <div ref = "parent" class="gamemap">    <!--ref = "xxx"表示让xxx对象指向div-->
        <canvas ref = "canvas" tabindex="0"></canvas>    <!--同理-->
    </div>
</template>

<script >
import {GameMap} from "../assets/scripts/GameMap";
import {ref, onMounted} from 'vue';
import {useStore} from 'vuex';
export default{                  // 这个就相当于把他导出去了
    setup(){                     // 入口函数
        let parent = ref(null);
        let canvas = ref(null);
        const store = useStore();  // 存着地图信息
        onMounted(()=>{            // 创建地图,然后地图开始刷新
            store.commit("updateGameObject",
                new GameMap(canvas.value.getContext('2d'), parent.value, store)
            );
    
        });

        return {parent,canvas};   // 返回之后才能在template里面用
    }
}
</script>


<style scoped>

div.gamemap{
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>