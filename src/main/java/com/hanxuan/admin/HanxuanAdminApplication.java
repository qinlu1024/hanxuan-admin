package com.hanxuan.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 晗煊后台管理系统启动类
 *
 * @author hanxuan
 */
@SpringBootApplication
@EnableAsync
public class HanxuanAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(HanxuanAdminApplication.class, args);
        System.out.println("""
                \n
                ==========================================
                  晗煊后台管理系统启动成功！
                  接口文档: http://localhost:8080/doc.html
                ==========================================
                """);
    }
}
