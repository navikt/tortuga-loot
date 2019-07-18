package no.nav.opptjening.loot;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.StreamsConfig;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;

import no.nav.common.KafkaEnvironment;
import no.nav.opptjening.loot.client.inntektskatt.InntektSkattClient;
import no.nav.opptjening.loot.sts.TokenClient;
import no.nav.opptjening.schema.Fastlandsinntekt;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.opptjening.schema.Svalbardinntekt;
import no.nav.opptjening.schema.skatt.hendelsesliste.HendelseKey;
import org.junit.jupiter.api.*;

class ComponentTest {

    private static final int NUMBER_OF_BROKERS = 2;
    private static final int WIREMOCK_SERVER_PORT = 8080;
    private static final List<String> TOPICS = Collections.singletonList(KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC);
    private static final Properties streamsConfiguration = new Properties();
    private static final String STS_TOKEN_ENDPOINT = "/rest/v1/sts/token";
    private static final String INNTEKT_SKATT_ENDPOINT = "/popp-ws/api/lagre-inntekt-skd";
    private static final WireMockServer wireMockServer = new WireMockServer(WIREMOCK_SERVER_PORT);

    private static KafkaEnvironment kafkaEnvironment;

    @BeforeAll
    static void setUp() {
        wireMockServer.start();
        kafkaEnvironment = new KafkaEnvironment(NUMBER_OF_BROKERS, TOPICS, Collections.emptyList(), true, false, Collections.emptyList(), false, new Properties());
        kafkaEnvironment.start();

        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEnvironment.getBrokersURL());
        streamsConfiguration.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaEnvironment.getSchemaRegistry().getUrl());
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
        kafkaEnvironment.tearDown();
    }

    @Test
    void kafkaStreamProcessesCorrectRecordsAndProducesOnNewTopic() throws Exception {
        Properties config = (Properties) streamsConfiguration.clone();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "tortuga-loot-streams");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Map<String, String> env = new HashMap<>();
        env.put("STS_URL", "http://localhost:" + wireMockServer.port());
        env.put("STS_CLIENT_USERNAME", "testusername");
        env.put("STS_CLIENT_PASSWORD", "testpassword");
        env.put("INNTEKT_SKATT_URL", "http://localhost:" + wireMockServer.port() + INNTEKT_SKATT_ENDPOINT);

        InntektSkattClient inntektSkattClient = new InntektSkattClient(env);
        TokenClient tokenClient = new TokenClient(env);
        Application app = new Application(config, inntektSkattClient, tokenClient);

        createTestRecords();
        createMockApi();

        try {
            app.run();

            Thread.sleep(15 * 1000L);
        } finally {
            app.shutdown();
        }
    }

    private void createTestRecords() {
        Producer<HendelseKey, PensjonsgivendeInntekt> producer = testProducer();

        Map<HendelseKey, PensjonsgivendeInntekt> hendelser = new HashMap<>();
        hendelser.put(HendelseKey.newBuilder()
                .setGjelderPeriode("2017")
                .setIdentifikator("01029804032").build(), new PensjonsgivendeInntekt("01029804032", "2017", Fastlandsinntekt.newBuilder().
                setPersoninntektBarePensjonsdel(1L).
                setPersoninntektFiskeFangstFamiliebarnehage(2L).
                setPersoninntektLoenn(3L).
                setPersoninntektNaering(4L)
                .build(), Svalbardinntekt.newBuilder()
                .setSvalbardLoennLoennstrekkordningen(5L)
                .setSvalbardPersoninntektNaering(6L).build()));
        hendelser.put(HendelseKey.newBuilder()
                .setGjelderPeriode("2017")
                .setIdentifikator("04057849687").build(), new PensjonsgivendeInntekt("04057849687", "2017", Fastlandsinntekt.newBuilder().
                setPersoninntektBarePensjonsdel(1L).
                setPersoninntektFiskeFangstFamiliebarnehage(2L).
                setPersoninntektLoenn(3L).
                setPersoninntektNaering(4L)
                .build(), Svalbardinntekt.newBuilder()
                .setSvalbardLoennLoennstrekkordningen(5L)
                .setSvalbardPersoninntektNaering(6L).build()));
        hendelser.put(HendelseKey.newBuilder()
                .setGjelderPeriode("2017")
                .setIdentifikator("09038800237").build(), new PensjonsgivendeInntekt("09038800237", "2017", Fastlandsinntekt.newBuilder().
                setPersoninntektBarePensjonsdel(1L).
                setPersoninntektFiskeFangstFamiliebarnehage(2L).
                setPersoninntektLoenn(3L).
                setPersoninntektNaering(4L)
                .build(), Svalbardinntekt.newBuilder()
                .setSvalbardLoennLoennstrekkordningen(5L)
                .setSvalbardPersoninntektNaering(6L).build()));
        hendelser.put(HendelseKey.newBuilder()
                .setGjelderPeriode("2017")
                .setIdentifikator("01029413157").build(), new PensjonsgivendeInntekt("01029413157", "2017", Fastlandsinntekt.newBuilder().
                setPersoninntektBarePensjonsdel(1L).
                setPersoninntektFiskeFangstFamiliebarnehage(2L).
                setPersoninntektLoenn(3L).
                setPersoninntektNaering(4L)
                .build(), Svalbardinntekt.newBuilder()
                .setSvalbardLoennLoennstrekkordningen(5L)
                .setSvalbardPersoninntektNaering(6L).build()));
        hendelser.put(HendelseKey.newBuilder()
                .setGjelderPeriode("2017")
                .setIdentifikator("10026300407").build(), new PensjonsgivendeInntekt("10026300407", "2017", Fastlandsinntekt.newBuilder().
                setPersoninntektBarePensjonsdel(1L).
                setPersoninntektFiskeFangstFamiliebarnehage(2L).
                setPersoninntektLoenn(3L).
                setPersoninntektNaering(4L)
                .build(), Svalbardinntekt.newBuilder()
                .setSvalbardLoennLoennstrekkordningen(5L)
                .setSvalbardPersoninntektNaering(6L).build()));
        hendelser.put(HendelseKey.newBuilder()
                .setGjelderPeriode("2017")
                .setIdentifikator("10016000383").build(), new PensjonsgivendeInntekt("10016000383", "2017", Fastlandsinntekt.newBuilder().
                setPersoninntektBarePensjonsdel(1L).
                setPersoninntektFiskeFangstFamiliebarnehage(2L).
                setPersoninntektLoenn(3L).
                setPersoninntektNaering(4L)
                .build(), Svalbardinntekt.newBuilder()
                .setSvalbardLoennLoennstrekkordningen(5L)
                .setSvalbardPersoninntektNaering(6L).build()));
        hendelser.put(HendelseKey.newBuilder()
                .setGjelderPeriode("2017")
                .setIdentifikator("04063100264").build(), new PensjonsgivendeInntekt("04063100264", "2017", Fastlandsinntekt.newBuilder().
                setPersoninntektBarePensjonsdel(1L).
                setPersoninntektFiskeFangstFamiliebarnehage(2L).
                setPersoninntektLoenn(3L).
                setPersoninntektNaering(4L)
                .build(), Svalbardinntekt.newBuilder()
                .setSvalbardLoennLoennstrekkordningen(5L)
                .setSvalbardPersoninntektNaering(6L).build()));
        hendelser.put(HendelseKey.newBuilder()
                .setGjelderPeriode("2017")
                .setIdentifikator("04116500200").build(), new PensjonsgivendeInntekt("04116500200", "2017", Fastlandsinntekt.newBuilder().
                setPersoninntektBarePensjonsdel(1L).
                setPersoninntektFiskeFangstFamiliebarnehage(2L).
                setPersoninntektLoenn(3L).
                setPersoninntektNaering(4L)
                .build(), Svalbardinntekt.newBuilder()
                .setSvalbardLoennLoennstrekkordningen(5L)
                .setSvalbardPersoninntektNaering(6L).build()));
        hendelser.put(HendelseKey.newBuilder()
                .setGjelderPeriode("2017")
                .setIdentifikator("04126200248").build(), null);

        final String topic = "aapen-opptjening-pensjonsgivendeInntekt";
        for (Map.Entry<HendelseKey, PensjonsgivendeInntekt> entry : hendelser.entrySet()) {
            producer.send(new ProducerRecord<>(topic, entry.getKey(), entry.getValue()));
        }
        producer.flush();
    }

    private Producer<HendelseKey, PensjonsgivendeInntekt> testProducer() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaEnvironment.getBrokersURL());
        configs.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaEnvironment.getSchemaRegistry().getUrl());

        Map<String, Object> producerConfig = new HashMap<>(configs);
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
        producerConfig.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);

        return new KafkaProducer<>(producerConfig);
    }

    private void createMockApi() {
        WireMock.stubFor(WireMock.get(WireMock.urlPathEqualTo(STS_TOKEN_ENDPOINT))
                .withQueryParam("grant_type", WireMock.matching("client_credentials"))
                .withQueryParam("scope", WireMock.matching("openid"))
                .willReturn(WireMock.okJson("{\"access_token\":\"eyJ4vaea3\",\"expires_in\":\"3600\",\"token_type\":\"Bearer\"}")));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo(INNTEKT_SKATT_ENDPOINT))
                .withHeader("Authorization", WireMock.matching("Bearer " + "eyJ4vaea3"))
                .willReturn(WireMock.okJson("{\"LagreBeregnetSkattResponse\":\"{}\"}"))
        );
    }
}
