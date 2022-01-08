package br.hikarikun92.springkafkareactor.rest;

import br.hikarikun92.springkafkareactor.kafka.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class KafkaRestController {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaRestController.class);

    private final KafkaProducer producer;

    public KafkaRestController(KafkaProducer producer) {
        this.producer = producer;
    }

    @PostMapping(path = "/kafka", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> publishMessage(@RequestBody MessageDto message) {
        LOGGER.info("Received message to be published: {}", message);
        return producer.send(message.getKey(), message.getValue());
    }
}
