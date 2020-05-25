package com.jdh.fuhsi.portal.util.crypt;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.defaults.DefaultSqlSession;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: qinzhishen
 * @create: 2020-02-07 11:53
 */
@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class CryptInterceptor implements Interceptor {

    private static final String PARAM = "param";

    private static final String PARAM_TYPE_LIST = "list";

    private static final String PARAM_TYPE_COLLECTION = "collection";

    private static final String MAPPEDSTATEMENT_ID_SEPERATOR = ".";

    /**
     * 适用于加密判断
     */
    private static final ConcurrentHashMap<String, Set<String>> METHOD_PARAM_ANNOTATIONS_MAP = new ConcurrentHashMap<String, Set<String>>();
    /**
     * 适用于解密判断
     */
    private static final ConcurrentHashMap<String, Boolean> METHOD_ANNOTATIONS_MAP = new ConcurrentHashMap<String, Boolean>();

    public CryptInterceptor() {
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object parameter = invocation.getArgs()[1];
        MappedStatement statement = (MappedStatement) invocation.getArgs()[0];

        // 入参加密，不同参数类型进行处理
        if (isNeedCrypt(parameter)) {
            if (parameter instanceof String) {
                invocation.getArgs()[1] = stringEncrypt((String) parameter, getParameterAnnotations(statement));
            } else if (parameter instanceof DefaultSqlSession.StrictMap) {
                handlerStrictMap(statement, parameter);
            } else if (parameter instanceof MapperMethod.ParamMap) {
                handlerParamMap(statement, parameter);
            } else {
                beanEncrypt(parameter);
            }
        }

        // 获得出参
        Object returnValue = invocation.proceed();

        if (returnValue instanceof Integer && parameter instanceof Object) {
            beanDecrypt(parameter);
            return returnValue;
        }
        // 出参解密
        if (isNotCrypt(returnValue)) {
            return returnValue;
        }
        Boolean bo = getMethodAnnotations(statement);
        if (returnValue instanceof String && bo) {
            return stringDecrypt((String) returnValue);
        }
        if (returnValue instanceof List) {
            listDecrypt((List) returnValue, bo);
            return returnValue;
        }
        return returnValue;
    }


    /**
     * 处理 ParamMap 类型查询参数
     */
    private void handlerParamMap(MappedStatement statement, Object parameter) throws Exception {
        MapperMethod.ParamMap<Object> paramMap = (MapperMethod.ParamMap<Object>) parameter;
        Set<String> set = getParameterAnnotations(statement);
        boolean setEmpty = set.isEmpty();
        // 解析每一个参数
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            // 判断不需要解析的类型 不解析map
            if (isNotCrypt(entry.getValue()) || entry.getValue() instanceof Map || entry.getKey().contains(PARAM)) {
                continue;
            }
            // 如果string
            if (entry.getValue() instanceof String) {
                String encryptValue = stringEncrypt(entry.getKey(), (String) entry.getValue(), set, true);
                entry.setValue(encryptValue);
                continue;
            }

            boolean isSetValue = !setEmpty && set.contains(entry.getKey());
            // 如果 list
            if (entry.getValue() instanceof List) {
                listEncrypt((List) entry.getValue(), isSetValue);
                continue;
            }
            beanEncrypt(entry.getValue());
        }

    }

    /**
     * 处理 StrictMap 查询条件
     */
    private void handlerStrictMap(MappedStatement statement, Object parameter) throws Exception {
        DefaultSqlSession.StrictMap<Object> strictMap = (DefaultSqlSession.StrictMap<Object>) parameter;
        for (Map.Entry<String, Object> entry : strictMap.entrySet()) {
            if (entry.getKey().contains(PARAM_TYPE_COLLECTION)) {
                continue;
            }
            if (entry.getKey().contains(PARAM_TYPE_LIST)) {
                Set<String> set = getParameterAnnotations(statement);
                listEncrypt((List) entry.getValue(), !set.isEmpty());
            }
        }
    }


    /**
     * 获取 方法上的注解
     */
    private Boolean getMethodAnnotations(MappedStatement statement) throws ClassNotFoundException {
        final String id = statement.getId();
        Boolean bo = METHOD_ANNOTATIONS_MAP.get(id);
        if (bo != null) {
            return bo;
        }
        Method m = getMethodByMappedStatementId(id);
        if (m == null) {
            return Boolean.FALSE;
        }
        final CryptField cryptField = m.getAnnotation(CryptField.class);
        // 如果允许解密
        if (cryptField != null && cryptField.decrypt()) {
            bo = Boolean.TRUE;
        } else {
            bo = Boolean.FALSE;
        }
        Boolean bo1 = METHOD_ANNOTATIONS_MAP.putIfAbsent(id, bo);
        if (bo1 != null) {
            bo = bo1;
        }

        return bo;
    }

    /**
     * 获取 方法参数上的注解
     */
    private Set<String> getParameterAnnotations(MappedStatement statement) throws ClassNotFoundException {
        final String id = statement.getId();
        Set<String> set = METHOD_PARAM_ANNOTATIONS_MAP.get(id);
        if (set != null) {
            return set;
        }
        set = new HashSet<String>();
        Method m = getMethodByMappedStatementId(id);
        if (m == null) {
            return set;
        }
        final Annotation[][] paramAnnotations = m.getParameterAnnotations();
        // get names from @CryptField annotations
        for (Annotation[] paramAnnotation : paramAnnotations) {
            for (Annotation annotation : paramAnnotation) {
                if (annotation instanceof CryptField) {
                    CryptField cryptField = (CryptField) annotation;
                    // 如果允许加密
                    if (cryptField.encrypt()) {
                        String cryptFieldValue = cryptField.value();
                        set.add(cryptFieldValue);
                    }
                    break;
                }
            }
        }

        Set<String> oldSet = METHOD_PARAM_ANNOTATIONS_MAP.putIfAbsent(id, set);
        if (oldSet != null) {
            set = oldSet;
        }

        return set;
    }

    /**
     * 通过mappedStatementId get Method
     */
    private Method getMethodByMappedStatementId(String id) throws ClassNotFoundException {
        Method m = null;
        final Class clazz = Class.forName(id.substring(0, id.lastIndexOf(MAPPEDSTATEMENT_ID_SEPERATOR)));
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(id.substring(id.lastIndexOf(MAPPEDSTATEMENT_ID_SEPERATOR) + 1))) {
                m = method;
                break;
            }
        }

        return m;
    }

    /**
     * 判断是否需要加密
     */
    private boolean isNeedCrypt(Object o) {
        return !isNotCrypt(o);
    }

    /**
     * 判断是否不需要加密
     */
    private boolean isNotCrypt(Object o) {
        return o == null || o instanceof Double || o instanceof Integer || o instanceof Long || o instanceof Boolean;
    }

    /**
     * String 加密
     */
    private String stringEncrypt(String str) throws Exception {
        return stringEncrypt(null, str, null, null);
    }

    /**
     * String 加密
     */
    private String stringEncrypt(String str, Set<String> set) throws Exception {
        return stringEncrypt(null, str, set, true);
    }

    /**
     * String 加密
     *
     * @param name
     * @param str
     * @param set
     * @param isSingle
     * @return
     * @throws Exception
     */
    private String stringEncrypt(String name, String str, Set<String> set, Boolean isSingle) throws Exception {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        if (isSingle == null) {
            str = AesUtils.encrypt(str);
            return str;
        }
        if (isSingle && set != null && !set.isEmpty()) {
            str = AesUtils.encrypt(str);
            return str;
        }
        if (!isSingle && set != null && !set.isEmpty() && set.contains(name)) {
            str = AesUtils.encrypt(str);
            return str;
        }
        return str;
    }

    /**
     * String 解密
     */
    private String stringDecrypt(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        str = AesUtils.decrypt(str);
        return str;
    }

    /**
     * list 加密
     */
    private List listEncrypt(List list, Boolean bo) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            Object listValue = list.get(i);
            // 判断不需要解析的类型
            if (isNotCrypt(listValue) || listValue instanceof Map) {
                break;
            }
            if (listValue instanceof String && bo) {
                list.set(i, stringEncrypt((String) listValue));
                continue;
            }
            beanEncrypt(listValue);
        }

        return list;
    }

    /**
     * list 解密
     */
    private List listDecrypt(List list, Boolean bo) throws Exception {
        for (int i = 0; i < list.size(); i++) {
            Object listValue = list.get(i);
            // 判断不需要解析的类型 获得
            if (isNotCrypt(listValue) || listValue instanceof Map) {
                break;
            }
            if (listValue instanceof String && bo) {
                list.set(i, stringDecrypt((String) listValue));
                continue;
            }
            beanDecrypt(listValue);
        }

        return list;
    }

    /**
     * bean 加密
     */
    private void beanEncrypt(Object val) throws Exception {
        Class objClazz = val.getClass();
        Field[] objFields = objClazz.getDeclaredFields();
        for (Field field : objFields) {
            CryptField cryptField = field.getAnnotation(CryptField.class);
            if (cryptField != null && cryptField.encrypt()) {
                field.setAccessible(true);
                Object fieldValue = field.get(val);
                if (fieldValue == null) {
                    continue;
                }
                if (field.getType().equals(String.class)) {
                    field.set(val, stringEncrypt((String) fieldValue));
                    continue;
                }
                if (field.getType().equals(List.class)) {
                    field.set(val, listEncrypt((List) fieldValue, Boolean.TRUE));
                    continue;
                }
            }
        }
    }

    /**
     * bean 解密
     */
    private void beanDecrypt(Object val) throws Exception {
        Class objClazz = val.getClass();
//      使用 FieldUtils.getAllFields 解决不能读取父类 private 的问题
        Field[] objFields = FieldUtils.getAllFields(objClazz);
        for (Field field : objFields) {
            CryptField cryptField = field.getAnnotation(CryptField.class);
            if (cryptField != null && cryptField.decrypt()) {
                field.setAccessible(true);
                Object fieldValue = field.get(val);
                if (fieldValue == null) {
                    continue;
                }
                if (field.getType().equals(String.class)) {
                    field.set(val, stringDecrypt((String) fieldValue));
                    continue;
                }
                if (field.getType().equals(List.class)) {
                    field.set(val, listDecrypt((List) fieldValue, Boolean.TRUE));
                    continue;
                }
            }
        }
    }

    /**
     * 数据库字段名转驼峰
     */
    private String criterionToLowerCamel(String criterion) {
        if (StringUtils.isEmpty(criterion)) {
            return criterion;
        }
        // 消除所有空格和特殊字符
        criterion = criterion.substring(0, criterion.indexOf(" ")).trim();
        //转驼峰
        criterion = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, criterion);
        return criterion;
    }
}