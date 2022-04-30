package com.jay.base.membership;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Membership findByUserIdAndMembershipName(final String userId, final MembershipName membershipName);
}
