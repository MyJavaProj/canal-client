package com.fxrh.service.impl;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.fxrh.config.MyProps;
import com.fxrh.dao.CompanyAccountDao;
import com.fxrh.entity.CompanyAccount;
import com.fxrh.entity.MobileNumber;
import com.fxrh.pool.RedisKeyPrefix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service("backupTableService")
public class UpdatePoolServiceImpl implements com.fxrh.service.UpdatePoolService {
	@Autowired
	CompanyAccountDao companyAccountDao;
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Autowired
	MyProps myProps;
	
	@Override
	public void UpdatePool(){
		String ip = AddressUtils.getHostIp();
		// 第一步：与canal进行连接
		CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(myProps.getAddress(), myProps.getPort().intValue()), myProps.getDestinations(), myProps.getUsername(), myProps.getPassword());
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
//					System.out.println("当前暂时没有数据");
					Thread.sleep(1000); // 没有数据
				} else {
					System.out.println("-------------------------- 有数据啦 -----------------------");

					PrintEntry(message.getEntries());
				}

				// position id ack （方便处理下一条）
				//不进行确认则获取之前所有更新数据
				connector.ack(batchID);

			} catch (Exception e) {
				// TODO: handle exception

			} finally {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	// 获取每条打印的记录
		public void PrintEntry(List<Entry> entrys) {
			for (Entry entry : entrys) {
				if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN
						|| entry.getEntryType() == EntryType.TRANSACTIONEND) {
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
//				System.out.println(String.format("================> binlog[%s:%s] , name[%s,%s] , eventType : %s",
//						entry.getHeader().getLogfileName(), entry.getHeader().getLogfileOffset(),
//						entry.getHeader().getSchemaName(), entry.getHeader().getTableName(), eventType));

				// company_account 表操作 增删改查
				if (entry.getHeader().getTableName().equals("company_account")) {
//					for (RowData rowData : rowChage.getRowDatasList()) {
//						CompanyAccount beforeModel = getCompanyAccount(rowData.getBeforeColumnsList());
//						CompanyAccount afterModel = getCompanyAccount(rowData.getAfterColumnsList());
	//
//						if (beforeModel.getMidcdr_push_url().contains(push_url)) {
//							if (eventType == EventType.DELETE) {
	//
//							} else if (eventType == EventType.INSERT) {
//								CompanyAccount model = companyAccountDao.findOne(afterModel.getId());
//								if (model == null) {
//									companyAccountDao.save(afterModel);
//								}
//								// else {
//								// companyAccountDao.save(afterModel);
//								// }
	//
//							} else {
	//
//							}
	//
//						}
//					}

				}

				// mobile_number 缓存操作
				else if (entry.getHeader().getTableName().equals("mobile_number")) {
					for (RowData rowData : rowChage.getRowDatasList()) {
						MobileNumber afterMobileNumber = getMobileNumber(rowData.getAfterColumnsList());
						MobileNumber beforeMobileNumber = getMobileNumber(rowData.getBeforeColumnsList());

							try {
								if (eventType == EventType.DELETE) {
									deletePool(beforeMobileNumber);
								} else if (eventType == EventType.INSERT) {
									if(!afterMobileNumber.getDisable() && afterMobileNumber.getCompanyId() !=0 && afterMobileNumber.getType()!= 0) {
										addPool(afterMobileNumber);
									}
								} else {
									if (beforeMobileNumber.getType() != afterMobileNumber.getType() //更新码号类型、所属公司、冻结
											|| beforeMobileNumber.getCompanyId() != afterMobileNumber.getCompanyId()
											||beforeMobileNumber.getDisable() != afterMobileNumber.getDisable()) {
										changePool(beforeMobileNumber, afterMobileNumber);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						
					}
				}

			}
		}

		/**
		 * 把列转换为数据模型
		 * 
		 * @param columns
		 * @return
		 */
		public MobileNumber getMobileNumber(List<Column> columns) {
			MobileNumber model = new MobileNumber();
			for (Column column : columns) {
				if (column.getName().equals("mobile")) {
					model.setMobile(Long.parseLong(column.getValue()));
				}

				if (column.getName().equals("area_code")) {
					model.setAreaCode(column.getValue());
				} else if (column.getName().equals("company_id")) {
					model.setCompanyId(Integer.parseInt(column.getValue().hashCode() == 0 ? "0" : column.getValue()));
				}
				// else
				// if(column.getName().equals("emergency")) {
				// model.set(Long.parseLong(column.getValue()));
				// }
				else if (column.getName().equals("type")) {
					model.setType(Integer.parseInt(column.getValue().hashCode() == 0 ? "0" : column.getValue()));
				} else if (column.getName().equals("bind_tel")) {
					model.setBind_tel(column.getValue());
				} else if (column.getName().equals("bind_tel2")) {
					model.setBind_tel2(column.getValue());
				} else if (column.getName().equals("caller_show")) {
					model.setCaller_show(column.getValue());
				} else if (column.getName().equals("disable")) {
					model.setDisable(column.getValue().equals("0") ? false : true);
				} else if (column.getName().equals("create_time")) {
					SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			        String formatUtilDate = sdf1.format(new Date());
					String ss = column.getValue().hashCode() == 0 ? formatUtilDate : column.getValue();
					Date parseUtilDate = null;
					try {
						parseUtilDate = sdf1.parse(ss);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					model.setCreate_time(parseUtilDate);
				} else if (column.getName().equals("expiration")) {
					model.setExpiration(Integer.parseInt(column.getValue().hashCode() == 0 ? "0" : column.getValue()));
				} else if (column.getName().equals("test")) {
					model.setTest(column.getValue().hashCode() == 0 ? true : false);
				}
			}
			return model;
		}

		public CompanyAccount getCompanyAccount(List<Column> columns) {
			CompanyAccount model = new CompanyAccount();
			for (Column column : columns) {
				if (column.getName().equals("id")) {
					model.setId(Integer.parseInt(column.getValue().hashCode() == 0 ? "0" : column.getValue()));
				}
				if (column.getName().equals("app_key")) {
					model.setAppKey(column.getValue());
				}
				if (column.getName().equals("app_secret")) {
					model.setAppSecret(column.getValue());
				}
				if (column.getName().equals("ax_ext_audio_x")) {
					model.setAx_ext_audio_x(column.getValue());
				}
				if (column.getName().equals("axb_audio_other_call_x")) {
					model.setAxb_audio_other_call_x(column.getValue());
				}
				if (column.getName().equals("ax_ext_no_tel_audio")) {
					model.setAx_ext_No_Tel_audio(column.getValue());
				}
				if (column.getName().equals("company_id")) {
					model.setCompany_id(Integer.parseInt(column.getValue().hashCode() == 0 ? "0" : column.getValue()));
				}
				if (column.getName().equals("name")) {
					model.setName(column.getValue());
				}
				if (column.getName().equals("midcdr_push_url")) {
					model.setMidcdr_push_url(column.getValue());
				}
				if (column.getName().equals("midvoice_push_url")) {
					model.setMidvoice_push_url(column.getValue());
				}
				if (column.getName().equals("mid_type")) {
					model.setMid_type(Integer.parseInt(column.getValue().hashCode() == 0 ? "0" : column.getValue()));
				}
			}
			return model;
		}

		public void deletePool(MobileNumber beforeMobileNumber) {
			List<String> number = new ArrayList<String>();
			number.add(beforeMobileNumber.getMobile().toString());

			if (beforeMobileNumber.getType() == 1) { // 1:axb模式；2：Ax模式；3：Ax分机号模式
				removeAxbPool(number, beforeMobileNumber.getAreaCode(), beforeMobileNumber.getCompanyId().toString());
			} else if (beforeMobileNumber.getType() == 3) {
				removeAxExtPool(number, beforeMobileNumber.getAreaCode(), beforeMobileNumber.getCompanyId().toString());
			}
		}

		public void addPool(MobileNumber beforeMobileNumber) {
			List<String> number = new ArrayList<String>();
			number.add(beforeMobileNumber.getMobile().toString());

			if (beforeMobileNumber.getType() == 1) { // 1:axb模式；2：Ax模式；3：Ax分机号模式
				addCompanyNumberAxb2Pool(number, beforeMobileNumber.getAreaCode(),
						beforeMobileNumber.getCompanyId().toString());
			} else if (beforeMobileNumber.getType() == 3) {
				addCompanyNumberAxExt2Pool(number, beforeMobileNumber.getAreaCode(),
						beforeMobileNumber.getCompanyId().toString());
			}
		}
		public void addByMobileNumber(MobileNumber afterMobileNumber) {
			List<String> number = new ArrayList<String>();
			number.add(afterMobileNumber.getMobile().toString());
			if (afterMobileNumber.getType() == 1) {
				addCompanyNumberAxb2Pool(number, afterMobileNumber.getAreaCode(),
						afterMobileNumber.getCompanyId().toString());
			} else if (afterMobileNumber.getType() == 3) {
				addCompanyNumberAxExt2Pool(number, afterMobileNumber.getAreaCode(),
						afterMobileNumber.getCompanyId().toString());
			}
		}
		@SuppressWarnings("static-access")
		public void changePool(MobileNumber beforeMobileNumber, MobileNumber afterMobileNumber) {
			List<String> number = new ArrayList<String>();
			number.add(afterMobileNumber.getMobile().toString());
			if(beforeMobileNumber.getDisable().equals(false) && afterMobileNumber.getDisable().equals(true)) {//冻结
				if (beforeMobileNumber.getType() == 1) {// 1:axb模式；2：Ax模式；3：Ax分机号模式
					removeAxbPool(number, beforeMobileNumber.getAreaCode(), beforeMobileNumber.getCompanyId().toString());
				} else if (beforeMobileNumber.getType() == 3) {
					removeAxExtPool(number, beforeMobileNumber.getAreaCode(), beforeMobileNumber.getCompanyId().toString());
				}
			}else if(beforeMobileNumber.getDisable().equals(true) && afterMobileNumber.getDisable().equals(false)) {//解冻
				addByMobileNumber(afterMobileNumber);
			}
			else if(afterMobileNumber.getDisable().equals(false)){
				if (beforeMobileNumber.getType() == 1) {
					removeAxbPool(number, beforeMobileNumber.getAreaCode(), beforeMobileNumber.getCompanyId().toString());
					addByMobileNumber(afterMobileNumber);
				} else if (beforeMobileNumber.getType() == 3) {
					removeAxExtPool(number, beforeMobileNumber.getAreaCode(), beforeMobileNumber.getCompanyId().toString());
					addByMobileNumber(afterMobileNumber);
				} else {//更新前没有类型的或者更新前type=2
					addByMobileNumber(afterMobileNumber);
				}
			}
		}

		/*
		 * companyid 变更  清理AXB 企业地区号码池、企业已开通号码地区
		 */
		public void removeAxbPool(List<String> number, String area_code, String company_id) {
			String keyPool = RedisKeyPrefix.getAxbCompanyAreaNnmberPoolKey(company_id, area_code);
			redisTemplate.opsForSet().remove(keyPool, number.toArray());
			
			System.out.println("removeAxbPool  keyPool:" + keyPool + ",number:" + number.get(0));
			System.out.println("AxbPool  key:" + keyPool + ",value:" + redisTemplate.opsForSet().members(keyPool).toString());
			
			
			String key = RedisKeyPrefix.getAxbCompanyAreaKey(company_id.toString());
			if (redisTemplate.opsForSet().size(keyPool) <= 0) {
				redisTemplate.opsForSet().remove(key, area_code);
				
				System.out.println("removeAxbCompanyAreaKey  key:" + key );
				System.out.println("AxbCompanyAreaKey  key:" + key + ",value:" + redisTemplate.opsForSet().members(key).toString());
			}
		}

		/**
		 * companyid 变更 清理AX分机号 企业地区全量号码池、企业已开通号码地区
		 * @param number
		 * @param area_code
		 * @param company_id
		 */
		public void removeAxExtPool(List<String> number, String area_code, String company_id) {
			String keyPool = RedisKeyPrefix.getAxExtCompanyAreaFullNnmberPoolKey(company_id, area_code);
			redisTemplate.opsForSet().remove(keyPool, number.toArray());

			System.out.println("removeAxExtPool  keyPool:" + keyPool + ",number:" + number.get(0));
			System.out.println("AxExtPool  key:" + keyPool + ",value:" + redisTemplate.opsForSet().members(keyPool).toString());
			
			
			String key = RedisKeyPrefix.getAxExtCompanyAreaKey(company_id.toString());
			if (redisTemplate.opsForSet().size(keyPool) <= 0) {
				redisTemplate.opsForSet().remove(key, area_code);
				
				System.out.println("removeAxExtCompanyAreaKey  key:" + key +",area_code:" +area_code);
				System.out.println("AxExtCompanyAreaKey  key:" + key + ",value:" + redisTemplate.opsForSet().members(key).toString());
			}
			
			String usableKey = RedisKeyPrefix.getAxExtCompanyAreaUsableNnmberPoolKey(company_id, area_code);
			redisTemplate.opsForSet().remove(usableKey, number.toArray());
			
			System.out.println("remove AxExtCompanyAreaUsableNnmberPoolKey  usableKey:" + usableKey);
			System.out.println("AxExtCompanyAreaUsableNnmberPoolKey  key:" + usableKey + ",value:" + redisTemplate.opsForSet().members(usableKey).toString());
			
			
			String usableNamePoolKey = RedisKeyPrefix.getAxExtCompanyAreaUsableNnmberPoolKey(company_id, area_code);
			redisTemplate.opsForSet().remove(usableNamePoolKey, number.toArray());
			System.out.println("getAxExtCompanyAreaUsableNnmberPoolKey  usableNamePoolKey:" + usableNamePoolKey + ",value:" + redisTemplate.opsForSet().members(usableNamePoolKey).toString());
			
			
			Object[] exts = getExts();
			for(String mobile :number) {
				String companyUsableKey = RedisKeyPrefix.getAxExtCompanyUsableKey(company_id, mobile);
				redisTemplate.opsForSet().remove(companyUsableKey, exts);
				System.out.println("getAxExtCompanyUsableKey  key:" + companyUsableKey + ",value:" + redisTemplate.opsForSet().members(companyUsableKey).toString());
			}
		}

		public void addCompanyNumberAxb2Pool(List<String> number, String area_code, String company_id) {
			String key = RedisKeyPrefix.getAxbCompanyAreaKey(company_id.toString());
			redisTemplate.opsForSet().add(key, area_code);

			System.out.println("addAxbCompanyAreaKey  key:" + key +",area_code" + area_code);
			System.out.println("AxbCompanyAreaKey  key:" + key + ",value:" + redisTemplate.opsForSet().members(key).toString());
			
			String keyPool = RedisKeyPrefix.getAxbCompanyAreaNnmberPoolKey(company_id, area_code);
			
			redisTemplate.opsForSet().add(keyPool, number.toArray());
			
			System.out.println("addAxbCompanyAreaNnmberPoolKey  keyPool:" + keyPool + ",number:" + number.get(0));
			System.out.println("AxbCompanyAreaNnmberPoolKey  key:" + keyPool + ",value:" + redisTemplate.opsForSet().members(keyPool).toString());
		}

		public void addCompanyNumberAxExt2Pool(List<String> number, String area_code, String company_id) {
			String key = RedisKeyPrefix.getAxExtCompanyAreaKey(company_id.toString());
			
			redisTemplate.opsForSet().add(key, area_code);
			System.out.println("addAxExtCompanyAreaKey  key:" + key +",area_code" + area_code);
			System.out.println("AxExtCompanyAreaKey  key:" + key + ",value:" + redisTemplate.opsForSet().members(key).toString());
			
			
			String keyPool = RedisKeyPrefix.getAxExtCompanyAreaFullNnmberPoolKey(company_id, area_code);
			
			redisTemplate.opsForSet().add(keyPool, number.toArray());
			
			System.out.println("addCompanyNumberAxExt2Pool  keyPool:" + keyPool + ",number:" + number.get(0));
			System.out.println("AxExtCompanyAreaFullNnmberPoolKey  key:" + keyPool + ",value:" + redisTemplate.opsForSet().members(keyPool).toString());
			
			String usableNamePoolKey = RedisKeyPrefix.getAxExtCompanyAreaUsableNnmberPoolKey(company_id, area_code);
			redisTemplate.opsForSet().add(usableNamePoolKey, number.toArray());
			System.out.println("getAxExtCompanyAreaUsableNnmberPoolKey  usableNamePoolKey:" + usableNamePoolKey + ",value:" + redisTemplate.opsForSet().members(usableNamePoolKey).toString());
			
			
			Object[] exts = getExts();
			for(String mobile :number) {
				String companyUsableKey = RedisKeyPrefix.getAxExtCompanyUsableKey(company_id, mobile);
				redisTemplate.opsForSet().add(companyUsableKey, exts);
				
				System.out.println("getAxExtCompanyUsableKey  key:" + companyUsableKey + ",value:" + redisTemplate.opsForSet().members(companyUsableKey).toString());
			}
			
		}

		/**
		 * X号码分机号集合
		 * @return
		 */
		private Object[] getExts() {
			Object[] exts = new String[9000];
			int index = 0;
			for (int i = 1000; i < 10000; i++) {
				exts[index] = String.valueOf(i);
				index ++ ;
			}
			return exts;
		}
	}
