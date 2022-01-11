package br.hikarikun92.springkafkareactor.util;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Helper class for integration tests involving Embedded Kafka. This class is available as a Spring Bean (which can be
 * injected with @Autowired) in Spring tests.
 */
@Component
@ConditionalOnBean(EmbeddedKafkaBroker.class)
//Only enable this class if EmbeddedKafkaBroker is present. This avoids the creation of this bean in case your Spring test does not require Kafka.
public class EmbeddedKafkaHelper {
    @Autowired
    private ConsumerFactory<?, ?> consumerFactory; //Necessary to create a test consumer

    @Autowired
    @Qualifier(EmbeddedKafkaBroker.BEAN_NAME)
    private EmbeddedKafkaBroker embeddedKafka; //Helper class for dealing with the embedded broker

    /**
     * Read the messages sent to a given topic.
     *
     * @param topic The topic to read.
     * @param <K>   The key type.
     * @param <V>   The value type.
     * @return The records that were sent to that topic (which can be iterated).
     */
    public <K, V> ConsumerRecords<K, V> readRecordsFromTopic(String topic) {
        //Create a test consumer; its groupId must not match the same of the application (otherwise this one might not receive the message)
        try (Consumer<?, ?> consumer = consumerFactory.createConsumer("test-group", null)) {
            //Subscribe to the topic
            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, topic);

            //Retrieve the records from that topic
            @SuppressWarnings("unchecked") final ConsumerRecords<K, V> records = (ConsumerRecords<K, V>) KafkaTestUtils.getRecords(consumer);
            return records;
        }
    }

    /**
     * Read a single message sent to a topic, failing the test if 0 or more than 1 message were received.
     *
     * @param topic The topic to read.
     * @param <K>   The key type.
     * @param <V>   The value type.
     * @return The record that was sent to that topic.
     */
    public <K, V> ConsumerRecord<K, V> expect1RecordFromTopic(String topic) {
        final ConsumerRecords<K, V> records = readRecordsFromTopic(topic);
        assertEquals(1, records.count());

        return records.iterator().next();
    }
}

