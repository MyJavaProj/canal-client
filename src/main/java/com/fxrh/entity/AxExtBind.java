package com.fxrh.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "axExtBind")
@CompoundIndexes({ 
	@CompoundIndex(name = "com_area_index", def = "{company_id: 1, area: 1}") 
}) 
public class AxExtBind {
	
	@Id
	private String id;
	
	@Indexed
	private String company_id;
	@Indexed
	private String area;
	private String bind_id;
	private String tel_x;
	private String tel_x_ext;
	@Indexed
	private Long expire;//过期时间戳 秒
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCompany_id() {
		return company_id;
	}
	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getBind_id() {
		return bind_id;
	}
	public void setBind_id(String bind_id) {
		this.bind_id = bind_id;
	}
	public String getTel_x() {
		return tel_x;
	}
	public void setTel_x(String tel_x) {
		this.tel_x = tel_x;
	}
	public String getTel_x_ext() {
		return tel_x_ext;
	}
	public void setTel_x_ext(String tel_x_ext) {
		this.tel_x_ext = tel_x_ext;
	}
	public Long getExpire() {
		return expire;
	}
	public void setExpire(Long expire) {
		this.expire = expire;
	}
	
	
}
