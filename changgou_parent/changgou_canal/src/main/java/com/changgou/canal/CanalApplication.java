package com.changgou.canal;




import com.xpand.starter.canal.annotation.EnableCanalClient;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author lht
 * @date ${YEAR}-${MONTH}-${DAY} ${HOUR}:${MINUTE}
 */

@EnableCanalClient //声明当前的服务是canal的客户端
@SpringBootApplication
public class CanalApplication {
    public static void main(String[] args) {

        SpringApplication.run(CanalApplication.class, args);
    }
}