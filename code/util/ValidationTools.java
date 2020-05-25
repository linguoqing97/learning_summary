package com.jdh.fuhsi.portal.util;

import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 参数校验工具类(脚手架)
 * @author zz
 * @date 2019/9/19 19:07
 **/
public class ValidationTools {

    /**
     * 使用hibernate的注解来进行验证
     */
    private static Validator validator = Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory().getValidator();

    public static <T> void validate (T obj) throws Exception {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        if (constraintViolations.size() > 0) {
            Exception e = new Exception("参数:" + constraintViolations.iterator().next().getPropertyPath() + "不合法,提示信息:" + constraintViolations.iterator().next().getMessage());
            throw e;
        }
    }

    private ValidationTools(){}

}
