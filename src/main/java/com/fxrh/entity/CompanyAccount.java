package com.fxrh.entity;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

//企业账户
@Entity
@Table(name="company_account")
public class CompanyAccount implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id     
	@GeneratedValue(strategy = IDENTITY)
	private Integer id;
	
	@Column(name = "app_key", length=32, unique=true)
	private String appKey;		
	
	@Column(name = "app_secret", length=32)
	private String appSecret;
	
	private String name;
	
	private String axb_audio_other_call_x;//第三者呼叫 X号码时的提示语音文件名
	
	private String ax_ext_audio_x;//请输入分机号 提示 语音文件
	
	private String ax_ext_No_Tel_audio;//ax_ext 空号提示语音
	
	private Integer company_id;
	private String midcdr_push_url;
	private String midvoice_push_url;
	private Integer mid_type;
	
	public Integer getCompany_id() {
		return company_id;
	}

	public void setCompany_id(Integer company_id) {
		this.company_id = company_id;
	}

	public String getMidcdr_push_url() {
		return midcdr_push_url;
	}

	public void setMidcdr_push_url(String midcdr_push_url) {
		this.midcdr_push_url = midcdr_push_url;
	}

	public String getMidvoice_push_url() {
		return midvoice_push_url;
	}

	public void setMidvoice_push_url(String midvoice_push_url) {
		this.midvoice_push_url = midvoice_push_url;
	}

	public Integer getMid_type() {
		return mid_type;
	}

	public void setMid_type(Integer mid_type) {
		this.mid_type = mid_type;
	}

	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAxb_audio_other_call_x() {
		return axb_audio_other_call_x;
	}

	public void setAxb_audio_other_call_x(String axb_audio_other_call_x) {
		this.axb_audio_other_call_x = axb_audio_other_call_x;
	}

	public String getAx_ext_audio_x() {
		return ax_ext_audio_x;
	}

	public void setAx_ext_audio_x(String ax_ext_audio_x) {
		this.ax_ext_audio_x = ax_ext_audio_x;
	}

	public String getAx_ext_No_Tel_audio() {
		return ax_ext_No_Tel_audio;
	}

	public void setAx_ext_No_Tel_audio(String ax_ext_No_Tel_audio) {
		this.ax_ext_No_Tel_audio = ax_ext_No_Tel_audio;
	}		
	
}
