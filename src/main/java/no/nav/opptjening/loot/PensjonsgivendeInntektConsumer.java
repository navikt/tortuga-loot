package no.nav.opptjening.loot;

import io.prometheus.client.Counter;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PensjonsgivendeInntektConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(PensjonsgivendeInntektConsumer.class);

    private static final Counter pensjonsgivendeInntekterReceivedCounter = Counter.build()
            .name("pensjonsgivende_inntekter_received")
            .help("Antall pensjonsgivende inntekter hentet.").register();

    private static final Counter pensjonsgivendeInntekterProcessedCounter = Counter.build()
            .name("pensjonsgivende_inntekter_processed")
            .help("Antall pensjonsgivende inntekter prosessert").register();

    private final Consumer<String, PensjonsgivendeInntekt> consumer;

    public PensjonsgivendeInntektConsumer(Consumer<String, PensjonsgivendeInntekt> consumer) {
        this.consumer = consumer;
    }

    public void shutdown() {
        LOG.info("Shutting down PensjonsgivendeInntektConsumer");
        consumer.close();
    }

    public List<PensjonsgivendeInntekt> poll() {
        ConsumerRecords<String, PensjonsgivendeInntekt> hendelser = consumer.poll(500);

        List<PensjonsgivendeInntekt> pensjonsgivendeInntektListe = new ArrayList<>();
        for (ConsumerRecord<String, PensjonsgivendeInntekt> record : hendelser) {
            pensjonsgivendeInntekterReceivedCounter.inc();
            pensjonsgivendeInntektListe.add(record.value());
            pensjonsgivendeInntekterProcessedCounter.inc();
        }

        return pensjonsgivendeInntektListe;
    }

    public void commit() {
        consumer.commitAsync();
    }
}
