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
        //Send a message to topic "example", add some sucess and error behaviors to the callback and return an empty Mono
        //on completion. This return value can be used later, for example, returned to a REST controller or chained with
        //other behavior.
        return producerTemplate.send("example", key, value)
                .doOnSuccess(result -> LOGGER.info("Sent successfully"))
                .doOnError(throwable -> LOGGER.error("Error sending message", throwable))
                .then();
    }
}
