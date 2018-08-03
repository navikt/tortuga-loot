package no.nav.opptjening.loot;

import org.jetbrains.annotations.NotNull;
import io.prometheus.client.Counter;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

public class PensjonsgivendeInntektConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(PensjonsgivendeInntektConsumer.class);

    private static final Counter pensjonsgivendeInntekterReceivedCounter = Counter.build()
            .name("pensjonsgivende_inntekter_received")
            .help("Antall pensjonsgivende inntekter hentet.").register();

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

    public ConsumerRecords<String, PensjonsgivendeInntekt> poll(long timeout) {
        ConsumerRecords<String, PensjonsgivendeInntekt> pensjonsgivendeInntektRecords = consumer.poll(timeout);
        pensjonsgivendeInntekterReceivedCounter.inc(pensjonsgivendeInntektRecords.count()); //TODO: Correct count?
        return pensjonsgivendeInntektRecords;
    }

    public void commit() {
        consumer.commitAsync();
    }
}
