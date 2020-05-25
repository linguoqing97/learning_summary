package com.jdh.fuhsi.portal.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.util.Map;

/**
 * @author shenyong on 2020/3/5
 */
public class HttpClientUtil {

    private static final Log logger = LogFactory.getLog(HttpClientUtil.class);

    //连接超时时间,毫秒
    private static int connectionTimeout = 10000;
    //超时设置
    private static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectionTimeout).build();

    private static RestTemplate formRestTemplate = null;

    private static RestTemplate jsonRestTemplate = null;

    //读取超时
    private static int READ_TIMEOUT = 30000;

    //初始化
    static {
        //构造工厂初始化
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectionTimeout);
        requestFactory.setReadTimeout(READ_TIMEOUT);

        //初始化表单发送实体
        formRestTemplate = new RestTemplate(requestFactory);

        //初始化JSON发送实体
        jsonRestTemplate = new RestTemplate(requestFactory);
    }


    /**
     * post请求 ‘application/json’形式
     *
     * @param url         访问路径
     * @param params      拼接在url中的参数
     * @param json        参数的json格式
     * @param returnClass 返回类型
     * @return
     */
    public static <T> T doPostParamAndJson(String url, Map<String, String> params, String json, Class<? extends T> returnClass) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {

            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addParameter(key, params.get(key));
                }
            }
            URI uri = builder.build();
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setConfig(requestConfig);

            // 创建请求内容
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            printException(url, params, e);
        } finally {
            try {
                if(response != null) {
                    response.close();
                }
            } catch (IOException e) {
                printException(url, params, e);
            }
        }
        return JSON.parseObject(resultString, returnClass);

    }

    private static void printException(String url, Map params, Exception exception) {
        try	{
            String logStr = ", localAddress:" + requestConfig.getLocalAddress()+ ", remote_url:" + url + ", params:" + params;
            if(exception instanceof ConnectTimeoutException) {
                logger.error("[HttpClient ConnectTimeoutException] ConnectTimeout:" + requestConfig.getConnectTimeout() + logStr, exception);
            } else if(exception instanceof SocketTimeoutException) {
                logger.error("[HttpClient SocketTimeoutException] SocketTimeout:" + requestConfig.getSocketTimeout() + logStr, exception);
            } else if (exception instanceof ConnectionPoolTimeoutException) {
                logger.error("[HttpClient ConnectionRequestTimeoutException] ConnectionRequestTimeout:" + requestConfig.getConnectionRequestTimeout() + logStr, exception);
            }	else {
                logger.error("[HttpClient Exception] url:" + url + ", params:" + params, exception);
            }
        } catch(Exception e) {
            logger.error("[log4HttpException]:", e);
            logger.error("[HttpClient Exception] url:" + url + ", params:" + params, exception);
        }
    }
}
