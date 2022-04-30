package com.jay.base.membership;

import com.jay.base.membership.dto.MembershipRequest;
import com.jay.base.membership.dto.MembershipResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.jay.base.membership.MembershipConstants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipResponse> addMembership(
             @RequestHeader(USER_ID_HEADER) final String userId,
             @RequestBody @Valid final MembershipRequest membershipRequest) {

        final MembershipResponse membershipResponse = membershipService.addMembership(userId, membershipRequest.getMembershipName(), membershipRequest.getPoint());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipResponse);
    }
}
