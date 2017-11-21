package com.fxrh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fxrh.service.UpdatePoolService;

@Component
@Order(1)
public class MyApplicationRunner implements ApplicationRunner{
	@Autowired
	UpdatePoolService updatePoolService;
	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("==start run UpdatePool===");
		
		updatePoolService.UpdatePool();
		
		System.out.println("==end run UpdatePool===");
	}

}
