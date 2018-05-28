package no.nav.opptjening.loot;

import no.nav.opptjening.loot.entity.InnlastingPgiOpptjeningService;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LagrePensjonsgivendeInntektTask implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(LagrePensjonsgivendeInntektTask.class);

    private final PensjonsgivendeInntektConsumer consumer;
    private final InnlastingPgiOpptjeningService innlastingPgiOpptjeningService;

    public LagrePensjonsgivendeInntektTask(PensjonsgivendeInntektConsumer consumer, InnlastingPgiOpptjeningService innlastingPgiOpptjeningService) {
        this.consumer = consumer;
        this.innlastingPgiOpptjeningService = innlastingPgiOpptjeningService;
    }

    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                List<PensjonsgivendeInntekt> pensjonsgivendeInntektListe = consumer.poll();

                if (!innlastingPgiOpptjeningService.save(pensjonsgivendeInntektListe)) {
                    throw new RuntimeException("Failed to persist PGI");
                }

                consumer.commit();
            }
        } catch (Exception e) {
            LOG.error("Error during processing of PensjonsgivendeInntekt", e);
        }

        LOG.info("LagrePensjonsgivendeInntektTask task stopped");
    }
}
