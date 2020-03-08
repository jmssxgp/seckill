package cn.jmss2u.seckill.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xgp
 * @version 1.0
 * @date 2020/3/8 16:40
 */
@Configuration
public class DelayQueueConfig {
    public final static String DELAY_QUEUE_PER_QUEUE_TTL_NAME="delay_queue_per_queue_ttl";

    public final static String DELAY_PROCESS_QUEUE_NAME="delay_process_queue";

    public final static String DELAY_EXCHANGE_NAME="delay_exchange";

    public final static int QUEUE_EXPIRATION = 4000;

    @Bean
    Queue delayQueuePerQueueTTL(){
        return QueueBuilder.durable(DELAY_QUEUE_PER_QUEUE_TTL_NAME)
                .withArgument("x-dead-letter-exchange", DELAY_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", DELAY_PROCESS_QUEUE_NAME)
                .withArgument("x-message-ttl", QUEUE_EXPIRATION)
                .build();
    }

    @Bean
    Queue delayProcessQueue(){
        return QueueBuilder.durable(DELAY_PROCESS_QUEUE_NAME)
                .build();
    }

    @Bean
    DirectExchange delayExchange(){
        return new DirectExchange(DELAY_EXCHANGE_NAME);
    }

    @Bean
    Binding dlxBinding(Queue delayProcessQueue, DirectExchange delayExchange){
        return BindingBuilder.bind(delayProcessQueue)
                .to(delayExchange)
                .with(DELAY_PROCESS_QUEUE_NAME);
    }

}











