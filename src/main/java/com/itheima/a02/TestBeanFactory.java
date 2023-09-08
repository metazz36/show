package com.itheima.a02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.annotation.Resource;

public class TestBeanFactory {

    public static void main(String[] args) {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // bean �Ķ��壨class, scope, ��ʼ��, ���٣�
        AbstractBeanDefinition beanDefinition =
                BeanDefinitionBuilder.genericBeanDefinition(Config.class).setScope("singleton").getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);
        System.out.println("<<<<<<<<<<<< bean����׶� <<<<<<<<<<<<<<<<<<");
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println("<<<<<<<<<<<< bean����׶� <<<<<<<<<<<<<<<<<<");
        // �� BeanFactory ���һЩ���õĺ�����
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        System.out.println("<<<<<<<<<<<< �� BeanFactory ���һЩ���õĺ����� <<<<<<<<<<<<<<<<<<");
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println("<<<<<<<<<<<< �� BeanFactory ���һЩ���õĺ����� <<<<<<<<<<<<<<<<<<");
        // BeanFactory ��������Ҫ���ܣ�������һЩ bean ���� @Bean��
        beanFactory.getBeansOfType(BeanFactoryPostProcessor.class).values().forEach(beanFactoryPostProcessor -> {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        });

        System.out.println("<<<<<<<<<<<< beanFactoryִ�к������׶� <<<<<<<<<<<<<<<<<<");
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println("<<<<<<<<<<<< beanFactoryִ�к������׶� <<<<<<<<<<<<<<<<<<");

        // Bean ������, ��� bean ���������ڵĸ����׶��ṩ��չ, ���� @Autowired @Resource ...
        beanFactory.getBeansOfType(BeanPostProcessor.class).values().stream()
                .sorted(beanFactory.getDependencyComparator())
                .forEach(beanPostProcessor -> {
            System.out.println(">>>>" + beanPostProcessor);
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        });

        System.out.println("<<<<<<<<<<<< bean��Ӻ������׶� <<<<<<<<<<<<<<<<<<");
        for (String name : beanFactory.getBeanDefinitionNames()) {
            System.out.println(name);
        }
        System.out.println("<<<<<<<<<<<< bean��Ӻ������׶� <<<<<<<<<<<<<<<<<<");

        beanFactory.preInstantiateSingletons(); // ׼�������е���
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> ");
        System.out.println(beanFactory.getBean(Bean1.class).getBean2());
        System.out.println(beanFactory.getBean(Bean1.class).getInter());
        /*
            ѧ����ʲô:
            a. beanFactory ����������
                   1. ������������ BeanFactory ������
                   2. ����������� Bean ������
                   3. ����������ʼ������
                   4. �������beanFactory ��������� ${ } �� #{ }
            b. bean ����������������߼�
         */

        System.out.println("Common:" + (Ordered.LOWEST_PRECEDENCE - 3));
        System.out.println("Autowired:" + (Ordered.LOWEST_PRECEDENCE - 2));
    }

    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }

        @Bean
        public Bean3 bean3() {
            return new Bean3();
        }

        @Bean
        public Bean4 bean4() {
            return new Bean4();
        }
    }

    interface Inter {

    }

    static class Bean3 implements Inter {

    }

    static class Bean4 implements Inter {

    }

    static class Bean1 {
        private static final Logger log = LoggerFactory.getLogger(Bean1.class);

        public Bean1() {
            log.debug("���� Bean1()");
        }

        @Autowired
        private Bean2 bean2;

        public Bean2 getBean2() {
            return bean2;
        }

        @Autowired
        @Resource
        @Qualifier(value = "bean4")
        private Inter bean3;

        public Inter getInter() {
            return bean3;
        }
    }

    static class Bean2 {
        private static final Logger log = LoggerFactory.getLogger(Bean2.class);

        public Bean2() {
            log.debug("���� Bean2()");
        }
    }
}
