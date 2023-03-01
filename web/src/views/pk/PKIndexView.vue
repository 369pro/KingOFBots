<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
    <ResultBoard v-if="$store.state.pk.loser !='none'"/>
</template>


<script >
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import ResultBoard from '../../components/ResultBoard.vue'
import {onMounted, onUnmounted} from 'vue'
import {useStore} from 'vuex'
export default{
    components:{
        PlayGround,
        MatchGround,
        ResultBoard
    },
    setup(){
      const store = useStore();
      const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/`;
      store.commit("updateLoser", "none");     // 防止出现界面重叠情况
      let socket = null;
      onMounted(()=>{
        socket = new WebSocket(socketUrl);

        store.commit("updateOpponent",{
            username:"我的对手",
            photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
        })

        socket.onopen = ()=>{         // 链接成功建立调用的函数
          console.log("connected!");
          store.commit("updateSocket",socket);
        }

        socket.onmessage = msg=>{     // 从后端收到信息后调用的函数
          const data = JSON.parse(msg.data);
          if(data.event === "start-matching"){
            store.commit("updateOpponent",{  // 对手信息
              username:data.opponent_username,
              photo:data.opponent_photo,
            });
            setTimeout(()=>{
                store.commit("updateStatus","playing");
            }, 200);
            store.commit("updateGame",data.game);  // 更新游戏相关信息
          }else if(data.event === "move"){
            console.log(data);
              const game = store.state.pk.gameObject;
              const [snake0, snake1] = game.snakes;
              snake0.set_direction(data.a_direction);
              snake1.set_direction(data.b_direction);
          }else if(data.event === "result"){
            console.log(data);
            const game = store.state.pk.gameObject;
            const [snake0, snake1] = game.snakes;
            if(data.loser === "all" || data.loser === "A") snake0.status = "die";
            if(data.loser === "all" || data.loser === "B") snake1.status = "die";
            store.commit("updateLoser", data.loser);
          }
        }

        socket.onclose = ()=>{
          console.log("closed!");
        }
      });

      onUnmounted(()=>{
        socket.close();
        store.commit("updateStatus","matching");
      });
    }
}
</script>


<style scoped>
    
</style>