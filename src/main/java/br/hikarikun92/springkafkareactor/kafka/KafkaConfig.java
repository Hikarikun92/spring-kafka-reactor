package br.hikarikun92.springkafkareactor.kafka;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.SenderOptions;

import java.util.List;

@Configuration
public class KafkaConfig {
    @Bean
    public ReactiveKafkaProducerTemplate<String, String> producerTemplate(KafkaProperties properties) {
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(properties.buildProducerProperties()));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, String> consumerTemplate(KafkaProperties properties) {
        final ReceiverOptions<String, String> options = ReceiverOptions.<String, String>create(properties.buildConsumerProperties())
                .subscription(List.of("example"));
        return new ReactiveKafkaConsumerTemplate<>(options);
    }
}
