package no.nav.opptjening.loot.entity;

import io.prometheus.client.Counter;
import no.nav.opptjening.schema.PensjonsgivendeInntekt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class InnlastingPgiOpptjeningService {

    private static final Logger LOG = LoggerFactory.getLogger(InnlastingPgiOpptjeningService.class);
    private static final Counter producedCount = Counter.build()
            .name("pensjonsgivende_inntekter_persisted")
            .help("Antall inntekter bekreftet sendt til Kafka.").register();

    private final InnlastingPgiOpptjeningDao dao;
    private final PersonService personService;

    private InnlastingPgiOpptjeningMapper innlastingPgiOpptjeningMapper;

    public InnlastingPgiOpptjeningService(InnlastingPgiOpptjeningDao dao, PersonService personService) {
        this.dao = dao;
        this.personService = personService;
        this.innlastingPgiOpptjeningMapper = new InnlastingPgiOpptjeningMapper(personService);
    }

    public boolean save(List<PensjonsgivendeInntekt> pensjonsgivendeInntektListe) throws SQLException {
        List<InnlastingPgiOpptjening> innlastingPgiOpptjeningListe = new LinkedList<>();
        for (PensjonsgivendeInntekt pensjonsgivendeInntekt : pensjonsgivendeInntektListe) {
            InnlastingPgiOpptjening innlastingPgiOpptjening = innlastingPgiOpptjeningMapper.toInnlastningPGI(pensjonsgivendeInntekt);
            LOG.info("Mapper pensjonsgivende inntekt='{}' til innlastning pgi={}", pensjonsgivendeInntekt, innlastingPgiOpptjening);
            innlastingPgiOpptjeningListe.add(innlastingPgiOpptjening);
        }

        if (!dao.save(innlastingPgiOpptjeningListe)) {
            return false;
        }
        producedCount.inc(pensjonsgivendeInntektListe.size());
        return true;
    }
}
