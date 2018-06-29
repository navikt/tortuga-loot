package no.nav.opptjening.loot;

import com.sun.istack.internal.NotNull;
import io.prometheus.client.Counter;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

    public PensjonsgivendeInntektConsumer(@NotNull Consumer<String, PensjonsgivendeInntekt> consumer) {
        this.consumer = consumer;
        subscribeToTopic();
    }

    private void subscribeToTopic() {
        consumer.subscribe(Collections.singletonList(KafkaConfiguration.PENSJONSGIVENDE_INNTEKT_TOPIC), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                LOG.info("Partition revoked: {}", partitions);
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                LOG.info("Partitions assigned: {}", partitions);
            }
        });
    }

    public void close() {
        LOG.info("Shutting down PensjonsgivendeInntektConsumer");
        consumer.close();
    }

    public List<PensjonsgivendeInntekt> poll() {

        ConsumerRecords<String, PensjonsgivendeInntekt> pensjonsgivendeInntekterRecords = consumer.poll(500);
        List<PensjonsgivendeInntekt> pensjonsgivendeInntektListe = new ArrayList<>();
        for (ConsumerRecord<String, PensjonsgivendeInntekt> record : pensjonsgivendeInntekterRecords) {
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
