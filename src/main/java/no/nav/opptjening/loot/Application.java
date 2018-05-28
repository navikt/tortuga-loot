package no.nav.opptjening.loot;

import no.nav.opptjening.loot.entity.InnlastingPgiOpptjeningDao;
import no.nav.opptjening.loot.entity.InnlastingPgiOpptjeningService;
import no.nav.opptjening.loot.entity.PersonDao;
import no.nav.opptjening.loot.entity.PersonService;
import no.nav.opptjening.nais.ApplicationRunner;
import no.nav.opptjening.nais.NaisHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String [] args) {

        Map<String, String> env = System.getenv();

        ApplicationRunner appRunner;

        try {

            NaisHttpServer naisHttpServer = new NaisHttpServer();
            KafkaConfiguration kafkaConfiguration = new KafkaConfiguration(env);

            PensjonsgivendeInntektConsumer pensjonsgivendeInntektConsumer = new PensjonsgivendeInntektConsumer(kafkaConfiguration.pensjonsgivendeInntektConsumer());

            Connection dbConnection = DriverManager.getConnection(env.get("DB_URL"), env.get("DB_USERNAME"), env.get("DB_PASSWORD"));

            PersonDao personDao = new PersonDao.DefaultPersonDao(dbConnection);
            PersonService personService = new PersonService(personDao);

            InnlastingPgiOpptjeningDao innlastingPgiOpptjeningDao = new InnlastingPgiOpptjeningDao.DefaultInnlastingPgiOpptjeningDao(dbConnection);
            InnlastingPgiOpptjeningService innlastingPgiOpptjeningService = new InnlastingPgiOpptjeningService(innlastingPgiOpptjeningDao, personService);

            LagrePensjonsgivendeInntektTask task = new LagrePensjonsgivendeInntektTask(pensjonsgivendeInntektConsumer, innlastingPgiOpptjeningService);

            appRunner = new ApplicationRunner(task, naisHttpServer);

            appRunner.addShutdownListener(pensjonsgivendeInntektConsumer::shutdown);
            appRunner.addShutdownListener(() -> {
                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    LOG.error("Failed to close DB connection", e);
                }
            });
        } catch (Exception e) {
            LOG.error("Application failed to start", e);
            return;
        }
        appRunner.run();
    }
}
