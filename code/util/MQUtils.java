package com.jdh.fuhsi.portal.util;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

import com.jdh.fuhsi.portal.mq.MQConfirmCallback;
import com.jdh.log.LogTools;

/**
 * @Description MQ工具类
 */
public class MQUtils {
	
	private MQUtils() {
		throw new IllegalStateException("Utility class");
	}	
	
	/**
	 * MQ -- 发送信息
	 * 
	 * @param exchange 交换机名字 
	 * @param obj      消息 
	 * @return void
	 */
	public static void send(String exchange, Object obj) {
		
    	CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString().replaceAll("-", "")); 	
    	LogTools.info(SEND_MQ_MSG, exchange, correlationData.getId(), obj);
    	  	
    	try {
			RABBIT_TEMPLATE.setConfirmCallback(MQ_CONFIRM_CALLBACK);
			RABBIT_TEMPLATE.convertAndSend(exchange, "", obj, correlationData);
    	} catch (Exception e) {
    		LogTools.error(SEND_MQ_MSG_ERR, exchange, correlationData.getId(), obj, e);
    	}
	}
	
	private static final RabbitTemplate RABBIT_TEMPLATE = SpringContextUtils.getBean("rabbitTemplate");
	private static final MQConfirmCallback MQ_CONFIRM_CALLBACK = new MQConfirmCallback();
	private static String SEND_MQ_MSG = "[MQ--消息发送] - [exchange]:{} - [id]:{} - [msg]:{}";
	private static String SEND_MQ_MSG_ERR = "[MQ--消息异常] - [exchange]:{} - [id]:{} - [msg]:{} - [异常]:{}";
	
}
