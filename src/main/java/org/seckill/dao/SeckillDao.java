package org.seckill.dao;

import org.seckill.entity.Seckill;

import java.util.Date;
import java.util.List;

public interface SeckillDao {

    /**
     * 减少库存
     * @param seckillId
     * @param killTime
     * @return 影响行数>=1 更新
     */
    int reduceNumber(long seckillId, Date killTime);

    Seckill queryById(long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(int offset, int limit);


}
