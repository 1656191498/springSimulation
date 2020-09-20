package com.azhe.mySpring.postprocessor;

import com.azhe.mySpring.annotations.Component;
import com.azhe.mySpring.annotations.ComponentScan;
import com.azhe.mySpring.applicationContext.SpringApplicationContext;
import com.azhe.mySpring.bean.BeanDefinition;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * @Description 后置处理器
 * @Author xwz
 * @Date 2020/9/18 15:55
 * @Version 1.0
 */
@ComponentScan("com.azhe.mySpring")
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanFactory(Map<String, BeanDefinition> beanDefinitionMap) {

    }

    @Override
    public void postProcessBeanDefinitionRegistry(SpringApplicationContext context) {
        context.getBeanDefinition().forEach((key , value) ->{
            //查看beandefinition上面的注解
            Annotation componentScan = value.getBeanClass().getDeclaredAnnotation(ComponentScan.class);
            if(componentScan !=null && componentScan instanceof ComponentScan){
                String packageSearchPath = ((ComponentScan) componentScan).value();
                //将包名转换成文件路径
                String path = this.getClass().getResource("/").getPath().replaceFirst("test-classes","classes");
                String finalPath = path+packageSearchPath.replace(".", "/");
                //遍历该文件路径
                //获取路径和类文件
                List<String> strings = fileToString(finalPath, packageSearchPath, null);
                //遍历值转换为beanDefinetion
                strings.stream().forEach(clazzName -> {
                    try {
                        Class clazz = Class.forName(clazzName);
                        Annotation component = clazz.getDeclaredAnnotation(Component.class);
                        if(component !=null && component instanceof Component){
                            context.register(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                });
//                System.out.println(value1);
            }
        });
    }

    /**
     * 将文件转成全类名
     * @param path
     * @param packageName
     * @param list
     * @return
     */
    public List<String> fileToString(String path, String packageName, List<String> list){
        if(list==null){
            list = new ArrayList<>();
        }
        //判断是否扫描过
        if(SpringApplicationContext.packageNameSet.contains(path)){
            return list;
        }
        SpringApplicationContext.packageNameSet.add(path);
        File file = new File(path);
        //判断文件是否存在
        if(file.exists()){
            //如果该文件下有class文件，则提取名字
            //如果是文件夹，则继续遍历，将packageName加上文件名
            if(file.isDirectory()&&file.canRead()){
                for(File files: file.listFiles()){
                    //判断是否是文件夹
                    if(files.isDirectory()){
                        fileToString(files.getPath(),packageName+"."+files.getName(),list);
                    }else {
                        boolean matches = ConfigurationClassPostProcessor.wildcardMatch("*.class", files.getName());
                        if(matches){
                            list.add(packageName+"."+files.getName().replaceFirst(".class",""));
                        }
                    }
                }
            }else{
                System.out.println(file.getPath()+"不是文件夹或可读");
            }
        }
        return list;
    }

    /**
     * 通配符匹配
     * @param pattern    通配符模式
     * @param str    待匹配的字符串
     * @return    匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                //通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1),
                            str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                //通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    //表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return (strIndex == strLength);
    }
}
