package hello.jdbc.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class DBConeectionUtil {

    /*
    JDBC 표준 인터페이스가 지원하는 Connection
    getConnection() 호출 -> DriverManager를 통해 Connection 가져 옴 -> Connection 인터페이스의 구현체 반환
    현재 H2 데이터베이스이므로 JdbcConnection 반환
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
