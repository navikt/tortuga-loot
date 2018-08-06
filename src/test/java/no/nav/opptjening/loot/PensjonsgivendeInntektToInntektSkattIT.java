package no.nav.opptjening.loot;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import no.nav.common.KafkaEnvironment;
import no.nav.opptjening.schema.Fastlandsinntekt;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.opptjening.schema.Svalbardinntekt;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class PensjonsgivendeInntektToInntektSkattIT {

    private KafkaEnvironment kafkaEnvironment;
    private final List<String> topics = Collections.singletonList(KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC);
    private PensjonsgivendeInntektConsumer pensjonsgivendeInntektConsumer;
    private final TopicPartition topicPartition = new TopicPartition(KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC, 0);

    @Before
    public void setUp() {
        kafkaEnvironment = new KafkaEnvironment(1, topics, true, false, false);
        kafkaEnvironment.start();

        Map<String, String> env = new HashMap<>();
        env.put(KafkaConfiguration.Properties.BOOTSTRAP_SERVERS, kafkaEnvironment.getBrokersURL());
        env.put(KafkaConfiguration.Properties.SCHEMA_REGISTRY_URL, kafkaEnvironment.getServerPark().getSchemaregistry().getUrl());
        env.put(KafkaConfiguration.Properties.SECURITY_PROTOCOL, "PLAINTEXT");

        KafkaConfiguration kafkaConfiguration = new KafkaConfiguration(env);

        sendTestRecordsToTopic();

        pensjonsgivendeInntektConsumer = new PensjonsgivendeInntektConsumer(kafkaConfiguration.getPensjonsgivendeInntektConsumer());
    }

    @After
    public void tearDown() {
        kafkaEnvironment.stop();
    }

    @Test
    public void consumeFromPensjonsgivendeInntektTopicOk() {
        // Om testen feiler kan timeout være for lav
        ConsumerRecords<String, PensjonsgivendeInntekt> pensjonsgivendeInntektRecords = pensjonsgivendeInntektConsumer.poll(10000);
        assertEquals( 6, pensjonsgivendeInntektRecords.count());

        List<ConsumerRecord<String, PensjonsgivendeInntekt>> consumerRecordList = pensjonsgivendeInntektRecords.records(topicPartition);
        assertEquals("2017-12345678906", consumerRecordList.get(5).key());
    }

    private void sendTestRecordsToTopic() {
        Producer<String, PensjonsgivendeInntekt> producer = createDummyProducer();
        List<PensjonsgivendeInntekt> pensjonsgivendeInntektList = getPensjonsgivendeInntektList();
        String topic = KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC;

        producer.send(new ProducerRecord<>(topic,"2017-10097045457", pensjonsgivendeInntektList.get(0)));
        producer.send(new ProducerRecord<>(topic,"2018-10098045457", pensjonsgivendeInntektList.get(1)));
        producer.send(new ProducerRecord<>(topic,"2017-10099045457", pensjonsgivendeInntektList.get(2)));
        producer.send(new ProducerRecord<>(topic,"2018-12345678904", pensjonsgivendeInntektList.get(3)));
        producer.send(new ProducerRecord<>(topic,"2017-12345678905", pensjonsgivendeInntektList.get(4)));
        producer.send(new ProducerRecord<>(topic,"2017-12345678906", pensjonsgivendeInntektList.get(5)));
        producer.flush();

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

    private Producer<String, PensjonsgivendeInntekt> createDummyProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", kafkaEnvironment.getBrokersURL());
        producerProperties.put("key.serializer", StringSerializer.class);
        producerProperties.put("value.serializer", KafkaAvroSerializer.class);
        producerProperties.put("schema.registry.url", kafkaEnvironment.getServerPark().getSchemaregistry().getUrl());

        return new KafkaProducer<String, PensjonsgivendeInntekt>(producerProperties);
    }
}
