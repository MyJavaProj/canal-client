package com.fxrh.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fxrh.entity.CompanyAccount;
import com.fxrh.utils.Query;

/**
 * 
 * 
 * @author cf
 * @email 100@qq.com
 * @date 2017-08-25 09:30:24
 */
public interface CompanyAccountService {
	
	CompanyAccount queryObject(Integer id);
	public Page<CompanyAccount> search(Query query);
//	List<CompanyAccount> queryList(Map<String, Object> map);
	
//	int queryTotal(Map<String, Object> map);
	
	void save(CompanyAccount companyAccount)	;
	
	void update(CompanyAccount companyAccount);
 
	List<CompanyAccount> queryCompanyList();
	
	
}
