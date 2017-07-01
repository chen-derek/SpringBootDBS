package com.chj.datasource;

import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(-10)
// 保证该AOP在@Transactional之前执行
public class DynamicDataSourceAspect {

	final static Logger logger = LoggerFactory.getLogger(DynamicDataSourceAspect.class);
	
	@Pointcut("execution(* com.chj.persistence..*.*(..))) or execution(* com.chj.service..*.*(..)))")
	public void pointCut(){
		
	}

	/*
	 * @Before("@annotation(ds)") 的意思是：
	 * 
	 * @Before：在方法执行之前进行执行：
	 * 
	 * @annotation(targetDataSource)： 会拦截注解targetDataSource的方法，否则不拦截;
	 */
	@Before("pointCut()")
	public void doBefore(JoinPoint point) throws Exception {
		Class<?> target = point.getTarget().getClass();
		MethodSignature signature = (MethodSignature) point.getSignature();
		// 默认使用目标类型的注解，如果没有则使用其实现接口的注解
		for (Class<?> clazz : target.getInterfaces()) {
			resolveDataSource(clazz, signature.getMethod());
		}
		resolveDataSource(target, signature.getMethod());
	}

	@After("pointCut()")
	public void doAfter(JoinPoint point) throws Exception {
		DynamicDataSourceContextHolder.clearDataSource();
	}

	/**
	 * 提取目标对象方法注解和类型注解中的数据源标识
	 * 
	 * @param clazz
	 * @param method
	 */
	private void resolveDataSource(Class<?> clazz, Method method) {
		try {
			Class<?>[] types = method.getParameterTypes();
			// 默认使用类型注解
			if (clazz.isAnnotationPresent(TargetDataSource.class)) {
				TargetDataSource source = clazz.getAnnotation(TargetDataSource.class);
				DynamicDataSourceContextHolder.setDataSource(getDataSource(source));
			}
			// 方法注解可以覆盖类型注解
			Method m = clazz.getMethod(method.getName(), types);
			if (m != null && m.isAnnotationPresent(TargetDataSource.class)) {
				TargetDataSource source = m.getAnnotation(TargetDataSource.class);
				DynamicDataSourceContextHolder.setDataSource(getDataSource(source));
			}
		} catch (Exception e) {
			logger.error(clazz + ":" + e.getMessage());
		}
	}

	private String getDataSource(TargetDataSource source) {
		String dataSource = source.value();
		if (StringUtils.isNotBlank(dataSource)) {
			return dataSource;
		}
		return DynamicDataSource.getDefaultDataSource();
	}

}
