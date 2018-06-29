package no.nav.opptjening.loot;

import no.nav.opptjening.nais.NaisHttpServer;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);
    private final PensjonsgivendeInntektConsumer pensjonsgivendeInntektConsumer;

    public Application(PensjonsgivendeInntektConsumer pensjonsgivendeInntektConsumer) {
        this.pensjonsgivendeInntektConsumer = pensjonsgivendeInntektConsumer;
    }

    public static void main(String[] args) {

        final Application app;

        try {
            NaisHttpServer naisHttpServer = new NaisHttpServer();
            naisHttpServer.run();

            KafkaConfiguration kafkaConfiguration = new KafkaConfiguration(System.getenv());
            PensjonsgivendeInntektConsumer pensjonsgivendeInntektConsumer =
                    new PensjonsgivendeInntektConsumer(kafkaConfiguration.getPensjonsgivendeInntektConsumer());

            app = new Application(pensjonsgivendeInntektConsumer);

            Runtime.getRuntime().addShutdownHook(new Thread(pensjonsgivendeInntektConsumer::close));

            app.run();
        } catch (Exception e) {
            LOG.error("Application failed to start", e);
        }
    }

    public void run() {
        try {
            while(true) {
                List<PensjonsgivendeInntekt> pensjonsgivendeInntektListe = pensjonsgivendeInntektConsumer.poll();
                pensjonsgivendeInntektConsumer.commit();
            }
        } catch (Exception e) {
            LOG.error("Error during processing of PensjonsgivendeInntekt", e);
        }
    }
}
