package com.jay.base.membership;

import com.jay.base.membership.dto.MembershipDetailResponse;
import com.jay.base.membership.dto.MembershipPointRequest;
import com.jay.base.membership.dto.MembershipRequest;
import com.jay.base.membership.dto.MembershipResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

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

    @GetMapping("/api/v1/memberships")
    public ResponseEntity<List<MembershipDetailResponse>> getMemberList(
            @RequestHeader(USER_ID_HEADER) @Valid final String userId) {
        return ResponseEntity.ok(membershipService.getMembershipList(userId));
    }

    @GetMapping("/api/v1/memberships/{id}")
    public ResponseEntity<MembershipDetailResponse> getMembership(
            @PathVariable Long id,
            @RequestHeader(USER_ID_HEADER) @Valid final String userId) {

        return ResponseEntity.ok(membershipService.getMembership(id, userId));
    }

    @DeleteMapping("/api/v1/memberships/{id}")
    public ResponseEntity<Void> removerMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable final Long id) {

        membershipService.removeMembership(id, userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/memberships/{id}/accumulate")
    public ResponseEntity<Void> accumulateMembershipPoint(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable final Long id,
            @RequestBody @Valid final MembershipPointRequest membershipPointRequest) {

        membershipService.accumulateMembershipPoint(id, userId, membershipPointRequest.getPoint());

        return ResponseEntity.noContent().build();
    }



}
