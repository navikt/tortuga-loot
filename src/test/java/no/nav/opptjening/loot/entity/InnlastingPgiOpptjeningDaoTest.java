package no.nav.opptjening.loot.entity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.OracleContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InnlastingPgiOpptjeningDaoTest {
    @Rule
    public OracleContainer oracle = new OracleContainer();

    private Connection connection;

    private boolean initialized;

    @Before
    public void setUp() throws SQLException {
        String url = oracle.getJdbcUrl();

        connection = DriverManager.getConnection(url);

        if (!initialized) {
            createTable(connection);
            initialized = true;
        }
    }

    @Test
    public void save() throws Exception {
        List<InnlastingPgiOpptjening> innlastingPgiOpptjeningList = new ArrayList<>();

        innlastingPgiOpptjeningList.add(InnlastingPgiOpptjening.newBuilder()
            .withId(1)
            .withFilId(null)
                .withRecart(null)
                .withRappart(null)
                .withInntaar("2017")
                .withKommnr(null)
                .withRappdato(null)
                .withSkjonnsligning(null)
                .withPgiLonn("0")
                .withPgiNaring("0")
                .withAvvistKode(null)
                .withPgiJsf("0")
                .withStatus("0")
                .withFilInfoId(null)
                .withRapportertFnr("12345678911")
                .withKontekst(null)
                .withAvvistInntektId(null)
                .withPersonId(null)
                .withEndretAv(null)
                .withOpprettetAv(null)
                .withFailedInStep(null)
                .withDatoEndret(new Date(System.currentTimeMillis()))
                .withFeilmelding(null)
                .withDatoOpprettet(new Date(System.currentTimeMillis()))
            .build());

        /*InnlastingPgiOpptjeningDao innlastingPgiOpptjeningDao = new InnlastingPgiOpptjeningDao.DefaultInnlastingPgiOpptjeningDao(connection);
        innlastingPgiOpptjeningDao.save(innlastingPgiOpptjeningList);*/
    }

    private void createTable(Connection connection) throws SQLException {
        connection.createStatement().execute("CREATE SEQUENCE S_INNLASTING_PGI\n" +
                "  START WITH 1\n" +
                "  INCREMENT BY 1");

        connection.createStatement().execute("CREATE TABLE T_INNLASTING_PGI\n" +
                "(\n" +
                "    INNLASTING_PGI_ID NUMBER(*) PRIMARY KEY,\n" +
                "    FIL_ID_FK VARCHAR2(200 char),\n" +
                "    RECART VARCHAR2(1 char),\n" +
                "    RAPPART VARCHAR2(1 char),\n" +
                "    INNTAAR VARCHAR2(2 char),\n" +
                "    KOMMNR VARCHAR2(4 char),\n" +
                "    RAPPDATO VARCHAR2(6 char),\n" +
                "    SKJONNSLIGN VARCHAR2(1 char),\n" +
                "    PGI_LONN VARCHAR2(11 char),\n" +
                "    PGI_NARING VARCHAR2(11 char),\n" +
                "    AVVIST_KODE VARCHAR2(20 char),\n" +
                "    PGI_JSF VARCHAR2(11 char),\n" +
                "    STATUS VARCHAR2(20 char),\n" +
                "    FIL_INFO_ID NUMBER(*) NOT NULL,\n" +
                "    RAPPORTERT_FNR VARCHAR2(11 char),\n" +
                "    KONTEKST VARCHAR2(300 char),\n" +
                "    AVVIST_INNTEKT_ID NUMBER(*),\n" +
                "    PERSON_ID NUMBER(*),\n" +
                "    ENDRET_AV VARCHAR2(20 char),\n" +
                "    OPPRETTET_AV VARCHAR2(20 char),\n" +
                "    FAILED_IN_STEP VARCHAR2(200 char),\n" +
                "    DATO_ENDRET TIMESTAMP(6),\n" +
                "    FEILMELDING VARCHAR2(500 char),\n" +
                "    DATO_OPPRETTET TIMESTAMP(6)\n" +
                ")");

        connection.createStatement().execute("CREATE INDEX XIE3INNLASTING_PGI ON T_INNLASTING_PGI (STATUS, INNTAAR)");
        connection.createStatement().execute("CREATE INDEX XIE2INNLASTING_PGI ON T_INNLASTING_PGI (STATUS)");
        connection.createStatement().execute("CREATE INDEX XIF1INNLASTING_PGI ON T_INNLASTING_PGI (FIL_INFO_ID)");
        connection.createStatement().execute("CREATE INDEX XIF2INNLASTING_PGI ON T_INNLASTING_PGI (AVVIST_INNTEKT_ID)");
        connection.createStatement().execute("CREATE INDEX XIF3INNLASTING_PGI ON T_INNLASTING_PGI (PERSON_ID)");

        connection.createStatement().execute("CREATE TRIGGER T_INNLASTING_PGI_TRIGGER\n" +
                "    BEFORE INSERT ON T_INNLASTING_PGI\n" +
                "    FOR EACH ROW\n" +
                "BEGIN\n" +
                "    SELECT S_INNLASTING_PGI.nextval\n" +
                "    INTO :new.INNLASTING_PGI_ID\n" +
                "    FROM dual;\n" +
                "END");
    }

}
