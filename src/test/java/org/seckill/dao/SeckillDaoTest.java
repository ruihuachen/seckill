package org.seckill.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.entity.Seckill;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * 配置Spring和junit整合,junit启动的时加载SpringIOC容器
 * spring-test, junit
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring配置文件
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SeckillDaoTest {

    //注入Dao实现类依赖
    @Resource
    private SeckillDao seckillDao;

    @Test
    public void reduceNumber() {
//        long id = 1000;
//        LocalDateTime localDateTime = LocalDateTime.now();
        Date killTime = new Date();
        int change = seckillDao.reduceNumber(1000L, killTime);
        System.out.println(change);
    }

    @Test
    public void queryById() {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);

    }

    @Test
    public void queryAll() {
        List<Seckill> seckills = seckillDao.queryAll(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }

    }
}