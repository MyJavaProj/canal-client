package com.fxrh.pool;

public class RedisKeyPrefix {

	/**
	 * 虚号属性
	 * @param mobile
	 * @return
	 */
	public static String getMobileNumberKey(String mobile){
		return "mobile.snapshot."+mobile;
	}
	/**
	 * AXB
	 * 企业已开通号码地区 KEY
	 * @param company_id
	 * @return
	 */
	public static String getAxbCompanyAreaKey(String company_id){
		return "axb.com."+company_id+".area";
	}
	
	/**
	 * AXB
	 * 企业地区号码池
	 * @param company_id 企业ID
	 * @param area_code 地区编码 ： 010
	 * @return
	 */
	public static String getAxbCompanyAreaNnmberPoolKey(String company_id, String area_code){
		return "axb.com."+company_id+"."+area_code+".pool";
	}
	
	/**
	 * AXB
	 * 企业请求绑定关系
	 * @param company_id 企业ID
	 * @param banding_id 绑定ID
	 * @return
	 */
	public static String getAxbCompanyReqKey(String company_id, String request_id){
		return "axb.com."+company_id+".req."+request_id;
	}
	
	/**
	 * AXB
	 * 企业请求绑定关系
	 * @param company_id 企业ID
	 * @param bind_id 绑定ID
	 * @return
	 */
	public static String getAxbCompanyBindKey(String company_id, String bind_id){
		return "axb.com."+company_id+".bind."+bind_id;
	}
	
	/**
	 * AXB
	 * 绑定关系(A_X或者B_X作为key)
	 * @param tel_x 虚号
	 * @param tel 主叫
	 * @return
	 */
	public static String getAxbCompanyRelationKey(String company_id, String tel_x, String tel){
		return "{axb.com."+company_id+".rel."+tel_x+"}."+tel;//{} 表示集群 数据保存在相同节点
	}
	
	/**
	 * AXB
	 * 企业号码使用率
	 * @param company_id
	 * @return
	 */
	public static String getAxbUtilizationKey(String company_id){
		return "axb.com."+company_id+".utilization";
	}
	
	/**
	 * AX分机号 
	 * 企业已开通号码地区
	 * @param company_id
	 * @return
	 */
	public static String getAxExtCompanyAreaKey(String company_id){
		return "axext.com."+company_id+".area";
	}
	
	/**
	 * AX分机号
	 * 企业地区全量号码池
	 * @param company_id
	 * @param area_code
	 * @return
	 */
	public static String getAxExtCompanyAreaFullNnmberPoolKey(String company_id, String area_code){
		return "{axext.com."+company_id+"."+area_code+"}.pool";
	}
	
	/**
	 * AX分机号
	 * 企业地区可用(有可用分机号)号码池
	 * @param company_id
	 * @param area_code
	 * @return
	 */
	public static String getAxExtCompanyAreaUsableNnmberPoolKey(String company_id, String area_code){
		return "{axext.com."+company_id+"."+area_code+"}.usablePool";
	}
	
	/**
	 * AX分机号
	 * X号码可使用的分机号集合
	 * @param tel_x 虚号
	 * @return
	 */
	public static String getAxExtCompanyUsableKey(String company_id, String tel_x){
		return "axext.com."+company_id+".usable."+tel_x;
	}
	
	/**
	 * AX分机号
	 * 企业请求
	 * @param company_id 企业ID
	 * @param request_id 
	 * @return
	 */
	public static String getAxExtCompanyReqKey(String company_id, String request_id){
		return "axext.com."+company_id+".req."+request_id;
	}
	
	/**
	 * AX分机号
	 * 企业绑定关系
	 * @param company_id 企业ID
	 * @param bind_id 
	 * @return
	 */
	public static String getAxExtCompanyBindingKey(String company_id, String bind_id){
		return "axext.com."+company_id+".bind."+bind_id;
	}
	
	
	/**
	 * AX分机号
	 * 虚号和分机号的绑定关系
	 * @param tel_x 虚号
	 * @param ext 分机号
	 * @return
	 */
	public static String getAxExtCompanyRelationKey(String company_id, String tel_x, String ext){
		return "axext.com."+company_id+".rel."+tel_x+"."+ext;
	}
	
	/**
	 * Ax查询中  绑定的ayb 集合 
	 * @param axext_bind_id
	 * @return
	 */
//	public static String getAxExtCompanyAybSetKey(String company_id, String bind_id){
//		return "axext.com."+company_id+".aybset."+bind_id;
//	}
	
	/**
	 * AXB
	 * 手机号使用虚号的数量 的集合  Zset  ｛A,B,C} 数量是score
	 * @param ax_bind_id
	 * @return
	 */
	public static String getAxBCompanyAreaTelUsedMaximumKey(String company_id, String area_code){
		return "axb.com."+company_id+"."+area_code+".maxinum";
	}
	
	/**
	 * AX分机号 使用个数
	 * 最大量值
	 * @param ax_bind_id
	 * @return
	 */
	public static String getAxExtCompanyAreaMaximumKey(String company_id, String area_code){
		return "axext.com."+company_id+"."+area_code+".maxinum";
	}
	
	/**
	 * 服务不可用
	 * @return
	 */
	public static String getServer_Error(){
		return "Server_Error";
	}
	
	/**
	 * 移动 帐号信息
	 * @param company_id
	 * @return
	 */
	public static String getMidAccount(String company_id){
		return "mid.com."+company_id+".account";
	}
} 
