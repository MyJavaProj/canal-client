package com.fxrh.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fxrh.entity.AxBBind;


public interface AxbBindRepository extends MongoRepository<AxBBind, String>{
	
	public AxBBind findByCompanyidAndAreaAndTel(String company_id, String area, String tel);
	
}
