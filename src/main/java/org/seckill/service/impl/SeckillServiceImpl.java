package org.seckill.service.impl;

import org.seckill.dao.SeckillDao;
import org.seckill.dao.SuccessKilledDao;
import org.seckill.dao.cache.RedisDao;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.entity.SuccessKilled;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;


//@Component @Service @Dao @Controller
@Service
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //@Resource @Inject
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    @Autowired
    private RedisDao redisDao;

    //(混淆)md5盐值字符串,用于混淆MD5
    private final String slat = "da>;DS:D@Fa2^2d3Ds#$@%:>>cs:?SAD*&%%$";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    /**
     * 暴露秒杀地址
     * @param seckillId
     * @return
     *
     * 用redis做缓存 ——> 减少对数据库的访问量
     * 解决大量用户发送大量请求 ——>获取秒杀地址
     *
     */
    public Exposer exportSeckillUrl(long seckillId) {

        //优化：缓存优化——> 超时的基础上维护一致性
        //Seckill秒杀对象是不可变的 像秒杀记录有出错是直接废弃再新建而不是修改
        /**
         * get from cache
         * if null
         *  get db
         * else
         *  put cache
         * cgoin
         *
         */
        //1.访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //2.访问数据库
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            }else {
                //3.放入redis
                redisDao.putSeckill(seckill);
            }
        }

        System.out.println(seckill.toString() + "\n");

//        if (seckill == null) {
//            return new Exposer(false, seckillId);
//        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();

        //系统当前时间
        Date currentTime = new Date();


        //判断是否还没到秒杀时间或者是过了秒杀时间
//        LocalDateTime startTime = seckill.getStartTime();
//        LocalDateTime endTime = seckill.getEndTime();
//        LocalDateTime nowTime = LocalDateTime.now();

//        System.out.println(currentTime + "\n");
//
//        System.out.println(startTime+ "\n");
//
//        System.out.println(endTime);

        if (currentTime.getTime() < startTime.getTime()
                || currentTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, currentTime.getTime(), startTime.getTime(), endTime.getTime());

        }

        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    //转化成特定的字符串的过程,不可逆
    private String getMd5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    /**
     * 使用注解来控制事务的优点：
     * 1) 开发团队达成一致约定,明确标注事务方法的编程风格
     * 2) 保证事务方法的执行时间尽可能短,不要穿插其他网络操作RPC/HTTP风格 ---> 写入操作/抛出运行期异常则回滚事务 否则commit
     * 3) 不是所有的方法都需要事务,如只有一条修改操作,只读操作不需要事务操作
     */
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {


        System.out.println(seckillId+ " "+ userPhone + " "+md5);

        //为空或者不等于
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }

        //秒杀逻辑：减库存 + 记录购买行为
        Date currentTime = new Date();

        /**
         * 优化：减少行级锁的持有时间
         * 先insert——> update ——> 是否秒杀成功
         *
         *
         */
        try {
            //记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);

            if (insertCount <= 0) {
                //重复秒杀
                logger.warn("重复秒杀");
                throw new RepeatKillException("seckill repeated");
            } else {
                //减少库存
                int updateCount = seckillDao.reduceNumber(seckillId, currentTime);

                if (updateCount <= 0) {
                    //没有更新到记录,秒杀结束
                    logger.warn("没有更新数据库记录,说明秒杀结束");
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    //秒杀成功
                    //唯一：seckillId,userPhone
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, successKilled);
                }
            }

        } catch (SeckillCloseException e1) {
            //直接抛出
            throw e1;
        } catch (RepeatKillException e2) {
            //直接抛出
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new SeckillException("seckill inner error:" + e.getMessage());
        }
    }
}
