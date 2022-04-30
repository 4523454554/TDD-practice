package com.jay.base.membership.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Membership findByUserIdAndMembershipName(final String userId, final MembershipName membershipName);

    List<Membership> findAllByUserId(String userId);
}
