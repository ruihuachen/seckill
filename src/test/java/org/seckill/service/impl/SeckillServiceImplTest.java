package org.seckill.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private SeckillService seckillService;


    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list = {}", list);
    }

    @Test
    public void getById() {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill = {}", seckill);

        //16:23:34.641 [main] INFO org.seckill.service.impl.SeckillServiceImplTest - seckill =
        // Seckill{seckill_id=0, name='1000元秒杀iphone6', number=100,
        // startTime=Sat Sep 07 13:00:00 CST 2019,
        // endTime=Sun Sep 08 13:00:00 CST 2019,
        // createTime=Sun Sep 08 06:53:30 CST 2019}
    }

    //注意可重复秒杀
    @Test
    public void exportSeckillLogic() {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);

        //是否开启秒杀
        if (exposer.isExposed()) {
            logger.info("exposer = {}", exposer);
            long phone = 18883390667L;
            String md5 = exposer.getMd5();

            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
                logger.info("seckillExecution = {}", seckillExecution);
            }catch (RepeatKillException e) {
                logger.error(e.getMessage());
            }catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            }
        }else {
            System.out.println("秒杀未开启");
            logger.warn("exposer = {}",exposer);
        }


        //16:30:31.323 [main] INFO org.seckill.service.impl.SeckillServiceImplTest -
        // exposer = Exposer{exposed=false, md5='null', seckillId=1000, now=1568795431323, start=1567832400000, end=1567918800000}

        //17:49:52.997 [main] INFO org.seckill.service.impl.SeckillServiceImplTest -
        // exposer = Exposer{exposed=true, md5='1055c550eea679ad2ec0ea78b23dcf71', seckillId=1000, now=0, start=0, end=0}
    }

//    @Test
//    public void executeSeckill() {
//        long id = 1000;
//        long phone = 18883390667L;
//        String md5 = "1055c550eea679ad2ec0ea78b23dcf71";
//
//        try {
//            SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
//            logger.info("seckillExecution = {}", seckillExecution);
//        }catch (RepeatKillException e) {
//            logger.error(e.getMessage());
//        }catch (SeckillCloseException e) {
//            logger.error(e.getMessage());
//        }

        //18:00:29.771 [main] INFO org.seckill.service.impl.SeckillServiceImplTest -
        // seckillExecution = SeckillExecution{seckillId=1000, state=1, stateInfo='秒杀成功',

        // successKilled=SuccessKilled{seckillId=1000, userPhone=18883390667, state=0,
        // createTime=Thu Sep 19 07:00:29 CST 2019,

        // seckill=Seckill{seckill_id=1000, name='1000元秒杀iphone6', number=99,
        // startTime=Thu Sep 19 07:00:29 CST 2019, endTime=Fri Sep 20 13:00:00 CST 2019, createTime=Sun Sep 08 06:53:30 CST 2019}}}
//    }
}