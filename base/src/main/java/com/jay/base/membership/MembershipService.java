package com.jay.base.membership;

import com.jay.base.membership.domain.Membership;
import com.jay.base.membership.domain.MembershipName;
import com.jay.base.membership.domain.MembershipRepository;
import com.jay.base.membership.dto.MembershipResponse;
import com.jay.base.membership.exception.MembershipErrorResult;
import com.jay.base.membership.exception.MembershipException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembershipService {

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
}
