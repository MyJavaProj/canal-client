package com.fxrh.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.fxrh.utils.xss.SQLFilter;

/**
 * 查询参数
 *
 */
public class Query extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;
	//当前页码
    private int page;
    //每页条数
    private int limit;
    private Pageable pageable;

    public Query(Map<String, Object> params){
        this.putAll(params);

        //分页参数
        this.page = Integer.parseInt(params.get("page").toString());
        this.limit = Integer.parseInt(params.get("limit").toString());
        this.put("offset", (page - 1) * limit);
        this.put("page", page);
        this.put("limit", limit);

        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String sidx = params.get("sidx").toString();
        String order = params.get("order").toString();
        this.put("sidx", SQLFilter.sqlInject(sidx));
        this.put("order", SQLFilter.sqlInject(order));
        if(sidx != null && !"".equals(sidx.trim())){
        	Sort sort = null;
        	if (Direction.ASC.toString().equals(order.toUpperCase())) {
                sort = new Sort(Direction.ASC, sidx);
            } else {
                sort = new Sort(Direction.DESC, sidx);
            }
        	this.pageable = new PageRequest(page-1, limit, sort);
        }else{
        	this.pageable = new PageRequest(page-1, limit);
        }
    }


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
    
    public Pageable getPageable() {
		return pageable;
	}
}
