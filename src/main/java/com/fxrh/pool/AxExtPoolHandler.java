package com.fxrh.pool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fxrh.config.LogUtils;
import com.fxrh.config.LoggerUtilization;
import com.fxrh.dao.AxExtBindRepository;
import com.fxrh.dao.MobileNumberDao;
import com.fxrh.entity.AxExtBind;

@Service
public class AxExtPoolHandler {
	
	static Logger logger = LogManager.getLogger(AxBPoolHandler.class);
	static Logger log_utilization= LogManager.getLogger(LoggerUtilization.class);
	
	@Autowired RedisTemplate<String, Object> redisTemplate;
	@Autowired MobileNumberDao mobileNumberDao;
	@Autowired AxExtBindRepository axExtBindRepository;
	
	/**
	 * 初始化分机号种子集合
	 */
	public void initExtPool(int startExt, int endExt){
		int length = endExt-startExt;
		Object[] exts = new String[length];
		int index = 0;
		for (int i = startExt; i < endExt; i++) {
			exts[index] = String.valueOf(i);
			index ++ ;
		}
		logger.info("初始化分机号种子集合");
		//初始化分机号种子集合
//		Long add = redisTemplate.opsForSet().add(RedisKeyPrefix.getAxExtSetKey(), exts);
//		System.out.println(add);
//		logger.info("初始化分机号种子集合个数："+add);
	}
	
	/**
	 * 初始化企业已开通号码地区池
	 * @param company_id 企业ID
	 */
	public void initCompanyArea(Integer company_id){
		//1.查询号码表，条件：企业ID、号码类型为 ax分机号模式，按照地区分组，获取企业有号码的地区
		//2.先清除key,然后插入集合
		logger.info(LogUtils.getLogString(company_id,"===初始化企业已开通号码地区"));
		List<String> areaList = mobileNumberDao.groupArea(company_id,3);
		logger.info(LogUtils.getLogString(company_id,"===初始化企业已开通号码地区:",areaList));
		String key = RedisKeyPrefix.getAxExtCompanyAreaKey(company_id.toString());
		redisTemplate.delete(key);
		Long add = redisTemplate.opsForSet().add(key, areaList.toArray());
		logger.info(LogUtils.getLogString(company_id,"===初始化企业已开通号码地区个数:",add));
	}
	
	/**
	 * 初始化企业已开通号码地区全量池
	 * @param company_id 企业ID
	 * @param area_code 地区
	 */
	@Autowired(required = false)
	public void initCompanyAreaFullNnmberPool(Integer company_id, String area_code){
		//1.查询号码表，条件：企业ID、号码类型为 ax分机号模式、地区，
		//2.先清除key,然后插入集合
		logger.info(LogUtils.getLogString(company_id,"===初始化企业已开通号码地区全量池"));
		String key = RedisKeyPrefix.getAxExtCompanyAreaFullNnmberPoolKey(company_id.toString(), area_code);
		String key0 = RedisKeyPrefix.getAxExtCompanyAreaUsableNnmberPoolKey(company_id.toString(), area_code);
		Set<String> list = mobileNumberDao.getNumberByArea(company_id, 3, area_code);
		if(list == null || list.size()==0){
			redisTemplate.delete(key);
			redisTemplate.delete(key0);
			return;
		}
		Object[] exts = new String[9000];
		int index = 0;
		for (int i = 1000; i < 10000; i++) {
			exts[index] = String.valueOf(i);
			index ++ ;
		}
		Set<Object> set = redisTemplate.opsForSet().members(key);
		Set<Object> numberList = new HashSet<Object>(); 
		numberList.clear();  
		numberList.addAll(list);  
		numberList.removeAll(set);
		System.out.println(set.iterator().next().getClass());
        System.out.println(LogUtils.getLogString("增量差集：", numberList)); 
        
		logger.info(LogUtils.getLogString(company_id,"===增量：",numberList.size()));
		String[] array = new String[numberList.size()];
		Iterator<Object> it = numberList.iterator();
		int i = 0;
		while (it.hasNext()) {
			String tel_x = String.valueOf(it.next());
			array[i] = tel_x;
			i++;
			redisTemplate.opsForSet().add(RedisKeyPrefix.getAxExtCompanyUsableKey(company_id.toString(), tel_x), exts);
		}
		
		Long add = redisTemplate.opsForSet().add(key, array);
		redisTemplate.opsForSet().add(key0, array);
		logger.info(LogUtils.getLogString(company_id,"===增加个数：",add));
		
		Set<Object> sub_set = new HashSet<Object>();
		Set<Object> set1 = redisTemplate.opsForSet().members(key);
		sub_set.addAll(set1);
		System.out.println(set1.iterator().next().getClass());
		sub_set.removeAll(list);//减量
		if(sub_set.size() > 0){
			redisTemplate.opsForSet().remove(key0, sub_set.toArray());
			redisTemplate.opsForSet().remove(key, sub_set.toArray());
		}
	}
	
