package com.tanhua.commons;

import com.tanhua.commons.properties.FaceProperties;
import com.tanhua.commons.properties.OssProperties;
import com.tanhua.commons.properties.SmsProperties;
import com.tanhua.commons.templates.FaceTemplate;
import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.commons.templates.SmsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动配置类
 * 配置工具类 template
 */
@Configuration
@EnableConfigurationProperties({SmsProperties.class, OssProperties.class, FaceProperties.class})
public class CommonsAutoConfiguration {

    /**
     * 创建发送短信的工具类，进入容器, 名称=方法名 smsTemplate
     *
     * @param smsProperties
     * @return
     * @Autowired smsTemplate
     */
    @Bean
    public SmsTemplate smsTemplate(SmsProperties smsProperties) {
        SmsTemplate smsTemplate = new SmsTemplate(smsProperties);
        smsTemplate.init();
        return smsTemplate;
    }

    /*
     * 阿里云存储操作模板
     * */
    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties) {
        return new OssTemplate(ossProperties);
    }

    /*
     * 百度云api人脸识别
     * */
    @Bean
    public FaceTemplate faceTemplate(FaceProperties faceProperties) {
        return new FaceTemplate(faceProperties);
    }
}
