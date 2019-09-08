package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    /**
     * 第一次执行插入时(商品ID + 手机号)
     * insertCount = 1
     *
     * 第二次执行插入时：
     * insertCount = 0
     *
     * 原因:商品ID + 手机号组成的是联合主键 不能重复插入即可实现不能重复秒杀
     */
    @Test
    public void insertSuccessKilled() {
        long id = 1000L;
        long phone = 17783390667L;
        int insertCount = successKilledDao.insertSuccessKilled(id, phone);
        System.out.println("insertCount = "+insertCount);
    }

    @Test
    public void queryByIdWithSeckill() {
        long id = 1000L;
        long phone = 17783390667L;
        SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(id, phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }
}