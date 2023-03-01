import { AcGameObject} from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject
{
    constructor(info, gamemap)          // info是一个对象,表示蛇类信息
    {
        super();
        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)];
        this.next_cell = null;         // 下一步的位置

        this.direction = -1;           // 0 1 2 3分别表示上右下左
        this.status = "idle"           // move, die
        this.speed = 5;              // 每秒走几格
        this.steps = 0;                // 回合数
        this.eps = 1e-2;

        this.dr = [-1,0,1,0];
        this.dc = [0,1,0,-1];

        this.eye_direction = 0;
        if(this.id === 1) this.eye_direction = 2;  // 右上角初始朝下
        this.eye_dx = [  // 蛇眼睛不同方向的x的偏移量
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eye_dy = [  // 蛇眼睛不同方向的y的偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ]
    }
    start()
    {

    }

    check_tail_increasing()           // 本前端项目带check的都是bool函数
    {
        if(this.steps <= 6) return true;
        if(this.steps % 3 === 0) return true;
        return false;
    }
    set_direction(d)
    {
        this.direction = d;
    }
    next_step()           // 设置下一步状态啥的
    {
        this.status = "move";
        const d = this.direction;
        this.direction = -1;       // 清空方向
        this.steps++;

        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d;
        const len = this.cells.length;  // 拷贝构造,此处不能创建cells[0]不然后面没法做动画效果
        for(let i = len; i > 0; i--) this.cells[i] = JSON.parse(JSON.stringify(this.cells[i-1]));
    }

    update_move()        // 设置移动的具体坐标
    {
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx*dx + dy*dy);
        if(distance < this.eps)
        {
            this.status = "idle";
            this.cells[0] = this.next_cell;
            this.next_cell = null;

            if(!this.check_tail_increasing()) this.cells.pop();

        }else{
            const move_distance = this.speed * this.timedelta / 1000; 
            this.cells[0].x += move_distance * dx / distance;            // 蛇动画效果
            this.cells[0].y += move_distance * dy / distance;

            if(!this.check_tail_increasing())          // 蛇尾不增加,即蛇尾要动
            {
                const len = this.cells.length;
                const tail = this.cells[len - 1];
                const target = this.cells[len - 2];
                const tail_dx = target.x - tail.x;
                const tail_dy = target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;     // 移动的距离是一样的，但是方向可能不一样 
                tail.y += move_distance * tail_dy / distance;

            }
        }
    }
    update()
    {
        if(this.status === "move") this.update_move();
        this.render();
    }
    render()  // 每帧渲染
    {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;
        ctx.fillStyle = this.color;
        if(this.status === "die"){
            ctx.fillStyle = "white";
        }

        for(const cell of this.cells)
        {
            ctx.beginPath();
            ctx.arc(cell.x*L, cell.y*L, L/2 * 0.8, 0, Math.PI*2);
            ctx.fill();
        }

        for(let i = 1; i < this.cells.length; i++)
        {
            const a = this.cells[i-1], b = this.cells[i];
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)
                continue;
            if(Math.abs(a.x - b.x) < this.eps)  // 两个球在同一列
            {                                                       // 圆改半径后,两个圆心相对距离自然会变
                ctx.fillRect((a.x-0.4)*L, Math.min(a.y, b.y) * L, L*0.8, Math.abs(a.y-b.y) * L);
            }else{ //两个球在同一行 
                ctx.fillRect(Math.min(a.x, b.x)*L, (a.y-0.4)*L, Math.abs(a.x-b.x) * L, L*0.8);
            }
        }
        // 画眼睛
        ctx.fillStyle = "black";
        for (let i = 0; i < 2; i ++ ) {
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.15) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.15) * L;

            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.05, 0, Math.PI * 2);
            ctx.fill();
        }
    }
}