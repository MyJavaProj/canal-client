package com.fxrh.dao;

import java.util.Set;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fxrh.entity.AxExtBind;


public interface AxExtBindRepository extends MongoRepository<AxExtBind, String>{
	
	//{expire:{$lte:222} 小于等于
	public Set<AxExtBind> findByExpireLessThanEqual(Long expire);
	
}
