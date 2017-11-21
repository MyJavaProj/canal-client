package com.fxrh.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="myProps") //接收application.yml中的myProps下面的属性
public class MyProps {
	private String push_url;
	private String address;
	private Long port;
	private String destinations;
	private String username;
	private String password;
	public String getPush_url() {
		return push_url;
	}
	public void setPush_url(String push_url) {
		this.push_url = push_url;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Long getPort() {
		return port;
	}
	public void setPort(Long port) {
		this.port = port;
	}
	public String getDestinations() {
		return destinations;
	}
	public void setDestinations(String destinations) {
		this.destinations = destinations;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
