package com.jay.base.membership;

import com.jay.base.membership.domain.Membership;
import com.jay.base.membership.domain.MembershipName;
import com.jay.base.membership.domain.MembershipRepository;
import com.jay.base.membership.dto.MembershipDetailResponse;
import com.jay.base.membership.dto.MembershipResponse;
import com.jay.base.membership.exception.MembershipErrorResult;
import com.jay.base.membership.exception.MembershipException;
import com.jay.base.point.PointCalculateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    @Mock
    private PointCalculateService pointCalculateService;

    private final Long membershipId = 1L;
    private final String userId = "userId";
    private final MembershipName membershipName = MembershipName.NAVER;
    private final Integer point = 10000;

    private Membership membership() {
        return Membership.builder()
                .id(1L)
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

    @Test
    public void 멤버십목록조회() {
        //given
        doReturn(Arrays.asList(
                Membership.builder().build(),
                Membership.builder().build(),
                Membership.builder().build()
        )).when(membershipRepository).findAllByUserId(userId);

        //when
        final List<MembershipDetailResponse> result = target.getMembershipList(userId);

        //then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 멤버십상세조회실패_존재하지않음() {
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.getMembership(membershipId, userId));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십상세조회실패_본인이아님() {
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.getMembership(membershipId, "notowner"));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십상세조회성공() {
        //given
        doReturn(Optional.of(membership())).when(membershipRepository).findById(membershipId);

        //when
        final MembershipDetailResponse result = target.getMembership(membershipId, userId);

        //then
        assertThat(result.getMembershipName()).isEqualTo(membershipName);
        assertThat(result.getPoint()).isEqualTo(point);
    }

    @Test
    public void 멤버십삭제실패_존재하지않음() {
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, ()-> target.removeMembership(membershipId, userId));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십삭제실패_본인이아님() {
        //given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, ()-> target.removeMembership(membershipId, "notOwner"));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십삭제성공() {
        //given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        target.removeMembership(membershipId, userId);

        //then
    }

    @Test
    public void 멤버십적립실패_존재하지않음() {
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.accumulateMembershipPoint(membershipId, userId, 10000));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십적립실패_본인이아님() {
        //given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.accumulateMembershipPoint(membershipId, "notOwner", 10000));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);

    }

    @Test
    public void 멤버십적립성공() {
        //given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        target.accumulateMembershipPoint(membershipId, userId, 10000);

        //then

    }


}
