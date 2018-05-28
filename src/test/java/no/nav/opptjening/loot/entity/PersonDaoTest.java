package no.nav.opptjening.loot.entity;

import org.junit.*;
import org.testcontainers.containers.OracleContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class PersonDaoTest {
    @Rule
    public OracleContainer oracle = new OracleContainer();

    private Connection connection;

    private boolean initialized;

    @Before
    public void setUp() throws SQLException {
        String url = oracle.getJdbcUrl();

        connection = DriverManager.getConnection(url);

        if (!initialized) {
            opprettTestpersoner();
            initialized = true;
        }
    }

    @Test
    public void that_Person_Is_Returned_When_Fnr_Exists() throws Exception {
        PersonDao personDao = new PersonDao.DefaultPersonDao(connection);

        Person person = personDao.finnPerson("12345678911");
        Assert.assertNotNull(person);

        Assert.assertEquals(1, person.getId());
        Assert.assertEquals("12345678911", person.getFnr());
        Assert.assertEquals("TORTUGA", person.getOpprettetAv());
        Assert.assertEquals("TORTUGA", person.getEndretAv());
        Assert.assertEquals(1, person.getVersjon());
    }

    @Test
    public void when_Person_Does_Not_Exist_Then_Return_Null() throws Exception {
        PersonDao personDao = new PersonDao.DefaultPersonDao(connection);

        Person person = personDao.finnPerson("98765432100");
        Assert.assertNull(person);
    }

    private void opprettTestpersoner() throws SQLException {
        Person[] personer = new Person[]{
                Person.newBuilder()
                        .withFnr("12345678911")
                        .withDatoOpprettet(new Date(System.currentTimeMillis()))
                        .withOpprettetAv("TORTUGA")
                        .withEndretAv("TORTUGA")
                        .withDatoEndret(new Date(System.currentTimeMillis()))
                        .withVersjon(1)
                        .build()
        };

        createTable(connection);
        createPersoner(connection, personer);
    }

    private void createPersoner(Connection connection, Person[] personer) throws SQLException {
        for (Person p : personer) {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO T_PERSON(" +
                    "PERSON_ID, FNR_FK, DATO_OPPRETTET, OPPRETTET_AV, DATO_ENDRET, ENDRET_AV, VERSJON) VALUES (" +
                    "S_PERSON.NEXTVAL, ?, ?, ?, ?, ?, ?)");

            stmt.setString(1, p.getFnr());
            stmt.setDate(2, new java.sql.Date(p.getDatoOpprettet().getTime()));
            stmt.setString(3, p.getOpprettetAv());
            stmt.setDate(4, new java.sql.Date(p.getDatoEndret().getTime()));
            stmt.setString(5, p.getEndretAv());
            stmt.setLong(6, p.getVersjon());

            stmt.executeUpdate();
        }
    }

    private void createTable(Connection connection) throws SQLException {
        connection.createStatement().execute("CREATE SEQUENCE S_PERSON\n" +
                "  START WITH 1\n" +
                "  INCREMENT BY 1");

        connection.createStatement().execute("CREATE TABLE T_PERSON\n" +
                "(\n" +
                "  PERSON_ID      NUMBER NOT NULL CONSTRAINT T_PERSON_PK PRIMARY KEY,\n" +
                "  FNR_FK         VARCHAR2(11 CHAR)                          NOT NULL,\n" +
                "  DATO_OPPRETTET TIMESTAMP(6)                               NOT NULL,\n" +
                "  OPPRETTET_AV   VARCHAR2(20 CHAR)                          NOT NULL,\n" +
                "  DATO_ENDRET    TIMESTAMP(6)                               NOT NULL,\n" +
                "  ENDRET_AV      VARCHAR2(20 CHAR)                          NOT NULL,\n" +
                "  VERSJON        NUMBER                                     NOT NULL\n" +
                ")");

    }
}
