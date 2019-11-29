package no.nav.opptjening.loot;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.Counter;

import no.nav.opptjening.loot.client.inntektskatt.InntektSkattClient;
import no.nav.opptjening.nais.NaisHttpServer;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.opptjening.schema.skatt.hendelsesliste.HendelseKey;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private final InntektSkattClient inntektSkattClient;

    private final KafkaStreams streams;

    private final NaisHttpServer naisHttpServer;

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
            InntektSkattClient inntektSkattClient = new InntektSkattClient(env);

            app = new Application(kafkaConfiguration.streamConfiguration(), inntektSkattClient);
            app.startHttpServer();
            app.run();
        } catch (Exception e) {
            LOG.error("Application failed to start", e);
            System.exit(1);
        }
    }

    public Application(Properties streamsProperties, InntektSkattClient inntektSkattClient) {
        this.inntektSkattClient = inntektSkattClient;

        StreamsBuilder builder = new StreamsBuilder();

        createStream(builder);

        streams = new KafkaStreams(builder.build(), streamsProperties);
        streams.setUncaughtExceptionHandler((t, e) -> {
            LOG.error("Uncaught exception in thread {}, closing streams", t, e);
            shutdown();
        });

        naisHttpServer = new NaisHttpServer(() -> streams.state().isRunning(), () -> true);

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

                    pensjonsgivendeInntekterProcessed.labels(value.getInntektsaar()).inc();
                    pensjonsgivendeInntekterProcessedTotal.inc();

                    if (dryRun) {
                        LOG.info("Skipping because dryRun");
                    } else {

                        inntektSkattClient.lagreInntektPopp(value);
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
