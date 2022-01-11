package br.hikarikun92.springkafkareactor;

import br.hikarikun92.springkafkareactor.util.EmbeddedKafkaHelper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWebTestClient
@EmbeddedKafka(topics = "example") //This annotation enables and registers a Spring Bean of type EmbeddedKafkaBroker
class KafkaIntegrationTest {
    @Autowired
    private WebTestClient client; //WebFlux client for calling a REST endpoint

    @Autowired
    private EmbeddedKafkaHelper helper;

    @Test
    void testSendMessage() {
        //This test validates the flow from the REST endpoint until the moment the Kafka producer sends the message, but
        //we're not validating (directly) that the associated consumer received it; instead, we're creating a new consumer
        //and checking its message (which should be the same as the one received by the application). To validate the
        //behavior of the real consumer, we should perform other validations; for example, if the consumer calls a method
        //from some business logic service class, we could use @SpyBean in the test and use Mockito's verify(obj).method()
        //to guarantee the method was called. This, however, would need some kind of synchronization, as consuming the
        //message is an asynchronous process. Another alternative for it is to create a simple Unit Test and check its
        //behavior by simply calling its method, using Mockito if necessary.
        final String payload = "{\"key\":\"Hello\",\"value\":\"World!\"}";

        //Call the endpoint in KafkaRestController
        client.post()
                .uri("/kafka")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(payload))
                .exchange()
                .expectStatus().isOk();

        //Retrieve the sent record and validate it
        final ConsumerRecord<String, String> record = helper.expect1RecordFromTopic("example");
        assertEquals("Hello", record.key());
        assertEquals("World!", record.value());
    }
}
