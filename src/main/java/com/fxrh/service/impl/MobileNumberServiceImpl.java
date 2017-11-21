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

import com.fxrh.dao.MobileNumberDao;
import com.fxrh.entity.MobileNumber;
import com.fxrh.service.MobileNumberService;
import com.fxrh.utils.Query;


@Service("mobileNumberService")
public class MobileNumberServiceImpl implements MobileNumberService {

	@Autowired MobileNumberDao mobileNumberDao;
	
	@Override
	public MobileNumber findById(Long number) {
		return mobileNumberDao.findOne(number);
	}

	@Override
	public List<MobileNumber> queryList(Query query) {
		int pageSize = query.getLimit();
        int pageIndex = query.getPage();
		return mobileNumberDao.queryList((pageIndex-1)*pageSize,pageSize);
	}

	@Override
	public int queryCount() {
		Long count = mobileNumberDao.count();
		return Integer.valueOf(count.toString());
	}
	@Override
	public Page<MobileNumber> searchKey(final Query query) {
		return mobileNumberDao.findAll(new Specification<MobileNumber>() {
			@Override
			public Predicate toPredicate(Root<MobileNumber> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if(query.containsKey("mobile")){
					String mobile = query.get("mobile").toString();
		            if(mobile != null && !"".equals(mobile.trim())){
		            	 predicates.add(cb.like(root.get("mobile").as(String.class),"%"+mobile+"%")); 
		            } 
				}
				if(query.containsKey("companyId")){
					String companyId = query.get("companyId").toString();
					if(companyId != null && !"".equals(companyId.trim())){
						predicates.add(cb.like(root.get("companyId").as(String.class),"%"+companyId+"%")); 
					} 
				}
				return cq.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();  
			}
		}, query.getPageable());
	}
	

}
