package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
class DBConeectionUtilTest {

    @Test
    void connection() throws SQLException {
        Connection connection = DBConeectionUtil.getConnection();
        Assertions.assertThat(connection).isNotNull();
    }
}