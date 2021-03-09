package com.tanhua.server.test;

import com.tanhua.commons.templates.OssTemplate;
import com.tanhua.commons.templates.SmsTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ossTest {
    @Autowired
    private OssTemplate ossTemplate;

    @Test
    public void ossTest() throws FileNotFoundException {
        System.out.println(ossTemplate.upload("test.jpg", new FileInputStream("D:\\1.jpg")));
    }
}

