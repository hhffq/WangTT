package com.changgou.business.listener;

import com.changgou.business.config.RabbitMqConfig;
import okhttp3.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author lht
 * @date 2025-08-08 23:11
 */

@Component
public class AdListener {

    @RabbitListener(queues = RabbitMqConfig.AD_UPDATE_QUEUE_POSITION)
    public void receive(String massage) {
        System.out.println("接受到的消息为：" + massage);

        // 接受消息后发送远程调用
        OkHttpClient okHttpClient = new OkHttpClient();

        String url = "http://192.168.200.128/ad_update?position=" + massage;

        Request request = new Request
                .Builder()
                .url(url)
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure( Call call,  IOException e) {

                // 请求失败
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call,  Response response) throws IOException {

                // 请求成功
                String message = response.message();
                System.out.println("请求成功:" +message );

            }
        });
    }

}
