package com.jay.base.membership;

import com.jay.base.membership.domain.Membership;
import com.jay.base.membership.domain.MembershipName;
import com.jay.base.membership.domain.MembershipRepository;
import com.jay.base.membership.dto.MembershipResponse;
import com.jay.base.membership.exception.MembershipErrorResult;
import com.jay.base.membership.exception.MembershipException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

    @InjectMocks
    private MembershipService target;

    @Mock
    private MembershipRepository membershipRepository;

    private final String userId = "userId";
    private final MembershipName membershipName = MembershipName.NAVER;
    private final Integer point = 10000;

    private Membership membership() {
        return Membership.builder()
                .id(-1L)
                .userId(userId)
                .membershipName(MembershipName.NAVER)
                .point(point)
                .build();
    }

    @Test
    public void 멤버십등록실패_이미존재함() {
        //given
        doReturn(Membership.builder().build()).when(membershipRepository).findByUserIdAndMembershipName(userId, membershipName);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.addMembership(userId, membershipName, point));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
    }

    @Test
    public void 멤버십등록성공() {
        //given
        doReturn(null).when(membershipRepository).findByUserIdAndMembershipName(userId, MembershipName.NAVER);
        doReturn(membership()).when(membershipRepository).save(any(Membership.class));

        //when
        final MembershipResponse result = target.addMembership(userId, MembershipName.NAVER, point);
        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMembershipName()).isEqualTo(MembershipName.NAVER);

        //verify
        verify(membershipRepository, times(1)).findByUserIdAndMembershipName(userId,membershipName);
        verify(membershipRepository, times(1)).save(any(Membership.class));

    }


}
