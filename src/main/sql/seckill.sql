






-- 秒杀执行存储过程
delimiter $$ -- console ; 转换为 $$

-- 定义存储过程
-- 参数：in 输入函数; out 输出函数
-- row_count():返回上一条修改类型sql(delete|update|insert)的影响行数
-- row_count: 0:未修改数据|>0:表示修改的行数|<0:sql错误/未执行修改sql

create procedure `seckill`.`execute_seckill`
    (in v_seckill_id bigint, in v_phone bigint,
        in v_kill_time timestamp, out r_result int)
    begin
        declare insert_count int default 0;
        start transaction;
        insert ignore into success_killId
            (seckill_id, user_phone, create_time)
                values (v_seckill_id, v_phone, v_kill_time);
        select row_count() into insert_count;
        if (insert_count = 0) then
            rollback;
            set r_result = -1;
        elseif (insert_count < 0) then
            rollback;
            set r_result = -2;
        else
            update seckill
            set `number` = `number` - 1
            where seckill_id = v_seckill_id
                and end_time > v_kill_time
                and start_time < v_kill_time
                and `number` > 0;
            select row_count() into insert_count;
            if (insert_count = 0) then
                rollback;
                set r_result = -1;
            elseif (insert_count < 0) then
                rollback;
                set r_result = -2;
            else
                commit;
                set r_result = 1;
            end if;
        end if;
    end;
$$