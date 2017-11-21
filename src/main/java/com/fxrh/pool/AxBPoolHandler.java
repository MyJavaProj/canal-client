package com.fxrh.pool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fxrh.config.LogUtils;
import com.fxrh.config.LoggerUtilization;
import com.fxrh.dao.AxbBindRepository;
import com.fxrh.dao.MobileNumberDao;
import com.fxrh.entity.AxBBind;
import com.mongodb.BasicDBObject;

@Service
public class AxBPoolHandler {
	
	static Logger logger = LogManager.getLogger(AxBPoolHandler.class);
	static Logger log_utilization= LogManager.getLogger(LoggerUtilization.class);
	
	@Autowired RedisTemplate<String, Object> redisTemplate;
	@Autowired MobileNumberDao mobileNumberDao;
	@Autowired AxbBindRepository axbBindRepository;
	@Autowired MongoTemplate mongoTemplate;
	/**
	 * 初始化企业已开通号码地区
	 * @param company_id 企业ID
	 */
	public void initCompanyArea(Integer company_id){
		//1.查询号码表，条件：企业ID、号码类型为 axb模式，按照地区分组，获取企业有号码的地区
		//2.先清除key,然后插入集合
		logger.info(LogUtils.getLogString(company_id,"====初始化企业已开通号码地区"));
		List<String> areaList = mobileNumberDao.groupArea(company_id,1);
		logger.info(company_id+"====已开通号码地区:"+areaList);
		String key = RedisKeyPrefix.getAxbCompanyAreaKey(company_id.toString());
		redisTemplate.delete(key);
		Long add = redisTemplate.opsForSet().add(key, areaList.toArray());
		logger.info(company_id+"====已开通号码地区个数:"+add);
	}
	
	/**
	 * 初始化企业已开通号码地区全量池
	 * @param company_id 企业ID
	 * @param area_code 地区
	 */
	public void initCompanyAreaFullNnmberPool(Integer company_id, String area_code){
		//1.查询号码表，条件：企业ID、号码类型为 axb模式、地区，
		//2.先清除key,然后插入集合
		logger.info(LogUtils.getLogString(company_id," ",area_code,"====初始化企业已开通号码地区全量池"));
		Set<String> numberList = mobileNumberDao.getNumberByArea(company_id, 1, area_code);
		String[] array = new String[numberList.size()];
		Iterator<String> it = numberList.iterator();
		int i = 0;
		while (it.hasNext()) {
			array[i] = String.valueOf(it.next());
			i++;
		}
 		String key = RedisKeyPrefix.getAxbCompanyAreaNnmberPoolKey(company_id.toString(), area_code);
		redisTemplate.delete(key);
		Long add = redisTemplate.opsForSet().add(key, array);
		logger.info(LogUtils.getLogString(company_id," ",area_code,"====已开通号码个数:",add));
	}
	
	
	/**
	 * 使用率
	 * @return
	 */
	public JSONArray utilization(String company_id){
		Set<Object> set = redisTemplate.opsForSet().members(RedisKeyPrefix.getAxbCompanyAreaKey(company_id));
		String area_code = "";
		JSONArray array = new JSONArray();
		JSONObject obj = null;
		Long fullNumberSize = 0l;
		Iterator<Object> cursor = set.iterator();
		while(cursor.hasNext()){
			area_code = (String)cursor.next();
			float utilization = 0;
			fullNumberSize = redisTemplate.opsForSet().size(RedisKeyPrefix.getAxbCompanyAreaNnmberPoolKey(company_id, area_code));
			TypedAggregation<AxBBind> agg = Aggregation.newAggregation(
				AxBBind.class,
				Aggregation.match(Criteria.where("companyid").is(company_id).and("area").is(area_code)),
	            Aggregation.group("companyid","area","tel").count().as("count"),
	            Aggregation.sort(Sort.Direction.DESC, "count"),
	            Aggregation.limit(5)
		    );
	    	AggregationResults<BasicDBObject> result = mongoTemplate.aggregate(agg, BasicDBObject.class);
//	    	System.out.println(agg.toString());
	    	//System.out.println(result.getMappedResults());
	    	int i = 0;
	    	Integer count=0;
	    	for(BasicDBObject bdbobj:result){
	    		if(i == 0){
	    			count = bdbobj.getInt("count");
	    			BigDecimal bg = new BigDecimal(String.valueOf(count));
	    			bg = bg.divide(new BigDecimal(fullNumberSize), 3, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
	    			utilization = bg.floatValue();
	    		}
	    		i++;
	    		logger.debug(LogUtils.getLogString("AxBCompanyAreaTelUsedMaximum ",i," size:",fullNumberSize," ",bdbobj.get("companyid")," ",bdbobj.get("area")," ",bdbobj.get("tel")," ",bdbobj.get("count")));
	    	}
			obj = new JSONObject();
			obj.put("area_code", area_code);
			obj.put("utilization", utilization>100?100:utilization);
			array.add(obj);
			//axb监控日志
			log_utilization.info(LogUtils.getLogString("|",company_id,"|AXB","|",area_code,"|",obj.get("utilization"),"|",count,"|",fullNumberSize));
		}
		return array;
	}
	
}
