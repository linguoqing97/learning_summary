package com.jdh.fuhsi.portal.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Service
@Lazy(false)
public class SpringContextUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringContextUtils.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(Class<T> clazz) {
		@SuppressWarnings("rawtypes")
		Map beanMaps = applicationContext.getBeansOfType(clazz);

		if (beanMaps != null && !beanMaps.isEmpty()) {
			return (T) beanMaps.values().iterator().next();
		} else {
			return null;
		}
	}

	/**
	 * 获取HttpServletRequest
	 */
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		return attributes.getRequest();
	}

	/**
	 * 获取HttpSession
	 */
	public static HttpSession getSession() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		return attributes.getRequest().getSession();
	}

	/**
	 * 获取完整的请求URL
	 */
	public static String getRequestUrl() {
		return getRequestUrl(getRequest());
	}

	/**
	 * 获取完整的请求URL
	 */
	public static String getRequestUrl(HttpServletRequest request) {
		// 当前请求路径
		String currentUrl = request.getRequestURL().toString();
		// 请求参数
		String queryString = request.getQueryString();
		if (!StringUtils.isEmpty(queryString)) {
			currentUrl = currentUrl + "?" + queryString;
		}

		String result = "";
		try {
			result = URLEncoder.encode(currentUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// ignore
		}

		return result;
	}

	/**
	 * 获取请求的客户端IP
	 */
	public static String getRequestIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");

		if (StringUtils.isNoneBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}

		ip = request.getHeader("X-Real-IP");
		if (StringUtils.isNoneBlank(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			return ip;
		}

		return request.getRemoteAddr();
	}

	/**
	 * 获取用户请求token
	 */
	public static String getToken() {
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		return attributes.getRequest().getHeader("AUTH_TOKEN");
	}

	/**
	 * get profiles
	 * @return
	 */
	public static String getActiveProfile(){
		String [] profiles = applicationContext.getEnvironment().getActiveProfiles();
		if (!ArrayUtils.isEmpty(profiles)){
			return profiles[0];
		}
		return "";
	}
}