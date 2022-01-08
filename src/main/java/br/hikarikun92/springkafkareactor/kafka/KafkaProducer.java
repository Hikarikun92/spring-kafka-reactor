package br.hikarikun92.springkafkareactor.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class KafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class);

    private final ReactiveKafkaProducerTemplate<String, String> producerTemplate;

    public KafkaProducer(ReactiveKafkaProducerTemplate<String, String> producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public Mono<Void> send(String key, String value) {
        return producerTemplate.send("example", key, value)
                .doOnSuccess(result -> LOGGER.info("Sent successfully"))
                .doOnError(throwable -> LOGGER.error("Error sending message", throwable))
                .then();
    }
}
