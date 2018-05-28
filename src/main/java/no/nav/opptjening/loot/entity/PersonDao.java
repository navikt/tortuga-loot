package no.nav.opptjening.loot.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface PersonDao {

    Person finnPerson(String fnr) throws SQLException;

    class DefaultPersonDao implements PersonDao {

        private final Connection connection;

        private final PersonRowMapper personRowMapper;

        public DefaultPersonDao(Connection connection) {
            this(connection, new PersonRowMapper.DefaultPersonRowMapper());
        }

        public DefaultPersonDao(Connection connection, PersonRowMapper personRowMapper) {
            this.connection = connection;
            this.personRowMapper = personRowMapper;
        }

        public Person finnPerson(String fnr) throws SQLException {
            try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM T_PERSON WHERE FNR_FK = ?")) {
                stmt.setString(1, fnr);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    return personRowMapper.toPerson(rs);
                }
            }
        }
    }
}
