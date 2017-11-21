package com.fxrh.service.impl;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.fxrh.dao.CompanyAccountDao;
import com.fxrh.entity.CompanyAccount;
import com.fxrh.service.CompanyAccountService;
import com.fxrh.utils.Query;



@Service("companyAccountService")
public class CompanyAccountServiceImpl implements CompanyAccountService {
	
	@Autowired
	private CompanyAccountDao companyAccountDao;
	
	@Override
	public CompanyAccount queryObject(Integer id){
		return companyAccountDao.findOne(id);
	}
	
	@Override
	public Page<CompanyAccount> search(final Query query) {
		return companyAccountDao.findAll(new Specification<CompanyAccount>() {
			@Override
			public Predicate toPredicate(Root<CompanyAccount> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if(query.containsKey("appKey")){
					String appKey = query.get("appKey").toString();
		            if(appKey != null && !"".equals(appKey.trim())){
		            	 predicates.add(cb.like(root.get("appKey").as(String.class),"%"+appKey+"%")); 
		            } 
				}
				if(query.containsKey("id")){
					String id = query.get("id").toString();
					if(id != null && !"".equals(id.trim())){
						predicates.add(cb.like(root.get("id").as(String.class),"%"+id+"%")); 
					} 
				}
				return cq.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();  
			}
		}, query.getPageable());
	} 
	@Override
	public void save(CompanyAccount companyAccount){
		companyAccountDao.save(companyAccount);
	}
	
	@Override
	public void update(CompanyAccount companyAccount){
		companyAccountDao.save(companyAccount);
	}

	@Override
	public List<CompanyAccount> queryCompanyList() {
		return (List<CompanyAccount>) companyAccountDao.findAll();
	}
	
}
