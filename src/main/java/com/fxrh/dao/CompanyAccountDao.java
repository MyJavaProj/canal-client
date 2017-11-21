package com.fxrh.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import com.fxrh.entity.CompanyAccount;


public interface CompanyAccountDao extends CrudRepository<CompanyAccount, Integer>, JpaSpecificationExecutor<CompanyAccount>{

	public CompanyAccount findByAppKey(String appKey);
	
}
