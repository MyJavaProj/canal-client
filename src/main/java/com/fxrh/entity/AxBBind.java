package com.fxrh.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "axBBind")
@CompoundIndexes({
	@CompoundIndex(name = "com_area_index", def = "{company_id: 1, area: 1}"),
	@CompoundIndex(name = "com_area_tel_index", def = "{company_id: 1, area: 1, tel: 1}")
}) 
public class AxBBind {
	
	@Id
	@Indexed
    private String id;
	
	@Indexed(expireAfterSeconds=0)
	private Date date;
	@Indexed
	private String companyid;
	@Indexed
	private String area;
	private String groupkey;//1.010
	private String bindid;
	private String tel; //客户电话
	private Long expire;//过期时间戳 秒  最早过期时间
	private String tel_x; //虚号

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCompanyid() {
		return companyid;
	}

	public void setCompanyid(String companyid) {
		this.companyid = companyid;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getBindid() {
		return bindid;
	}

	public void setBindid(String bindid) {
		this.bindid = bindid;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Long getExpire() {
		return expire;
	}

	public void setExpire(Long expire) {
		this.expire = expire;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTel_x() {
		return tel_x;
	}

	public void setTel_x(String tel_x) {
		this.tel_x = tel_x;
	}

	public String getGroupkey() {
		return groupkey;
	}

	public void setGroupkey(String groupkey) {
		this.groupkey = groupkey;
	}

	
}
