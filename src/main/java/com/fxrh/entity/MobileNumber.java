package com.fxrh.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name="mobile_number")
public class MobileNumber implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private Long mobile;//手机号码
	private Boolean test;//true:测试号码
	private Integer type;//1:axb模式；2：Ax模式；3：Ax分机号模式
	private String areaCode;		//区号
	private Integer companyId;		//企业ID
	private Boolean disable=false;//true:暂时冻结(号码不可用)
	
	private String bind_tel;		//Ax模式 绑定号码
	private String bind_tel2;		//Ax模式 绑定号码
	private String caller_show;//主叫显示号码  0:透传主叫；1:自动分配Y；其他虚号
	private Integer expiration;//caller_show = 1有效
	
	private Date create_time;
	public Long getMobile() {
		return mobile;
	}
	public void setMobile(Long mobile) {
		this.mobile = mobile;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public Boolean getDisable() {
		return disable;
	}
	public void setDisable(Boolean disable) {
		this.disable = disable;
	}
	public String getBind_tel() {
		return bind_tel;
	}
	public void setBind_tel(String bind_tel) {
		this.bind_tel = bind_tel;
	}
	public String getBind_tel2() {
		return bind_tel2;
	}
	public void setBind_tel2(String bind_tel2) {
		this.bind_tel2 = bind_tel2;
	}
	public String getCaller_show() {
		return caller_show;
	}
	public void setCaller_show(String caller_show) {
		this.caller_show = caller_show;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Integer getExpiration() {
		return expiration;
	}
	public void setExpiration(Integer expiration) {
		this.expiration = expiration;
	}
	public Boolean getTest() {
		return test;
	}
	public void setTest(Boolean test) {
		this.test = test;
	}
	
}
