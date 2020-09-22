package com.azhe.mySpring.applicationContext;

import com.azhe.mySpring.annotations.Autowired;
import com.azhe.mySpring.bean.BeanDefinition;
import com.azhe.mySpring.factory.DefaultListableBeanFactory;
import com.azhe.mySpring.postprocessor.BeanDefinitionRegistryPostProcessor;
import com.azhe.mySpring.postprocessor.ConfigurationClassPostProcessor;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description spring容器,简略版注解spring
 * @Author xwz
 * @Date 2020/9/18 14:24
 * @Version 1.0
 */
public class SpringApplicationContext {
//    private final DefaultListableBeanFactory beanFactory;
    private final Object startupShutdownMonitor = new Object();
    /**
     * beanDefinition容器
     */
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap(256);
    /**
     * 单例池
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /**
     * 暴露对象，二级缓存
     */
    public static Map<String,Object> earlySingletonObjects = new ConcurrentHashMap<>(256);
    /**
     * bean名称列表
     */
    private volatile List<String> beanDefinitionNames = new ArrayList(256);
    /**
     * 过滤重复扫描的包
     */
    public static Set<String> packageNameSet = new HashSet<>();
    /**
     * 是否正在创建
     */
    public static Set<String> isCreateBean = new HashSet<>();
    /**
     *
     * @param componentClasses 将配置类注册入容器中
     */
    public SpringApplicationContext(Class<?> componentClasses){
//        this.beanFactory = new DefaultListableBeanFactory();
        register(ConfigurationClassPostProcessor.class);
        register(componentClasses);
        refresh();
    }

    private void refresh() {
        synchronized (this.startupShutdownMonitor) {
            //执行PostProcessor方法
            invokeBeanFactoryPostProcessors();
            finishBeanFactoryInitialization();
        }
    }

    private void finishBeanFactoryInitialization() {
        beanDefinitionNames.forEach(beanName -> {
            getBean(beanName,null);
        });
    }

    /**
     * 执行bean工厂后置处理器
     */
    private void invokeBeanFactoryPostProcessors() {
        //获取beanDefinitionRegistryPostProcessor列表
        String[] beanNamesForType = getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class);
        //将beanNamesForType遍历
        for (String beanName:beanNamesForType){
            //创建bean，并执行方法
            BeanDefinitionRegistryPostProcessor bean = getBean(beanName, BeanDefinitionRegistryPostProcessor.class);
            bean.postProcessBeanDefinitionRegistry(this);
        }
        packageNameSet.clear();
    }

    /**
     *  获取，如果没有则创建bean
     * @param beanName
     */
    private <T> T getBean(String beanName,Class<T> requiredType){
        //如果单例池中没有则创建一个
        Object singletonObject = doGetBean(beanName);
        return (T) singletonObject;
    }

    /**
     * 真正创建bean
     * @param beanName
     * @return
     */
    private Object doGetBean(String beanName){
        Object singletonObject = singletonObjects.get(beanName);
        if(singletonObject == null){
            //如果正在创建的话
            if(isCreateBean.contains(beanName)){
                //从二级缓存中拿
                return earlySingletonObjects.get(beanName);
            }else {
                isCreateBean.add(beanName);
            }
            try {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                singletonObject = beanDefinition.getBeanClass().newInstance();
                earlySingletonObjects.put(beanName,singletonObject);
                //给属性赋值
                Field[] declaredFields = singletonObject.getClass().getDeclaredFields();
                populateBean(singletonObject,declaredFields);
                singletonObjects.put(beanName,singletonObject);
                //将该bean从缓存池中移除
                earlySingletonObjects.remove(beanName);
                isCreateBean.remove(beanName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return singletonObject;
    }

    /**
     * 属性赋值
     * @param singletonObject
     * @param declaredFields
     */
    private void populateBean(Object singletonObject, Field[] declaredFields) {
        try {
            //遍历所有字段看是否有Autowired,按照类型注入
            for (Field field:declaredFields){
                //还要解决循环依赖
                if(field.isAnnotationPresent(Autowired.class)){
                    field.setAccessible(true);
//                    String beanNameN = generateBeanName(field.getType());
                    //按照类型匹配
                    List<String> populateBean = getPopulateBean(field.getType());
                    String beanNameN = field.getName();
                    if(populateBean.size()==0){
                        throw new RuntimeException("不能够自动注入该类型:"+beanNameN);
                    }
                    String beanName = beanNameN;
                    //多个实现取属性名相等的
                    if(populateBean.size()>1){
                        beanName = populateBean.stream().filter(bean -> bean.equals(beanNameN)).findFirst().orElse(null);
                    }
                    if(beanName!=null){
                        field.set(singletonObject,doGetBean(beanName));
                    }else {
                        throw new RuntimeException("容器中不存在该类型:"+beanNameN);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按照类型匹配字段,可能存在多个实现
     * @param clazz
     * @return
     */
    private List<String> getPopulateBean(Class clazz){
        List<String> list = new ArrayList<>();
        for (String beanName:beanDefinitionNames){
            Object bean = doGetBean(beanName);
            if(clazz.isAssignableFrom(bean.getClass())){
                list.add(beanName);
            }
        }
        return list;
    }

    /**
     * 生成beanName
     * @param clazz
     * @return
     */
    public String generateBeanName(Class clazz){
        return clazz.getSimpleName().replaceFirst(clazz.getSimpleName().substring(0, 1), clazz.getSimpleName().substring(0, 1).toLowerCase());
    }

    /**
     * 遍历所有beanDefinition获取改type类型的字符串数组
     * @param type
     * @return
     */
    public String[] getBeanNamesForType(Class<?> type){
        List<String> result = new ArrayList<>();
        for(String beanName:beanDefinitionNames){
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if( type.isAssignableFrom(beanDefinition.getBeanClass())){
                result.add(beanName);
            }
        }
        return result.toArray(new String[0]);
    }

    /**
     * 注册类
     * @param componentClasses
     */
    public void register(Class<?> componentClasses){
        //1.先将该类定义成beanDefinition
        BeanDefinition beanDefinition = new BeanDefinition(componentClasses);
        //2.将该类的类名第一个字母小写当作改bean的名称
        String beanName = componentClasses.getSimpleName().replaceFirst(componentClasses.getSimpleName().substring(0,1),componentClasses.getSimpleName().substring(0,1).toLowerCase());
        //3.注册beanDefinition
        registerBeanDefinition(beanName,beanDefinition);
    }

    /**
     * 将我们的registerBeanDefinition注册入容器中
     * @param beanName
     * @param beanDefinition
     */
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition){
        //1.判断我们容器的单例池中是否有改beanDefinition
        BeanDefinition existingDefinition = this.beanDefinitionMap.get(beanName);
        if(existingDefinition==null){
            this.beanDefinitionMap.put(beanName,beanDefinition);
            beanDefinitionNames.add(beanName);
        }
    }

    public Map<String, BeanDefinition> getBeanDefinition(){
        return beanDefinitionMap;
    }

}
