package com.lbx.tradefix;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lbx.tradefix.dao")
public class TradeFixApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeFixApplication.class, args);
    }

}
