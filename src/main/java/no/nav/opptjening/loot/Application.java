package no.nav.opptjening.loot;

import no.nav.opptjening.loot.client.InntektSkattClient;
import no.nav.opptjening.loot.client.InntektSkattClientConfiguration;
import no.nav.opptjening.loot.sts.STSClientConfig;
import no.nav.opptjening.loot.sts.SrvLootStsProperties;
import no.nav.opptjening.nais.NaisHttpServer;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import no.nav.popp.tjenester.inntektskatt.v1.LagreBeregnetSkattSikkerhetsbegrensning;
import no.nav.popp.tjenester.inntektskatt.v1.LagreBeregnetSkattUgyldigInput;
import no.nav.popp.tjenester.inntektskatt.v1.meldinger.LagreBeregnetSkattRequest;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static no.nav.opptjening.loot.LagreBeregnetSkattRequestMapper.recordsToRequestList;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private final PensjonsgivendeInntektConsumer pensjonsgivendeInntektConsumer;
    private final InntektSkattClient inntektSkattClient;

    public Application(PensjonsgivendeInntektConsumer pensjonsgivendeInntektConsumer,
                       InntektSkattClient inntektSkattClient) {
        this.pensjonsgivendeInntektConsumer = pensjonsgivendeInntektConsumer;
        this.inntektSkattClient = inntektSkattClient;
    }

    public static void main(String[] args) {

        final Application app;

        try {

            Map<String, String> env = System.getenv();

            NaisHttpServer naisHttpServer = new NaisHttpServer();
            naisHttpServer.start();

            KafkaConfiguration kafkaConfiguration = new KafkaConfiguration(env);
            PensjonsgivendeInntektConsumer pensjonsgivendeInntektConsumer =
                    new PensjonsgivendeInntektConsumer(kafkaConfiguration.getPensjonsgivendeInntektConsumer());

            SrvLootStsProperties srvLootStsProperties = new SrvLootStsProperties();
            STSClientConfig stsClientConfig = new STSClientConfig();
            InntektSkattClientConfiguration inntektSkattClientConfiguration =
                    new InntektSkattClientConfiguration(JaxWsProxyFactoryBean::new, env, srvLootStsProperties, stsClientConfig);
            InntektSkattClient inntektSkattClient = new InntektSkattClient(inntektSkattClientConfiguration.configureAndGetClient());


            app = new Application(pensjonsgivendeInntektConsumer, inntektSkattClient);
            Runtime.getRuntime().addShutdownHook(new Thread(pensjonsgivendeInntektConsumer::close));

            app.run();
        } catch (Exception e) {
            LOG.error("Application failed to start", e);
        }
    }

    public void run() {
        try {
            while (true) {
                ConsumerRecords<String, PensjonsgivendeInntekt> pensjonsgivendeInntektRecords = pensjonsgivendeInntektConsumer.poll(500);

                for (LagreBeregnetSkattRequest lagreBeregnetSkattRequest : recordsToRequestList(pensjonsgivendeInntektRecords)) {
                    try {
                        inntektSkattClient.lagreBeregnetSkatt(lagreBeregnetSkattRequest);
                    } catch (LagreBeregnetSkattUgyldigInput e) {
                        LOG.warn("Ugyldig input til InntektSkatt.lagreBeregnetSkatt", e);
                    }
                }
                pensjonsgivendeInntektConsumer.commit();
            }
        } catch (LagreBeregnetSkattSikkerhetsbegrensning e) {
            LOG.error("Sikkerhetsbegrensning", e);
        } catch (RuntimeException e) {
            LOG.error("Error during processing of PensjonsgivendeInntekt", e);
        }
    }
}
