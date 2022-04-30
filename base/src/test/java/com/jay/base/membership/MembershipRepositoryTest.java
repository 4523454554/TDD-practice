package com.jay.base.membership;

import com.jay.base.membership.domain.Membership;
import com.jay.base.membership.domain.MembershipName;
import com.jay.base.membership.domain.MembershipRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MembershipRepositoryTest {
    /**
     * 나의 멤버십 등록 API
     * 기능: 나의 멤버십을 등록합니다.
     * 요청: 사용자 식별값, 멤버십 이름, 포인트
     * 응답: 멤버십 ID, 멤버십 이름
     */
    @Autowired
    private MembershipRepository memberShipRepository;

    @Test
    @DisplayName("멤버십이등록된다")
    public void 멤버십이등록된다() {
        //given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipName(MembershipName.NAVER)
                .point(10000)
                .build();

        //when
        final Membership result = memberShipRepository.save(membership);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo("userId");
        assertThat(result.getMembershipName()).isEqualTo(MembershipName.NAVER);
        assertThat(result.getPoint()).isEqualTo(10000);
    }

    @Test
    @DisplayName("멤버십이존재한다")
    public void 멤버십이저장된다() {
        //given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipName(MembershipName.NAVER)
                .point(10000)
                .build();

        //when
        memberShipRepository.save(membership);
        final Membership result = memberShipRepository.findByUserIdAndMembershipName("userId", MembershipName.NAVER);

                //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo("userId");
        assertThat(result.getMembershipName()).isEqualTo(MembershipName.NAVER);
        assertThat(result.getPoint()).isEqualTo(10000);
    }

}
