package com.jay.base.membership.dto;

import com.jay.base.membership.domain.MembershipName;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@RequiredArgsConstructor
public class MembershipDetailResponse {

    private final Long id;
    private final MembershipName membershipName;
    private final Integer point;
    private final LocalDateTime createdAt;

}
