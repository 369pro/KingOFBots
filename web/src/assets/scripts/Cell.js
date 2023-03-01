export class Cell
{
    constructor(r,c)
    {
        this.r = r;
        this.c = c;
        this.x = this.c + 0.5;
        this.y = this.r + 0.5;
    }
}