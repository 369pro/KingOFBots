import { AcGameObject } from "./AcGameObject";

export class Wall extends AcGameObject
{
    constructor(r, c, gamemap)
    {
        super();                        // 这句别忘了!!

        this.r = r;
        this.c = c;
        this.gamemap = gamemap;
        this.color = "#B37226"
    }
    update()
    {
        this.render();
    }
    render()
    {
        // this.gamemap.ctx.fillStyle = this.color;
        // this.gamemap.fillRect(this.c * this.gamemap.L, )          // 你TM不嫌烦呐这样写
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        ctx.fillRect(this.c * L, this.r * L, L, L);
    }
}