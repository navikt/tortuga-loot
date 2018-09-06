package no.nav.opptjening.loot;

import io.prometheus.client.Counter;
import no.nav.opptjening.loot.client.inntektskatt.InntektSkattClient;
import no.nav.opptjening.nais.NaisHttpServer;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.opptjening.schema.skatt.hendelsesliste.HendelseKey;
import no.nav.popp.tjenester.inntektskatt.v1.LagreBeregnetSkattSikkerhetsbegrensning;
import no.nav.popp.tjenester.inntektskatt.v1.LagreBeregnetSkattUgyldigInput;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private final InntektSkattClient inntektSkattClient;

    private final KafkaStreams streams;

    private final NaisHttpServer naisHttpServer = new NaisHttpServer();

    private volatile boolean shutdown = false;

    private static final Counter pensjonsgivendeInntekterProcessedTotal = Counter.build()
            .name("pensjonsgivende_inntekter_processed_total")
            .help("Antall pensjonsgivende inntekter prosessert").register();
    private static final Counter pensjonsgivendeInntekterProcessed = Counter.build()
            .name("pensjonsgivende_inntekter_processed")
            .labelNames("year")
            .help("Antall pensjonsgivende inntekter prosessert").register();

    private static final boolean dryRun;

    static {
        dryRun = "true".equalsIgnoreCase(System.getenv().getOrDefault("DRY_RUN", "false"));
    }

    public static void main(String[] args) {
        final Application app;

        try {
            Map<String, String> env = System.getenv();

            KafkaConfiguration kafkaConfiguration = new KafkaConfiguration(env);

            InntektSkattClient inntektSkattClient = InntektSkattClient.createFromEnvironment(env);

            app = new Application(kafkaConfiguration.streamsConfiguration(), inntektSkattClient);
            app.startHttpServer();
            app.run();
        } catch (Throwable e) {
            LOG.error("Application failed to start", e);
            System.exit(1);
        }
    }

    public Application(Properties streamsProperties,
                       InntektSkattClient inntektSkattClient) {
        this.inntektSkattClient = inntektSkattClient;

        StreamsBuilder builder = new StreamsBuilder();

        createStream(builder);

        streams = new KafkaStreams(builder.build(), streamsProperties);
        streams.setUncaughtExceptionHandler((t, e) -> {
            LOG.error("Uncaught exception in thread {}, closing streams", t, e);
            shutdown();
        });

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void createStream(StreamsBuilder builder) {
        PensjonsgivendeInntektMapper pensjonsgivendeInntektMapper = new PensjonsgivendeInntektMapper();
        PensjonsgivendeInntektRecordMapper pensjonsgivendeInntektRecordMapper = new PensjonsgivendeInntektRecordMapper();

        KStream<HendelseKey, PensjonsgivendeInntekt> stream = builder.stream(KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC);
        stream.mapValues((readOnlyKey, value) -> pensjonsgivendeInntektMapper.mapToInntektSkatt(value))
                .mapValues((readOnlyKey, value) -> pensjonsgivendeInntektRecordMapper.mapToLagreBeregnetSkattRequest(readOnlyKey.getGjelderPeriode(),
                        readOnlyKey.getIdentifikator(), value))
                .foreach((key, value) -> {
                    try {
                        pensjonsgivendeInntekterProcessed.labels(value.getInntektsaar()).inc();
                        pensjonsgivendeInntekterProcessedTotal.inc();

                        LOG.debug("Saving record={} for key={}", value, key);
                        if (dryRun) {
                            LOG.info("Skipping because dryRun");
                        } else {
                            inntektSkattClient.lagreBeregnetSkatt(value);
                        }
                    } catch (LagreBeregnetSkattUgyldigInput e) {
                        LOG.error("Ugyldig input til InntektSkatt.lagreBeregnetSkatt", e);
                        throw new RuntimeException(e);
                    } catch (LagreBeregnetSkattSikkerhetsbegrensning e) {
                        LOG.error("LagreBeregnetSkattSikkerhetsbegrensning", e);
                        throw new RuntimeException(e);
                    }
                });
    }

    public void shutdown() {
        if (shutdown) {
            return;
        }
        shutdown = true;

        streams.close();
        try {
            naisHttpServer.stop();
        } catch (Exception e) {
            LOG.error("Error while shutting down nais http server", e);
        }
    }

    public void startHttpServer() throws Exception {
        naisHttpServer.start();
    }

    public void run() {
        streams.setStateListener((newState, oldState) -> {
            LOG.debug("State change from {} to {}", oldState, newState);
            if ((oldState.equals(KafkaStreams.State.PENDING_SHUTDOWN) && newState.equals(KafkaStreams.State.NOT_RUNNING)) ||
                    (oldState.isRunning() && newState.equals(KafkaStreams.State.ERROR))) {
                LOG.warn("Stream shutdown, stopping nais http server");
                shutdown();
            }
        });
        streams.start();
    }
}
