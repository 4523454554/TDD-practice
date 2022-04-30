package com.jay.base.membership;

import com.jay.base.membership.domain.Membership;
import com.jay.base.membership.domain.MembershipName;
import com.jay.base.membership.domain.MembershipRepository;
import com.jay.base.membership.dto.MembershipDetailResponse;
import com.jay.base.membership.dto.MembershipResponse;
import com.jay.base.membership.exception.MembershipErrorResult;
import com.jay.base.membership.exception.MembershipException;
import com.jay.base.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final PointService pointService;
    private final MembershipRepository membershipRepository;

    public MembershipResponse addMembership(final String userId, final MembershipName membershipName, final Integer point) {
        Membership result = membershipRepository.findByUserIdAndMembershipName(userId, membershipName);
        if (result != null) {
            throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
        }

        Membership membership = Membership.builder()
                .userId(userId)
                .membershipName(membershipName)
                .point(point)
                .build();

        Membership savedMembership = membershipRepository.save(membership);

        return MembershipResponse.builder()
                .id(savedMembership.getId())
                .membershipName(savedMembership.getMembershipName())
                .build();
    }

    public List<MembershipDetailResponse> getMembershipList(String userId) {

        final List<Membership> membershipList = membershipRepository.findAllByUserId(userId);

        return membershipList.stream()
                .map(v -> MembershipDetailResponse.builder()
                        .id(v.getId())
                        .membershipName(v.getMembershipName())
                        .point(v.getPoint())
                        .createdAt(v.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public MembershipDetailResponse getMembership(final Long membershipId, final String userId) {
        final Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);
        final Membership membership = optionalMembership.orElseThrow(()-> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

        if (membershipId < 1) {
            throw new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
        }

        if (!membership.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
        }

        return MembershipDetailResponse.builder()
                .id(membership.getId())
                .membershipName(membership.getMembershipName())
                .point(membership.getPoint())
                .createdAt(membership.getCreatedAt())
                .build();
    }

    public void removeMembership(final Long membershipId, final String userId) {

       final Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);
       final Membership membership = optionalMembership.orElseThrow(()-> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

       if (!membership.getUserId().equals(userId)) {
           throw new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
       }

       membershipRepository.deleteById(membershipId);

    }

    @Transactional
    public void accumulateMembershipPoint(final Long membershipId, final String userId, final int price) {
        final Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);
        final Membership membership = optionalMembership.orElseThrow(
                ()-> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

        if (!membership.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
        }

        final int willBeAddedPoint = pointService.getPoint(price);

        membership.setPoint(membership.getPoint() + willBeAddedPoint);
    }
}
