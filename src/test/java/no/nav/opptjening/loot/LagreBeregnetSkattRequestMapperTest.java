package no.nav.opptjening.loot;

import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LagreBeregnetSkattRequestMapperTest {

    private final TopicPartition topicPartition = new TopicPartition(KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC, 0);

    @Test
    public void recordsToRequestListOk() {
        ConsumerRecords<String, PensjonsgivendeInntekt> testConsumerRecords = getTestConsumerRecords();
        assertEquals(0, 0);
    }

    private ConsumerRecords<String, PensjonsgivendeInntekt> getTestConsumerRecords() {
        //TODO
        return null;
    }

}
