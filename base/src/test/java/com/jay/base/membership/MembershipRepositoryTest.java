package com.jay.base.membership;

import com.jay.base.membership.domain.Membership;
import com.jay.base.membership.domain.MembershipName;
import com.jay.base.membership.domain.MembershipRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    public void 멤버십이등록된다() {
        //given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipName(MembershipName.NAVER)
                .point(10000)
                .build();

        //when
        final Membership result = membershipRepository.save(membership);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo("userId");
        assertThat(result.getMembershipName()).isEqualTo(MembershipName.NAVER);
        assertThat(result.getPoint()).isEqualTo(10000);
    }

    @Test
    public void 멤버십이저장된다() {
        //given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipName(MembershipName.NAVER)
                .point(10000)
                .build();

        //when
        membershipRepository.save(membership);
        final Membership result = membershipRepository.findByUserIdAndMembershipName("userId", MembershipName.NAVER);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo("userId");
        assertThat(result.getMembershipName()).isEqualTo(MembershipName.NAVER);
        assertThat(result.getPoint()).isEqualTo(10000);
    }

    @Test
    public void 멤버십_조회_사이즈0() {
        //given

        //when
        List<Membership> result = membershipRepository.findAllByUserId("userId");

        //then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 멤버십_조회_사이즈2() {
        //given
        membershipRepository.save(Membership.builder()
                .userId("userId")
                .membershipName(MembershipName.NAVER)
                .point(10000)
                .build());

        membershipRepository.save(Membership.builder()
                .userId("userId")
                .membershipName(MembershipName.KAKAO)
                .point(10000)
                .build());

        //when
        List<Membership> result = membershipRepository.findAllByUserId("userId");

        //then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void 멤버십_추가후_삭제() {
        //given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipName(MembershipName.NAVER)
                .point(10000)
                .build();

        final Membership savedMembership = membershipRepository.save(membership);

        //when
        membershipRepository.deleteById(membership.getId());

        //then
    }

}
