import { AcGameObject } from "./AcGameObject";
import { Snake } from "./Snake";
import {Wall} from "./Wall";

export class GameMap extends AcGameObject
{
    constructor(ctx, parent, store)
    {
        super();
        this.ctx = ctx;
        this.parent = parent;
        this.store = store;
        this.L = 0;                 // 基本小正方形的边长(绝对距离)

        this.rows = 13;
        this.cols = 14;
        this.inner_wall_cnt = 20;
        this.walls = [];           // 保存创建好了的墙的信息

        this.snakes = [
            new Snake({id:0, color:"#4876EC", r:this.rows-2, c:1}, this),
            new Snake({id:1, color:"#F94848", r:1, c:this.cols-2}, this)
        ]
    }

    start()
    {
        this.create_wall();
        this.add_listening_events();          // GameMap一开始就监听
    }
    add_listening_events()
    {
        this.ctx.canvas.focus();
        this.ctx.canvas.addEventListener("keydown", e =>{
            let d = -1;
            if(e.key === 'w') d = 0;
            else if(e.key === 'd') d = 1;
            else if(e.key === 's') d = 2;
            else if(e.key === 'a') d = 3;
            else if(e.key === 'ArrowUp') d = 0;
            else if(e.key === 'ArrowRight') d = 1;
            else if(e.key === 'ArrowDown') d = 2;
            else if(e.key === 'ArrowLeft') d = 3;

            if(d >= 0){
                this.store.state.pk.socket.send(JSON.stringify({
                    event:"move",
                    direction:d,
                }));
            }
        });
    }

    
    create_wall()
    {
        const g = this.store.state.pk.gamemap;
        for(let r = 0; r < this.rows; ++r){
            for(let c = 0; c < this.cols; ++c)
            {
                if(g[r][c]) this.walls.push(new Wall(r,c,this));
            }
        }
    }

    update_size()
    {
        // 边长转成整型
        this.L = parseInt(Math.min(this.parent.clientWidth/this.cols, this.parent.clientHeight/this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }
    next_step()
    {
        for(const snake of this.snakes) snake.next_step();
    }
    check_ready()       // 看蛇蛇是否准备好
    {
        for(const snake of this.snakes)   //所有蛇静止且方向不为-1(即有输入)时判定为准备好
        {
            if(snake.direction === -1) return false;
            if(snake.status !== "idle") return false;
        }
        return true;
    }
    render()
    {
        const color_even = "#AAD751", color_odd = "#A2D149";
        for(let r = 0; r < this.rows; r++)
        {
            for(let c = 0; c < this.cols; c++)
            {
                if((r + c) % 2 == 0) this.ctx.fillStyle = color_even;     // 铺瓷砖,颜色相间隔
                else this.ctx.fillStyle = color_odd;
                this.ctx.fillRect(c*this.L, r*this.L, this.L, this.L);
            }
        }
    }

    update()
    {
        this.update_size();
        if(this.check_ready()) this.next_step();
        this.render();
    }
}