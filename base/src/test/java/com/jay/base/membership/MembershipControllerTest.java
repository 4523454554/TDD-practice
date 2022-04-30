package com.jay.base.membership;

import com.google.gson.Gson;
import com.jay.base.membership.domain.MembershipName;
import com.jay.base.membership.dto.MembershipDetailResponse;
import com.jay.base.membership.dto.MembershipPointRequest;
import com.jay.base.membership.dto.MembershipRequest;
import com.jay.base.membership.dto.MembershipResponse;
import com.jay.base.membership.exception.GlobalExceptionHandler;
import com.jay.base.membership.exception.MembershipErrorResult;
import com.jay.base.membership.exception.MembershipException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static com.jay.base.membership.MembershipConstants.USER_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MembershipControllerTest {

    @InjectMocks
    private MembershipController target;

    @Mock
    private MembershipService membershipService;

    private MockMvc mockMvc;
    private Gson gson;

    private MembershipRequest membershipRequest(final Integer point, final MembershipName membershipName) {
        return MembershipRequest.builder()
                .point(point)
                .membershipName(membershipName)
                .build();
    }

    private MembershipPointRequest membershipPointRequest(final Integer point) {
        return MembershipPointRequest.builder()
                .point(point)
                .build();
    }

    @BeforeEach
    public void init() {
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void 멤버십등록실패_사용자식별값이헤더에없음() throws Exception{
        //given
        final String url = "/api/v1/memberships";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(membershipRequest(10000, MembershipName.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("invalidMembershipAddParameter")
    public void 멤버십등록실패_잘못된파라미터(final Integer point, final MembershipName membershipName) throws Exception {
        //given
        final String url = "/api/v1/memberships";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "header")
                        .content(gson.toJson(membershipRequest(point, membershipName)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> invalidMembershipAddParameter() {
        return Stream.of(
                Arguments.of(null, MembershipName.NAVER),
                Arguments.of(-1, MembershipName.NAVER),
                Arguments.of(10000, null)
        );
    }

    @Test
    public void 멤버십등록실패_MemberService에서에러Throw() throws Exception{
        //given
        final String url = "/api/v1/memberships";
        doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
                .when(membershipService)
                .addMembership("12345", MembershipName.NAVER, 10000);

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(10000, MembershipName.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록성공() throws Exception {
        //given
        final String url = "/api/v1/memberships";
        final MembershipResponse membershipResponse = MembershipResponse.builder()
                                                                        .id(-1L)
                                                                        .membershipName(MembershipName.NAVER)
                                                                        .build();
        doReturn(membershipResponse).when(membershipService).addMembership("12345",MembershipName.NAVER,10000);

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(10000,MembershipName.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isCreated());

        final MembershipResponse response = gson.fromJson(resultActions.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), MembershipResponse.class);

        assertThat(response.getMembershipName()).isEqualTo(MembershipName.NAVER);
        assertThat(response.getId()).isNotNull();
    }

    @Test
    public void 멤버십목록조회실패_사용자식별값이헤더에없음() throws Exception {
        //given
        final String url = "/api/v1/memberships";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test public void 멤버십상세조회실패_멤버십이존재하지않음() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";
        doThrow(new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND))
                .when(membershipService)
                .getMembership(-1L, "12345");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER, "12345")
        );
        // then

        resultActions.andExpect(status().isNotFound());
    }

    @Test public void 멤버십상세조회성공() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1";

        doReturn(MembershipDetailResponse.builder().build())
                .when(membershipService)
                .getMembership(-1L, "12345");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER, "12345")
                        .param("membershipName", MembershipName.NAVER.name())
        );
        // then

        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 멤버십삭제실패_사용자식별값이헤더에없음() throws Exception{
        //given
        final String url = "/api/v1/memberships/-1";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십삭제성공() throws Exception{
        //given
        final String url = "/api/v1/memberships/-1";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
                        .header(USER_ID_HEADER, "12345")
        );

        //then
        resultActions.andExpect(status().isNoContent());
    }

    @Test
    public void 멤버십적립실패_사용자식별값이헤더에없음() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1/accumulate";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(membershipPointRequest(10000)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십적립실패_포인트가음수() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1/accumulate";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(membershipPointRequest(-1)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십적립성공() throws Exception {
        // given
        final String url = "/api/v1/memberships/-1/accumulate";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(membershipPointRequest(10000)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isNoContent());
    }
}
