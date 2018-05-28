package no.nav.opptjening.loot.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

public interface InnlastingPgiOpptjeningDao {

    boolean save(List<InnlastingPgiOpptjening> innlastingPgiOpptjeningListe) throws SQLException;

    class DefaultInnlastingPgiOpptjeningDao implements InnlastingPgiOpptjeningDao {
        private static final Logger LOG = LoggerFactory.getLogger(DefaultInnlastingPgiOpptjeningDao.class);

        private final Connection connection;

        public DefaultInnlastingPgiOpptjeningDao(Connection connection) {
            this.connection = connection;
        }

        public boolean save(List<InnlastingPgiOpptjening> innlastingPgiOpptjeningListe) throws SQLException {
            connection.setAutoCommit(false);
            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO T_INNLASTING_PGI (" +
                    "FIL_ID_FK, RECART, RAPPART, INNTAAR, KOMMNR, RAPPDATO, SKJONNSLIGN, PGI_LONN, PGI_NARING, " +
                    "AVVIST_KODE, PGI_JSF, STATUS, FIL_INFO_ID, RAPPORTERT_FNR, KONTEKST, AVVIST_INNTEKT_ID, PERSON_ID, " +
                    "ENDRET_AV, OPPRETTET_AV, FAILED_IN_STEP, DATO_ENDRET, FEILMELDING, DATO_OPPRETTET) VALUES(" +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                for (InnlastingPgiOpptjening innlastingPgiOpptjening : innlastingPgiOpptjeningListe) {
                    stmt.setString(1, innlastingPgiOpptjening.getFilId());
                    stmt.setString(2, innlastingPgiOpptjening.getRecart());
                    stmt.setString(3, innlastingPgiOpptjening.getRappart());
                    stmt.setString(4, innlastingPgiOpptjening.getInntaar());
                    stmt.setString(5, innlastingPgiOpptjening.getKommnr());
                    stmt.setString(6, innlastingPgiOpptjening.getRappdato());
                    stmt.setString(7, innlastingPgiOpptjening.getSkjonnsligning());
                    stmt.setString(8, innlastingPgiOpptjening.getPgiLonn());
                    stmt.setString(9, innlastingPgiOpptjening.getPgiNaring());
                    stmt.setString(10, innlastingPgiOpptjening.getAvvistKode());
                    stmt.setString(11, innlastingPgiOpptjening.getPgiJsf());
                    stmt.setString(12, innlastingPgiOpptjening.getStatus());
                    stmt.setString(13, innlastingPgiOpptjening.getFilInfoId());
                    stmt.setString(14, innlastingPgiOpptjening.getRapportertFnr());
                    stmt.setString(15, innlastingPgiOpptjening.getKontekst());
                    stmt.setString(16, innlastingPgiOpptjening.getAvvistInntektId());
                    if (innlastingPgiOpptjening.getPersonId() == null) {
                        stmt.setNull(17, Types.NUMERIC);
                    } else {
                        stmt.setLong(17, innlastingPgiOpptjening.getPersonId());
                    }
                    stmt.setString(18, innlastingPgiOpptjening.getEndretAv());
                    stmt.setString(19, innlastingPgiOpptjening.getOpprettetAv());
                    stmt.setString(20, innlastingPgiOpptjening.getFailedInStep());
                    stmt.setDate(21, new Date(innlastingPgiOpptjening.getDatoEndret().getTime()));
                    stmt.setString(22, innlastingPgiOpptjening.getFeilmelding());
                    stmt.setDate(23, new Date(innlastingPgiOpptjening.getDatoOpprettet().getTime()));
                    stmt.addBatch();
                }

                int[] insertCount = stmt.executeBatch();
                for (int i = 0; i < insertCount.length; i++) {
                    if (insertCount[i] == 0) {
                        connection.rollback();

                        return false;
                    }
                }

                connection.commit();

                return true;
            } catch (BatchUpdateException e) {
                LOG.error("BatchUpdateException: SQLState: {}, Message: {}, Vendor: {}",
                        e.getSQLState(), e.getMessage(), e.getErrorCode(), e);
                LOG.error("Update counts: {}", e.getUpdateCounts());

                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
