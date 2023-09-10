package hello.jdbc.repository;

import hello.jdbc.connection.DBConeectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null; // 파라미터 바인딩

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            // SQL 파라미터 바인딩
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();  // DB에 쿼리 실행
            return member;
        } catch (SQLException e) {
            log.error("db error", e);
            e.printStackTrace();
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();  // select 결과 담음

            /*
              next()로 rs에 select 결과가 있는지 확인 후 있으면 멤버 반환, 없으면 NoSuchElementExxception 예외 터트림
              next() -> true, false 반환
             */
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
                throw e;
            } finally {
            close(con, pstmt, rs);
        }
    }

    /**
     * 사용한 자원들 모두 닫기 (con, stmt, rs)
     * stmt에 SQLException이 터져도 catch로 잡았기 때문에 con에 영향을 주지 않음
     */
    private void close(Connection con, Statement stmt, ResultSet rs) {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

    }

    private static Connection getConnection() throws SQLException {
        return DBConeectionUtil.getConnection();
    }
}
