package com.fxrh.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.fxrh.entity.MobileNumber;


public interface MobileNumberDao extends CrudRepository<MobileNumber, Long>,JpaSpecificationExecutor<MobileNumber>{

	List<MobileNumber> findByCompanyId(Integer company_id);
	
	@Query("select mn.areaCode from MobileNumber mn where mn.companyId = ?1 and mn.type =?2 group by mn.areaCode") 
	List<String> groupArea(Integer company_id, Integer type);
	
	@Query("select mobile from MobileNumber mn where mn.companyId = ?1 and mn.type =?2 and areaCode = ?3") 
	Set<String> getNumberByArea(Integer company_id, Integer type, String areaCode);

	@Query(value="select * from mobile_number order by create_time desc limit ?1,?2",nativeQuery=true)
	List<MobileNumber> queryList(int pageIndex, int pageSize);
	
	/**
	 * 条件查询
	 * @param pageIndex
	 * @param pageSize
	 * @param where ：where 1=1
	 * @return
	 */
//	@Query(value="select * from mobile_number :#{#whereStr}",nativeQuery=true)
//	List<MobileNumber> searchList(String where);
	
	/**
	 * 删除号码
	 */
	@Transactional
	@Query(value="delete from mobile_number where mobile in ?1 and area_code = ?2 and company_id =?3 and type=?4 and disable=false",nativeQuery=true)
	@Modifying  
	int del(List<String> number, String area, String company_id, Integer type);
	
	/**
	 * 更新ax类型号码
	 * @param number
	 * @param area
	 * @param company_id
	 * @param type
	 */
	@Transactional
	@Query(value="update mobile_number set type=?4,bind_tel=null,bind_tel2=null where mobile in ?1 and area_code = ?2 and company_id =?3 and disable=false",nativeQuery=true)
	@Modifying  
	int updateType(List<String> number, String area, String company_id, Integer type);
	
	/**
	 * 冻结号码
	 * @param number
	 * @param area
	 * @param company_id
	 * @param type
	 */
	@Transactional
	@Query(value="update mobile_number set disable=true where mobile in ?1 and area_code = ?2 and company_id =?3 and disable=false",nativeQuery=true)
	@Modifying  
	int freezeNumber(List<String> number, String area, String company_id);
	
	/**
	 * 解冻号码
	 * @param number
	 * @param area
	 * @param company_id
	 * @param type
	 */
	@Transactional
	@Query(value="update mobile_number set disable=false where mobile in ?1 and area_code = ?2 and company_id =?3 and disable=true",nativeQuery=true)
	@Modifying  
	int unfreezeNumber(List<String> number, String area, String company_id);
	
	/**
	 * 回收号码
	 * @param number
	 * @param area
	 * @param company_id
	 * @param type
	 */
	@Transactional
	@Query(value="update mobile_number set company_id=null,type=null where mobile in ?1 and area_code = ?2 and disable=false",nativeQuery=true)
	@Modifying  
	int recoveryNumber(List<String> number, String area);
}