	/**
	 * X分机号回收 
	 */
	public void recycleAxExt(){
		Long time = System.currentTimeMillis()/1000;
		Set<AxExtBind> set = axExtBindRepository.findByExpireLessThanEqual(time);
		Iterator<AxExtBind> it = set.iterator();
		AxExtBind ax_ext = null;
		logger.debug(LogUtils.getLogString("===回收Ax_Ext数量：",set.size()));
		while (it.hasNext()) {
			ax_ext = it.next();
			String company_id = String.valueOf(ax_ext.getCompany_id());
			logger.debug(LogUtils.getLogString("===回收Ax_Ext：",company_id," ",ax_ext.getTel_x()," ",ax_ext.getTel_x_ext()));
			//分机号回收
			redisTemplate.opsForSet().add(RedisKeyPrefix.getAxExtCompanyUsableKey(company_id ,ax_ext.getTel_x()), ax_ext.getTel_x_ext());
			//可用号码池回收虚号X 
			redisTemplate.opsForSet().add(RedisKeyPrefix.getAxExtCompanyAreaUsableNnmberPoolKey(company_id ,ax_ext.getArea()), ax_ext.getTel_x());
			//企业地区分机号使用个数减1
			redisTemplate.opsForValue().increment(RedisKeyPrefix.getAxExtCompanyAreaMaximumKey(company_id, ax_ext.getArea()), -1L);
			axExtBindRepository.delete(ax_ext.getId());
		}
	}
	
	
	
	//分机号使用率
	public JSONArray utilization(String company_id){
		Set<Object> set = redisTemplate.opsForSet().members(RedisKeyPrefix.getAxExtCompanyAreaKey(company_id));
		Long extSize = 9000L;//
		JSONArray array = new JSONArray();
		float utilization = 0;
		Iterator<Object> cursor = set.iterator();
		String area_code = "";
		String usedCount = null;
		Long fullNumSize = null;
		JSONObject obj = null;
		StringBuffer logBuf = null;
		while(cursor.hasNext()){
			logBuf = new StringBuffer("");
			area_code = (String) cursor.next();
			usedCount = (String)redisTemplate.opsForValue().get(RedisKeyPrefix.getAxExtCompanyAreaMaximumKey(company_id, area_code));
			fullNumSize = redisTemplate.opsForSet().size(RedisKeyPrefix.getAxExtCompanyAreaFullNnmberPoolKey(company_id, area_code));
			if(fullNumSize == 0
					|| usedCount==null || "0".equals(usedCount) || "".equals(usedCount.trim())){
				utilization = 0;
			}else{
				utilization = count(usedCount,extSize.toString(),fullNumSize.toString());
			}
			logger.debug(LogUtils.getLogString("AxExtCompanyAreaMaximumKey ",company_id,".",area_code,",fullSize:",fullNumSize,",extSize:",extSize,",usedCount：",usedCount,",rate:",utilization));
			obj = new JSONObject();
			obj.put("area_code", area_code);
			obj.put("utilization", utilization>100?100:utilization);
			array.add(obj);
			
			//ax监控日志
			log_utilization.info(LogUtils.getLogString("|",company_id,"|AX","|",area_code,"|",obj.get("utilization"),"|",usedCount,"|",fullNumSize,"|",extSize));
		}
		return array;
	}
	
	private float count(String usedCount,String extSize,String fullNumberSize){
		BigDecimal bg = new BigDecimal(usedCount);
		BigDecimal mul = new BigDecimal(extSize);
		mul = mul.multiply(new BigDecimal(fullNumberSize));
		bg = bg.divide(mul, 10, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(1, RoundingMode.HALF_UP);
		return bg.floatValue();
	}
}
