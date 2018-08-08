package no.nav.opptjening.loot;

import no.nav.opptjening.schema.Fastlandsinntekt;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.opptjening.schema.Svalbardinntekt;
import no.nav.popp.tjenester.inntektskatt.v1.meldinger.LagreBeregnetSkattRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static no.nav.opptjening.loot.LagreBeregnetSkattRequestMapper.recordsToRequestList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LagreBeregnetSkattRequestMapperTest {

    private final int partition = 0;
    private final TopicPartition topicPartition = new TopicPartition(KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC, partition);

    @Test
    public void recordsToRequestListInntektsAarAndPersonIdentMappedOk() {
        ConsumerRecords<String, PensjonsgivendeInntekt> testConsumerRecords = getTestConsumerRecords();
        List<ConsumerRecord<String, PensjonsgivendeInntekt>> consumerRecordList = testConsumerRecords.records(topicPartition);
        List<LagreBeregnetSkattRequest> requestList = recordsToRequestList(testConsumerRecords);

        assertEquals(consumerRecordList.get(0).key(), requestList.get(0).getInntektsaar() + "-" + requestList.get(0).getPersonIdent());
        assertEquals(consumerRecordList.get(1).key(), requestList.get(1).getInntektsaar() + "-" + requestList.get(1).getPersonIdent());
        assertEquals(consumerRecordList.get(2).key(), requestList.get(2).getInntektsaar() + "-" + requestList.get(2).getPersonIdent());
        assertEquals(consumerRecordList.get(3).key(), requestList.get(3).getInntektsaar() + "-" + requestList.get(3).getPersonIdent());
        assertEquals(consumerRecordList.get(4).key(), requestList.get(4).getInntektsaar() + "-" + requestList.get(4).getPersonIdent());
        assertEquals(consumerRecordList.get(5).key(), requestList.get(5).getInntektsaar() + "-" + requestList.get(5).getPersonIdent());
    }

    @Test
    public void recordsToRequestListInntektSkattCanBeNull() {
        ConsumerRecords<String, PensjonsgivendeInntekt> testConsumerRecords = getTestConsumerRecords();
        List<LagreBeregnetSkattRequest> requestList = recordsToRequestList(testConsumerRecords);

        assertNull(requestList.get(3).getInntektSkatt());
        assertNull(requestList.get(4).getInntektSkatt());
        assertNull(requestList.get(5).getInntektSkatt());
    }

    private ConsumerRecords<String, PensjonsgivendeInntekt> getTestConsumerRecords() {

        List<PensjonsgivendeInntekt> pensjonsgivendeInntektList = getPensjonsgivendeInntektList();

        String topic = KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC;
        long offset = 0L;

        List<ConsumerRecord<String, PensjonsgivendeInntekt>> consumerRecordList = Arrays.asList(
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678901", pensjonsgivendeInntektList.get(0)),
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678902", pensjonsgivendeInntektList.get(1)),
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678903", pensjonsgivendeInntektList.get(2)),
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678904", pensjonsgivendeInntektList.get(3)),
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678905", pensjonsgivendeInntektList.get(4)),
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678906", pensjonsgivendeInntektList.get(5))
        );

        Map<TopicPartition, List<ConsumerRecord<String, PensjonsgivendeInntekt>>> map = new HashMap<>();
        map.put(topicPartition, consumerRecordList);
        return new ConsumerRecords<>(map);
    }

    private List<PensjonsgivendeInntekt> getPensjonsgivendeInntektList() {

        Fastlandsinntekt fastlandsinntekt = new Fastlandsinntekt(0L, 123000L, 16700L, 99000L);
        Fastlandsinntekt fastlandsinntekt2 = new Fastlandsinntekt(0L, 56000L, 200000L, 99000L);
        Fastlandsinntekt fastlandsinntekt3 = new Fastlandsinntekt(0L, 1230000L, 2900L, 99000L);

        Svalbardinntekt svalbardinntekt = new Svalbardinntekt(250000L, 500000L);
        Svalbardinntekt svalbardinntekt2 = new Svalbardinntekt(500000L, 250000L);
        Svalbardinntekt svalbardinntekt3 = new Svalbardinntekt(333000L, 333000L);

        return Arrays.asList(
                new PensjonsgivendeInntekt("10097045457", "2017", fastlandsinntekt, svalbardinntekt),
                new PensjonsgivendeInntekt("10098045457", "2018", fastlandsinntekt2, svalbardinntekt2),
                new PensjonsgivendeInntekt("10099045457", "2019", fastlandsinntekt3, svalbardinntekt3),
                null,
                null,
                null
        );
    }

}
