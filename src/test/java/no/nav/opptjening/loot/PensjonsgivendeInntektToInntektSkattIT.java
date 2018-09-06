package no.nav.opptjening.loot;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import no.nav.common.KafkaEnvironment;
import no.nav.opptjening.loot.client.EndpointSTSClientConfig;
import no.nav.opptjening.loot.client.inntektskatt.InntektSkattClient;
import no.nav.opptjening.schema.Fastlandsinntekt;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.opptjening.schema.Svalbardinntekt;
import no.nav.opptjening.schema.skatt.hendelsesliste.HendelseKey;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.streams.StreamsConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class PensjonsgivendeInntektToInntektSkattIT {

    private KafkaEnvironment kafkaEnvironment;
    private final List<String> topics = Collections.singletonList(KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private final Properties streamsConfiguration = new Properties();

    @Before
    public void setUp() {
        kafkaEnvironment = new KafkaEnvironment(3, topics, true, false, false);
        kafkaEnvironment.start();

        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEnvironment.getBrokersURL());
        streamsConfiguration.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaEnvironment.getServerPark().getSchemaregistry().getUrl());
    }

    @After
    public void tearDown() {
        kafkaEnvironment.stop();
    }

    @Test
    public void kafkaStreamProcessesCorrectRecordsAndProducesOnNewTopic() throws Exception {
        final Properties config = (Properties)streamsConfiguration.clone();

        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "tortuga-loot-streams");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Map<String, String> env = new HashMap<>();
        env.put("STS_URL", "http://localhost:" + wireMockRule.port() + "/SecurityTokenServiceProvider");
        env.put("STS_CLIENT_USERNAME", "testusername");
        env.put("STS_CLIENT_PASSWORD", "testpassword");
        env.put("INNTEKT_SKATT_URL", "http://localhost:" + wireMockRule.port() + "/popp-ws/InntektSkatt_v1");

        final InntektSkattClient inntektSkattClient = InntektSkattClient.createFromEnvironment(env, EndpointSTSClientConfig.STS_SAML_POLICY_NO_TRANSPORT_BINDING);
        final Application app = new Application(config, inntektSkattClient);

        createTestRecords();
        createMockApi();

        try {
            app.run();

            Thread.sleep(15*1000L);
        } finally {
            app.shutdown();
        }
    }

    private void createTestRecords() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaEnvironment.getBrokersURL());
        configs.put(KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaEnvironment.getServerPark().getSchemaregistry().getUrl());

        Map<String, Object> producerConfig = new HashMap<>(configs);
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        producerConfig.put(ProducerConfig.ACKS_CONFIG, "all");
        producerConfig.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);

        Producer<HendelseKey, PensjonsgivendeInntekt> producer = new KafkaProducer<>(producerConfig);

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

    private static String readTestResource(String file) throws Exception {
        URL url = PensjonsgivendeInntektToInntektSkattIT.class.getResource(file);
        if (url == null) {
            throw new RuntimeException("Could not find file " + file);
        }
        Path resourcePath = Paths.get(url.toURI());
        return new String(Files.readAllBytes(resourcePath), StandardCharsets.UTF_8);
    }

    private void createMockApi() throws Exception {
        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/SecurityTokenServiceProvider"))
                .withHeader("SOAPAction", WireMock.equalTo("\"http://docs.oasis-open.org/ws-sx/ws-trust/200512/RST/Issue\""))
                .willReturn(WireMock.okXml(readTestResource("/sts-response.xml"))));

        WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/popp-ws/InntektSkatt_v1"))
                .withHeader("SOAPAction", WireMock.equalTo("\"http://nav.no/popp/tjenester/inntektskatt/v1/inntektSkatt_v1/LagreBeregnetSkattRequest\""))
                .willReturn(WireMock.okXml(readTestResource("/popp-response.xml"))));
    }
}
