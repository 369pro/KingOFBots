<template>
    <div class="container">
        <div class="row">
            <div class="col-3">
                <div class="card" style="margin-top:20px;">
                    <img :src="$store.state.user.photo"  alt="" style="20%">
                </div>
            </div>
            <div class="col-9">
                <div class="card" style="margin-top:20px;">
                    <div class="card-header">
                        <span style = "font-size: 130%">我的Bot</span>
                        <!-- Button trigger modal -->
                        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                            创建Bot
                        </button>

                        <!-- Modal -->
                        <div class="modal fade" id="add-bot-btn" tabindex="-1" >
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h1 class="modal-title">创建Bot</h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label for="add-bot-title" class="form-label">Bot的名称</label>
                                            <input v-model = botadd.title type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                        </div>
                                    </div>
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label for="add-bot-description" class="form-label">Bot的描述</label>
                                            <textarea v-model = botadd.description type="text" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot的描述"></textarea>
                                        </div>
                                    </div>
                                    <div class="modal-body">
                                        <VAceEditor
                                        v-model:value="botadd.content"
                                        @init="editorInit"
                                        lang="c_cpp"
                                        theme="textmate"
                                        :options="{fontSize: 16}" 
                                        style="height: 300px" />
                                    </div>
                                    <div class="modal-footer">
                                        <div class="error_message">{{botadd.error_message}}</div>
                                        <button type="button" class="btn btn-primary" @click="add_bot">创建</button>
                                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <td>名称</td>
                                    <td>创建时间</td>
                                    <td>操作</td>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{bot.title}}</td>
                                    <td>{{bot.createtime}}</td>
                                    <td>
                                        <!-- Button trigger modal -->
                                        <button type="button" class="btn btn-secondary" data-bs-toggle="modal" :data-bs-target="'#update-bot-btn-'+bot.id" style="margin-right: 10px;">
                                            修改
                                        </button>
                                        <!-- Modal -->
                                            <div class="modal fade" :id="'update-bot-btn-'+bot.id" tabindex="-1" >
                                                <div class="modal-dialog modal-xl">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <h1 class="modal-title">修改Bot</h1>
                                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <div class="mb-3">
                                                                <label for="add-bot-title" class="form-label">Bot的名称</label>
                                                                <input v-model = bot.title type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                                            </div>
                                                        </div>
                                                        <div class="modal-body">
                                                            <div class="mb-3">
                                                                <label for="add-bot-description" class="form-label">Bot的描述</label>
                                                                <textarea v-model = bot.description type="text" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot的描述"></textarea>
                                                            </div>
                                                        </div>
                                                        <div class="modal-body">
                                                            <VAceEditor
                                                                v-model:value="bot.content"
                                                                @init="editorInit"
                                                                lang="c_cpp"
                                                                theme="textmate"
                                                                :options="{fontSize: 16}"   
                                                                style="height: 300px" />
                                                        </div>
                                                        <div class="modal-footer">
                                                            <div class="error_message">{{botadd.error_message}}</div>
                                                            <button type="button" class="btn btn-primary" @click="update_bot(bot)">更新</button>
                                                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">删除</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>


<script >
import { useStore } from 'vuex';
import {ref, reactive} from 'vue';
import { Modal } from 'bootstrap/dist/js/bootstrap';
import $ from 'jquery';
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';
export default{
    components:{
        VAceEditor
    },
    setup(){
        ace.config.set(
        "basePath", "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/")
        const store = useStore();
        let bots = ref([]);
        const botadd = reactive({         // 类似ref,不过是绑定对象
            title:"",                     // **等号改成冒号
            description:"",
            content:"",
            error_message:""           
        });
        const add_bot = ()=>{
            botadd.error_message = "",
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/add/",
                type:"post",
                data:{
                    title:botadd.title,
                    description:botadd.description,
                    content:botadd.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    if(resp.error_message === "success"){
                        Modal.getInstance("#add-bot-btn").hide();
                        refresh_bots();
                    }else{
                        botadd.error_message = resp.error_message; 
                    }
                }
            })
        }
        const remove_bot = (bot) => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/remove/",
                type: "post",
                data: {
                    bot_id: bot.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        refresh_bots();
                    }
                }
            })
        }

        const update_bot = (bot)=>{
            botadd.error_message = "";
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/update/",
                type:"post",
                data:{
                    bot_id:bot.id,
                    title:bot.title,
                    description:bot.description,
                    content:bot.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    if(resp.error_message === "success"){
                        Modal.getInstance('#update-bot-btn-'+bot.id).hide(); //第一个是'',每个不同的bot各占不同的模态框
                        refresh_bots();
                    }else{
                        botadd.error_message = resp.error_message; 
                    }
                }
            })
        }
        const refresh_bots = ()=>{
            $.ajax({
                url:"http://127.0.0.1:3000/user/bot/getlist/",
                type:"get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp){
                    bots.value = resp;
                    console.log(bots);
                }
            })
        }
        refresh_bots();

        return{
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot
        }
        
    }

}
</script>


<style scoped>
div.error_message{
    color: red;
}
</style>