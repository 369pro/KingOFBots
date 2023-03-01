const AC_GAME_OBJECTS = [];           // 定义全局变量
export class AcGameObject
{
    constructor()
    {
        AC_GAME_OBJECTS.push(this);
        this.timedelta = 0;
        this.has_called_start = false;
    }
    start()               // 第一次执行
    {

    }
    update()              // 之后每次执行一次
    {

    }
    on_destory()
    {

    }
    destory()
    {
        this.on_destory();
        for(let i in AC_GAME_OBJECTS)
        {
            const obj = AC_GAME_OBJECTS[i];
            if(this === obj) {
                AC_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }

}// class



let last_timestamp;
const step = (timestamp) =>{
    for(let obj of AC_GAME_OBJECTS)       // 每次刷新的时候遍历一遍所有对象(从前到后遍历),先push进去的先update
    {
        if(!obj.has_called_start)
        {
            obj.start();
            obj.has_called_start = true;
        }
        else{
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
            // last_timestamp = timestamp
        }
    }
    last_timestamp = timestamp;                        // 遍历完了所有物体才能更新
    requestAnimationFrame(step);
}
requestAnimationFrame(step);

