package hello.jdbc.repository;

import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - ConnectionParam
 */
@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

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
     * Connection 을 파라미터로 전달
     */
    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
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
            // connection은 서비스에서 닫아야 한다.
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
        //  JdbcUtils.closeConnection(con);
        }
    }

    /**
     * 회원 수정
     */
    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            // SQL 파라미터 바인딩
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();// DB에 쿼리 실행
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            e.printStackTrace();
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    /**
     * 회원 수정 -> 커넥션을 서비스에서 넘기도록 함
     */
    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        PreparedStatement pstmt = null;

        try {
            pstmt = con.prepareStatement(sql);

            // SQL 파라미터 바인딩
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();// DB에 쿼리 실행
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            e.printStackTrace();
            throw e;
        } finally {
            // connection 은 여기서 닫지 않는다.
            JdbcUtils.closeStatement(pstmt);
        }
    }

    /**
     * 회원 삭제
     */
    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, memberId);
            pstmt.executeUpdate();// DB에 쿼리 실행
        } catch (SQLException e) {
            log.error("db error", e);
            e.printStackTrace();
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    /**
     * 사용한 자원들 모두 닫기 (con, stmt, rs)
     */
    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }

    /**
     * DataSource 를 통해 얻은 connection 반환
     */
    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
