package no.nav.opptjening.loot;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import no.nav.common.KafkaEnvironment;
import no.nav.opptjening.schema.Fastlandsinntekt;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.opptjening.schema.Svalbardinntekt;
import no.nav.popp.tjenester.inntektskatt.v1.meldinger.LagreBeregnetSkattRequest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

import static no.nav.opptjening.loot.LagreBeregnetSkattRequestMapper.recordsToRequestList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PensjonsgivendeInntektToInntektSkattIT {

    private KafkaEnvironment kafkaEnvironment;
    private final List<String> topics = Collections.singletonList(KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC);
    private PensjonsgivendeInntektConsumer pensjonsgivendeInntektConsumer;
    private final TopicPartition topicPartition = new TopicPartition(KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC, 0);

    @Before
    public void setUp() {
        kafkaEnvironment = new KafkaEnvironment(3, topics, true, false, false);
        kafkaEnvironment.start();

        Map<String, String> env = new HashMap<>();
        env.put(KafkaConfiguration.Properties.BOOTSTRAP_SERVERS, kafkaEnvironment.getBrokersURL());
        env.put(KafkaConfiguration.Properties.SCHEMA_REGISTRY_URL, kafkaEnvironment.getServerPark().getSchemaregistry().getUrl());
        env.put(KafkaConfiguration.Properties.SECURITY_PROTOCOL, "PLAINTEXT");

        KafkaConfiguration kafkaConfiguration = new KafkaConfiguration(env);

        pensjonsgivendeInntektConsumer = new PensjonsgivendeInntektConsumer(kafkaConfiguration.getPensjonsgivendeInntektConsumer());
    }

    @After
    public void tearDown() {
        kafkaEnvironment.stop();
    }

    @Test
    public void consumeFromAllPartitionsInPensjonsgivendeInntektTopicOk() {
        List<ProducerRecord<String, PensjonsgivendeInntekt>> testRecords = getTestRecords(getPensjonsgivendeInntektList());
        sendTestRecordsToTopic(testRecords);

        CountDownLatch expectedRecordsPolledLatch = new CountDownLatch(testRecords.size());
        List<ConsumerRecord<String, PensjonsgivendeInntekt>> recordsFromAllPartitions = getRecordsFromAllPartitions(expectedRecordsPolledLatch);

        testRecords.sort(Comparator.comparing(ProducerRecord::key));
        recordsFromAllPartitions.sort(Comparator.comparing(ConsumerRecord::key));

        assertEquals(testRecords.size(), recordsFromAllPartitions.size());

        IntStream.range(0, recordsFromAllPartitions.size()).forEach(i -> {
            assertEquals(testRecords.get(i).key(), recordsFromAllPartitions.get(i).key());
            assertEquals(testRecords.get(i).value(), recordsFromAllPartitions.get(i).value());
        });
    }

    @Test
    public void pensjonsgivendeInntektTopicToLagreBeregnetSkattRequest() {
        List<ProducerRecord<String, PensjonsgivendeInntekt>> testRecords = getTestRecords(getPensjonsgivendeInntektList());
        sendTestRecordsToTopic(testRecords);

        CountDownLatch expectedRecordsPolledLatch = new CountDownLatch(testRecords.size());
        List<ConsumerRecord<String, PensjonsgivendeInntekt>> recordsFromAllPartitions = getRecordsFromAllPartitions(expectedRecordsPolledLatch);

        testRecords.sort(Comparator.comparing(ProducerRecord::key));
        recordsFromAllPartitions.sort(Comparator.comparing(ConsumerRecord::key));

        ConsumerRecords<String, PensjonsgivendeInntekt> consumerRecords = consumerRecordListToConsumerRecords(recordsFromAllPartitions);
        List<LagreBeregnetSkattRequest> requestList = recordsToRequestList(consumerRecords);

        IntStream.range(0, requestList.size()).forEach(i -> {
            assertEquals(testRecords.get(i).key(),
                    String.format("%s-%s", requestList.get(i).getInntektsaar(), requestList.get(i).getPersonIdent()));
        });
    }

    private List<ConsumerRecord<String, PensjonsgivendeInntekt>> getRecordsFromAllPartitions(CountDownLatch expectedRecordsPolledLatch) {
        List<ConsumerRecord<String, PensjonsgivendeInntekt>> recordsFromAllPartitions = new ArrayList<>();

        while(expectedRecordsPolledLatch.getCount() > 0) {
            ConsumerRecords<String, PensjonsgivendeInntekt> pensjonsgivendeInntektRecords = pensjonsgivendeInntektConsumer.poll(10000);
            for(ConsumerRecord<String, PensjonsgivendeInntekt> record: pensjonsgivendeInntektRecords) {
                recordsFromAllPartitions.add(record);
                expectedRecordsPolledLatch.countDown();
            }
        }
        return recordsFromAllPartitions;
    }

    private ConsumerRecords<String, PensjonsgivendeInntekt> consumerRecordListToConsumerRecords(List<ConsumerRecord<String, PensjonsgivendeInntekt>> consumerRecordList) {
        Map<TopicPartition, List<ConsumerRecord<String, PensjonsgivendeInntekt>>> map = new HashMap<>();
        map.put(topicPartition, consumerRecordList);
        return new ConsumerRecords<>(map);
    }

    private void sendTestRecordsToTopic(List<ProducerRecord<String, PensjonsgivendeInntekt>> recordList) {
        Producer<String, PensjonsgivendeInntekt> producer = createDummyProducer();

        for(ProducerRecord record: recordList) {
            producer.send(record);
        }
        producer.flush();
    }

    private List<ProducerRecord<String, PensjonsgivendeInntekt>> getTestRecords(List<PensjonsgivendeInntekt> pensjonsgivendeInntektList) {
        String topic = KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC;

        return Arrays.asList(
                new ProducerRecord<>(topic,"2017-10097045457", pensjonsgivendeInntektList.get(0)),
                new ProducerRecord<>(topic,"2018-10098045457", pensjonsgivendeInntektList.get(1)),
                new ProducerRecord<>(topic,"2017-10099045457", pensjonsgivendeInntektList.get(2)),
                new ProducerRecord<>(topic,"2018-12345678904", pensjonsgivendeInntektList.get(3)),
                new ProducerRecord<>(topic,"2017-12345678905", pensjonsgivendeInntektList.get(4)),
                new ProducerRecord<>(topic,"2017-12345678906", pensjonsgivendeInntektList.get(5))
        );
    }
    
    private List<PensjonsgivendeInntekt> getPensjonsgivendeInntektList() {
        
        Fastlandsinntekt fastlandsinntekt = new Fastlandsinntekt(null, 123000L, 16700L, 99000L);
        Fastlandsinntekt fastlandsinntekt2 = new Fastlandsinntekt(null, 56000L, 200000L, 99000L);
        Fastlandsinntekt fastlandsinntekt3 = new Fastlandsinntekt(null, 1230000L, 2900L, 99000L);
        
        Svalbardinntekt svalbardinntekt = new Svalbardinntekt(250000L, null);
        Svalbardinntekt svalbardinntekt2 = new Svalbardinntekt(null, 250000L);
        Svalbardinntekt svalbardinntekt3 = new Svalbardinntekt(null, null);
        
        return Arrays.asList(
            new PensjonsgivendeInntekt("10097045457", "2017", fastlandsinntekt, svalbardinntekt), 
            new PensjonsgivendeInntekt("10098045457", "2018", fastlandsinntekt2, svalbardinntekt2),
            new PensjonsgivendeInntekt("10099045457", "2019", fastlandsinntekt3, svalbardinntekt3),
            null,
            null,
            null
        );
    }

    private Producer<String, PensjonsgivendeInntekt> createDummyProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", kafkaEnvironment.getBrokersURL());
        producerProperties.put("key.serializer", StringSerializer.class);
        producerProperties.put("value.serializer", KafkaAvroSerializer.class);
        producerProperties.put("schema.registry.url", kafkaEnvironment.getServerPark().getSchemaregistry().getUrl());

        return new KafkaProducer<>(producerProperties);
    }
}
