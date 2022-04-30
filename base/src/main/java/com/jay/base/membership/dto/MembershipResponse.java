package com.jay.base.membership.dto;

import com.jay.base.membership.domain.MembershipName;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class MembershipResponse {

    private final Long id;
    private final MembershipName membershipName;
}
