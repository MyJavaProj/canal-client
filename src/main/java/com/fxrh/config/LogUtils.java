package com.fxrh.config;

public class LogUtils {

	
	public static String getLogString(Object... svcName){
		if(svcName==null)return "";
		StringBuffer buf = new StringBuffer("");
		for(int i=0;i<svcName.length;i++){
			buf.append(String.valueOf(svcName[i]));
		}
		return buf.toString();
	}
}
