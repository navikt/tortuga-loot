package no.nav.opptjening.loot;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import no.nav.common.KafkaEnvironment;
import no.nav.opptjening.schema.Fastlandsinntekt;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.opptjening.schema.Svalbardinntekt;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class PensjonsgivendeInntektToInntektSkattLagreInntektIT {

    private static final List<String> TOPICS = Collections.singletonList(KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC);
    private static final int NUMBER_OF_BROKERS = 3;
    private static KafkaEnvironment kafkaEnvironment;
    private static KafkaConfiguration kafkaConfiguration;
    private PensjonsgivendeInntektConsumer pensjonsgivendeInntektConsumer;

    @Before
    public void setUp() {

        kafkaEnvironment = new KafkaEnvironment(NUMBER_OF_BROKERS, TOPICS, true, false, false);
        kafkaEnvironment.start();

        Map<String, String> env = new HashMap<>();
        env.put(KafkaConfiguration.Properties.BOOTSTRAP_SERVERS, kafkaEnvironment.getBrokersURL());
        env.put(KafkaConfiguration.Properties.SECURITY_PROTOCOL, "PLAINTEXT");
        env.put(KafkaConfiguration.Properties.SCHEMA_REGISTRY_URL, kafkaEnvironment.getServerPark().getSchemaregistry().getUrl());
        kafkaConfiguration = new KafkaConfiguration(env);
        pensjonsgivendeInntektConsumer = new PensjonsgivendeInntektConsumer(kafkaConfiguration.getPensjonsgivendeInntektConsumer());

    }

    @After
    public void tearDown() { kafkaEnvironment.stop(); }

    @Test
    public void consumeFromPensjonsgivendeInntektTopicAndCallInntektSkattLagreBeregnetSkatt() {
        List<PensjonsgivendeInntekt> pensjonsgivendeInntektList = getPensjonsgivendeInntektList();
        createTestRecords(pensjonsgivendeInntektList);
        final Application app = new Application(pensjonsgivendeInntektConsumer);
        //TODO: full component test
    }

    @Test
    public void consumeFromPensjonsgivendeInntektTopicOk() {
        List<PensjonsgivendeInntekt> initialPensjonsgivendeInntektList = getPensjonsgivendeInntektList();
        createTestRecords(initialPensjonsgivendeInntektList);
        List<PensjonsgivendeInntekt> pensjonsgivendeInntektList = pensjonsgivendeInntektConsumer.poll();
        //TODO: Control how many records are polled by specifying partition, and thus creating a better assert
    }

    private void createTestRecords(List<PensjonsgivendeInntekt> pensjonsgivendeInntektList) {
        String topic = KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC;

        Producer<String, PensjonsgivendeInntekt> pensjonsgivendeInntektProducer = getPensjonsgivendeInntektProducer();

        List<ProducerRecord<String, PensjonsgivendeInntekt>> pensjonsgivendeInntektRecords = Arrays.asList(
                new ProducerRecord<>(topic, "key1", pensjonsgivendeInntektList.get(0)),
                new ProducerRecord<>(topic, "key2", pensjonsgivendeInntektList.get(1)),
                new ProducerRecord<>(topic, "key3", pensjonsgivendeInntektList.get(2))
        );

        for(ProducerRecord pensjonsgivendeInntektRecord: pensjonsgivendeInntektRecords) {
            pensjonsgivendeInntektProducer.send(pensjonsgivendeInntektRecord);
        }
        pensjonsgivendeInntektProducer.flush();
        pensjonsgivendeInntektProducer.close();
    }

    private Producer<String, PensjonsgivendeInntekt> getPensjonsgivendeInntektProducer() {

        Map<String, Object> configs = new HashMap<>();
        configs.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaEnvironment.getBrokersURL());
        configs.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaEnvironment.getServerPark().getSchemaregistry().getUrl());

        Map<String, Object> producerConfig = new HashMap<>(configs);
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
        producerConfig.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);

        return new KafkaProducer<>(producerConfig);
    }

    private List<PensjonsgivendeInntekt> getPensjonsgivendeInntektList() {

        Fastlandsinntekt fastlandsinntekt1 = new Fastlandsinntekt(1000L, 1000L, 1000L, 1000L);
        Fastlandsinntekt fastlandsinntekt2 = new Fastlandsinntekt(0L, 0L, 0L, 0L);
        Fastlandsinntekt fastlandsinntekt3 = new Fastlandsinntekt(9999L, 0L, 0L, 1002L);

        Svalbardinntekt svalbardinntekt1 = new Svalbardinntekt(0L, 0L);
        Svalbardinntekt svalbardinntekt2 = new Svalbardinntekt(1000L, 100000L);
        Svalbardinntekt svalbardinntekt3 = new Svalbardinntekt(0L, 11113131L);

        return Arrays.asList(
                new PensjonsgivendeInntekt("12345678901", "2017", fastlandsinntekt1, svalbardinntekt1),
                new PensjonsgivendeInntekt("12345678902", "2017", fastlandsinntekt2, svalbardinntekt2),
                new PensjonsgivendeInntekt("12345678903", "2018", fastlandsinntekt3, svalbardinntekt3)
        );
    }
}
