
-- 数据库初始化脚本

-- 创建数据库
create database seckill;

-- 使用数据库
use seckill;


-- auto_increment

-- 创建秒杀库存表
create table seckill (
`seckill_id` bigint not null auto_increment comment '商品库存id',
`name` varchar(120) not null comment '商品名称',
`number` int not null comment '库存数量',
`start_time` timestamp not null comment '秒杀开始时间',
`end_time` timestamp not null comment '秒杀结束时间',
`create_time` timestamp not null default current_timestamp comment '创建时间',
primary key (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)engine=InnoDB AUTO_INCREMENT=1000 default charset=utf8 comment '秒杀库存表';

-- 初始化数据
insert into
    seckill(name,number,start_time,end_time)
values
    ('1000元秒杀iphone6', 100, '2019-09-07 00:00:00', '2019-09-08 00:00:00'),
    ('500元秒杀ipad2', 100, '2019-09-07 00:00:00', '2019-09-08 00:00:00'),
    ('200元秒杀mix2', 100, '2019-09-07 00:00:00', '2019-09-08 00:00:00'),
    ('120元秒杀note9', 100, '2019-09-07 00:00:00', '2019-09-08 00:00:00'),
    ('1200元秒杀macbook', 100, '2019-11-07 00:00:00', '2019-11-08 00:00:00');

-- 秒杀成功明细表
-- 用户登录认证相关信息
create table success_killed (
`seckill_id` bigint not null comment '秒杀商品id',
`user_phone` varchar(11) not null comment '用户手机号',
`state` tinyint not null default  -1 comment '状态标识：-1：无效 0:成功 1:已付款 2:已发货',
`create_time` timestamp not null comment '创建时间',
primary key (seckill_id, user_phone), /*联合主键*/
key idx_create_time(create_time)
)engine=InnoDB default charset=utf8 comment '秒杀成功明细表';

-- 连接数据库控制台
mysql -uroot -p


-- 手写DDL
-- 可以记录每次上线的DDL修改
-- 上线v1.1
alter table seckill
drop index idx_create_time,
add index idx_c_s(start_time, create_time);

-- 上线V1.2
-- dd1


