package com.sogou.upd.passport.common;

import com.sogou.upd.passport.common.utils.RedisUtils;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: mayan
 * Date: 12-11-22
 * Time: 下午6:26
 * To change this template use File | Settings | File Templates.
 */
@ContextConfiguration(locations = {"classpath:spring-config-jredis.xml"})
public class JredisTest extends AbstractJUnit4SpringContextTests {

    @Inject
    private RedisUtils redisUtils;

    @Before
    public void init() {
    }

    @Test
    public void test() {
      try {
        redisUtils.set("aaaaa","bbbb");
//        redisUtils.expire("aaaaa",10);
      } catch (Exception e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }


    }
}