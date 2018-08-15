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
        List<ConsumerRecord<String, PensjonsgivendeInntekt>> recordList = testConsumerRecords.records(topicPartition);
        List<LagreBeregnetSkattRequest> requestList = recordsToRequestList(testConsumerRecords);

        assertEquals(recordList.get(0).key(), requestList.get(0).getInntektsaar() + "-" + requestList.get(0).getPersonIdent());
        assertEquals(recordList.get(1).key(), requestList.get(1).getInntektsaar() + "-" + requestList.get(1).getPersonIdent());
        assertEquals(recordList.get(2).key(), requestList.get(2).getInntektsaar() + "-" + requestList.get(2).getPersonIdent());
        assertEquals(recordList.get(3).key(), requestList.get(3).getInntektsaar() + "-" + requestList.get(3).getPersonIdent());
        assertEquals(recordList.get(4).key(), requestList.get(4).getInntektsaar() + "-" + requestList.get(4).getPersonIdent());
        assertEquals(recordList.get(5).key(), requestList.get(5).getInntektsaar() + "-" + requestList.get(5).getPersonIdent());
    }

    @Test
    public void recordsToRequestListInntektWithNullValues() {
        ConsumerRecords<String, PensjonsgivendeInntekt> testConsumerRecords = getTestConsumerRecords();
        List<ConsumerRecord<String, PensjonsgivendeInntekt>> recordList = testConsumerRecords.records(topicPartition);
        List<LagreBeregnetSkattRequest> requestList = recordsToRequestList(testConsumerRecords);

        assertEquals(recordList.get(0).value().getFastlandsinntekt().getPersoninntektLoenn(),
                requestList.get(0).getInntektSkatt().getPersoninntektLoenn());
        assertEquals(recordList.get(0).value().getFastlandsinntekt().getPersoninntektFiskeFangstFamiliebarnehage(),
                requestList.get(0).getInntektSkatt().getPersoninntektFiskeFangstFamilieBarnehage());
        assertEquals(recordList.get(0).value().getFastlandsinntekt().getPersoninntektNaering(),
                requestList.get(0).getInntektSkatt().getPersoninntektNaering());
        assertNull(requestList.get(0).getInntektSkatt().getPersoninntektBarePensjonsdel());
        assertNull(requestList.get(0).getInntektSkatt().getSvalbardPersoninntektNaering());
        assertNull(requestList.get(0).getInntektSkatt().getSvalbardLoennLoennstrekkordningen());

        assertEquals(recordList.get(1).value().getFastlandsinntekt().getPersoninntektLoenn(),
                requestList.get(1).getInntektSkatt().getPersoninntektLoenn());
        assertEquals(recordList.get(1).value().getFastlandsinntekt().getPersoninntektFiskeFangstFamiliebarnehage(),
                requestList.get(1).getInntektSkatt().getPersoninntektFiskeFangstFamilieBarnehage());
        assertEquals(recordList.get(1).value().getFastlandsinntekt().getPersoninntektNaering(),
                requestList.get(1).getInntektSkatt().getPersoninntektNaering());
        assertEquals(recordList.get(1).value().getFastlandsinntekt().getPersoninntektBarePensjonsdel(),
                requestList.get(1).getInntektSkatt().getPersoninntektBarePensjonsdel());
        assertEquals(recordList.get(1).value().getSvalbardinntekt().getSvalbardPersoninntektNaering(),
                requestList.get(1).getInntektSkatt().getSvalbardPersoninntektNaering());
        assertEquals(recordList.get(1).value().getSvalbardinntekt().getSvalbardLoennLoennstrekkordningen(),
                requestList.get(1).getInntektSkatt().getSvalbardLoennLoennstrekkordningen());

        assertNull(requestList.get(2).getInntektSkatt().getPersoninntektLoenn());
        assertEquals(recordList.get(2).value().getFastlandsinntekt().getPersoninntektFiskeFangstFamiliebarnehage(),
                requestList.get(2).getInntektSkatt().getPersoninntektFiskeFangstFamilieBarnehage());
        assertNull(requestList.get(2).getInntektSkatt().getPersoninntektNaering());
        assertEquals(recordList.get(2).value().getFastlandsinntekt().getPersoninntektBarePensjonsdel(),
                requestList.get(2).getInntektSkatt().getPersoninntektBarePensjonsdel());
        assertEquals(recordList.get(2).value().getSvalbardinntekt().getSvalbardPersoninntektNaering(),
                requestList.get(2).getInntektSkatt().getSvalbardPersoninntektNaering());
        assertEquals(recordList.get(2).value().getSvalbardinntekt().getSvalbardLoennLoennstrekkordningen(),
                requestList.get(2).getInntektSkatt().getSvalbardLoennLoennstrekkordningen());
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

        List<ConsumerRecord<String, PensjonsgivendeInntekt>> recordList = Arrays.asList(
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678901", pensjonsgivendeInntektList.get(0)),
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678902", pensjonsgivendeInntektList.get(1)),
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678903", pensjonsgivendeInntektList.get(2)),
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678904", pensjonsgivendeInntektList.get(3)),
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678905", pensjonsgivendeInntektList.get(4)),
                new ConsumerRecord<>(topic, partition, offset, "2017-12345678906", pensjonsgivendeInntektList.get(5))
        );

        Map<TopicPartition, List<ConsumerRecord<String, PensjonsgivendeInntekt>>> map = new HashMap<>();
        map.put(topicPartition, recordList);
        return new ConsumerRecords<>(map);
    }

    private List<PensjonsgivendeInntekt> getPensjonsgivendeInntektList() {

        Fastlandsinntekt fastlandsinntekt = new Fastlandsinntekt(0L, 123000L, 16700L, null);
        Fastlandsinntekt fastlandsinntekt2 = new Fastlandsinntekt(500L, 56000L, 200000L, 99000L);
        Fastlandsinntekt fastlandsinntekt3 = new Fastlandsinntekt(null, 1230000L, null, 99000L);

        Svalbardinntekt svalbardinntekt = new Svalbardinntekt(null, null);
        Svalbardinntekt svalbardinntekt2 = new Svalbardinntekt(500000L, null);
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
