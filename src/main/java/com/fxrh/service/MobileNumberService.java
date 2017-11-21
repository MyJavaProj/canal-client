package com.fxrh.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fxrh.entity.MobileNumber;
import com.fxrh.utils.Query;


public interface MobileNumberService {
	
	public MobileNumber findById(Long number);

	public List<MobileNumber> queryList(Query query);
	
	public int queryCount();
	
	public Page<MobileNumber> searchKey(Query query);
	
	 
}
