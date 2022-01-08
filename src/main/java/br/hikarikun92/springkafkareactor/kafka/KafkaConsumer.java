package br.hikarikun92.springkafkareactor.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    private final ReactiveKafkaConsumerTemplate<String, String> consumerTemplate;

    public KafkaConsumer(ReactiveKafkaConsumerTemplate<String, String> consumerTemplate) {
        this.consumerTemplate = consumerTemplate;
    }

    @Override
    public void afterPropertiesSet() {
        //You may configure your consumer behavior here in many possible ways using the Flux methods. For example, you
        //can use the first doOnNext() call to log incoming messages, then use map() to convert them to your domain
        //objects, then use another doOnNext() to call a business logic and so on.
        consumerTemplate.receive()
                .doOnNext(record -> {
                    LOGGER.info("Received message on topic {}; key: {}; value: {}", record.topic(), record.key(), record.value());
                })
                .map(record -> record.topic() + " - " + record.key() + "=" + record.value())
                .doOnNext(value -> LOGGER.info("Converted message: {}", value))
                .subscribe();
    }
}
