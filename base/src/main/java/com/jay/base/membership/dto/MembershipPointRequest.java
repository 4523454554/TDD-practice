package com.jay.base.membership.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class MembershipPointRequest {

    @NotNull
    @Min(0)
    private final Integer point;
}
