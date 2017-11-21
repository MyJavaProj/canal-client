package com.fxrh;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.fxrh.dao.CompanyAccountDao;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CanalClientApplicationTests {
	@Autowired CompanyAccountDao companyAccountDao;
	@Autowired RedisTemplate<String, Object> redisTemplate;
	@Test
	public void contextLoads() {
		System.out.println("===1====");
		String a = (String)redisTemplate.opsForValue().get("");
//		int a  = (int)companyAccountDao.count();
		
		System.out.println("===1====:" + a);
	}

}
