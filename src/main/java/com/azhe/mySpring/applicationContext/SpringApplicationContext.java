package com.azhe.mySpring.applicationContext;

import com.azhe.mySpring.bean.BeanDefinition;
import com.azhe.mySpring.factory.DefaultListableBeanFactory;
import com.azhe.mySpring.postprocessor.BeanDefinitionRegistryPostProcessor;
import com.azhe.mySpring.postprocessor.ConfigurationClassPostProcessor;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
     * bean名称列表
     */
    private volatile List<String> beanDefinitionNames = new ArrayList(256);
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

        }
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
            bean.postProcessBeanDefinitionRegistry(beanDefinitionMap);
        }

    }

    /**
     *  获取，如果没有则创建bean
     * @param beanName
     */
    private <T> T getBean(String beanName,Class<T> requiredType){
        //如果单例池中没有则创建一个
        Object singletonObject = singletonObjects.get(beanName);
        if(singletonObject == null){
            try {
                BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
                singletonObject = beanDefinition.getBeanClass().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return (T) singletonObject;
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


}
