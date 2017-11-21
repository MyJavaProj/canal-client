package com.fxrh.canal;

import java.net.InetSocketAddress;
import java.util.List;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;


public class myCanalTest {
	public static void main(String[] args) throws InterruptedException {
        String ip = AddressUtils.getHostIp();
        // 第一步：与canal进行连接
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress("36.102.216.130", 11111),"dbtest","","");
        connector.connect();

		// 第二步：开启订阅
		connector.subscribe();

		// 第三步：循环订阅
		while (true) {
			try {
				// 每次读取 1000 条
				Message message = connector.getWithoutAck(1000);

				long batchID = message.getId();

				int size = message.getEntries().size();

				if (batchID == -1 || size == 0) {
					System.out.println("当前暂时没有数据");
					Thread.sleep(1000); // 没有数据
				} else {
					System.out.println("-------------------------- 有数据啦 -----------------------");
					
					
					PrintEntry(message.getEntries());
				}

				// position id ack （方便处理下一条）
				connector.ack(batchID);

			} catch (Exception e) {
				// TODO: handle exception

			} finally {
				Thread.sleep(1000);
			}
		}
	}

	// 获取每条打印的记录
    public static void PrintEntry(List<Entry> entrys) {
		 for (Entry entry : entrys) {  
	            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {  
	                continue;  
	            }  
	  
	            RowChange rowChage = null;  
	            try {  
	                rowChage = RowChange.parseFrom(entry.getStoreValue());  
	            } catch (Exception e) {  
	                throw new RuntimeException("ERROR ## parser of eromanga-event has an error , data:" + entry.toString(),  
	                        e);  
	            }  
	  
	            EventType eventType = rowChage.getEventType();  
	            System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",  
	                    entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),  
	                    entry.getHeader().getSchemaName(), entry.getHeader().getTableName(),  
	                    eventType));  
	  
	            for (RowData rowData : rowChage.getRowDatasList()) {  
	                if (eventType == EventType.DELETE) {  
	                	PrintColumn(rowData.getBeforeColumnsList());  
	                } else if (eventType == EventType.INSERT) {  
	                	PrintColumn(rowData.getAfterColumnsList());  
	                } else {  
	                    System.out.println("-------> before");  
	                    PrintColumn(rowData.getBeforeColumnsList());  
	                    System.out.println("-------> after");  
	                    PrintColumn(rowData.getAfterColumnsList());  
	                }  
	            }  
	        }  
    }

	// 每个row上面的每一个column 的更改情况
	public static void PrintColumn(List<Column> columns) {
		for (Column column : columns) {  
            System.out.println(column.getName() + " : " + column.getValue() + "    update=" + column.getUpdated());  
        }  
    }
}
