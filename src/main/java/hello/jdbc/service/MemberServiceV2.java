package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */

@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    /**
     * 계좌이체 로직
     */
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false);   // 트랜잭션 시작
            bizLogic(con, fromId, toId, money);
            con.commit();   // 커밋 명령어가 커넥션을 통해서 DB의 세션에 전달, 세션이 커밋 실행
        } catch (Exception e) {
            con.rollback(); // 실패 시 롤백
            throw new IllegalStateException(e);
        } finally {
            release(con);
        }
    }

    /**
     * 비즈니스 로직
     */
    private void bizLogic(Connection con, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(con, fromId);
        Member toMember = memberRepository.findById(con, toId);

        memberRepository.update(con, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(con, toId, toMember.getMoney() + money);
    }

    /**
     * 여기서 커넥션을 close 하면 오토커밋이 false 인 상태로 풀에 반납되므로 true 로 설정
     */
    private static void release(Connection con) {
        if (con != null) {
            try {
                con.setAutoCommit(true);    // 커넥션 풀 고려
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    /**
     * 테스트용 계좌이체 예외 구현
     */
    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
