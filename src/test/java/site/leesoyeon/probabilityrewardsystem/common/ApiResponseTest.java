package site.leesoyeon.probabilityrewardsystem.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus;

import static org.junit.jupiter.api.Assertions.*;

public class ApiResponseTest {

    @Test
    @DisplayName("성공 응답이 올바르게 생성되는지 테스트")
    void testSuccessResponse() {
        ApiResponse<String> response = ApiResponse.success(ApiStatus.SUCCESS, "data");

        assertAll("성공 응답 검증",
                () -> assertTrue(response.isSuccess(), "성공 여부"),
                () -> assertEquals(200, response.getStatusCode(), "상태 코드"),
                () -> assertEquals("SU", response.getCode(), "응답 코드"),
                () -> assertEquals("Success", response.getMessage(), "응답 메시지"),
                () -> assertEquals("data", response.getData(), "응답 데이터")
        );
    }

    @Test
    @DisplayName("에러 응답이 올바르게 생성되는지 테스트")
    void testErrorResponse() {
        ApiResponse<String> response = ApiResponse.error(ApiStatus.INVALID_INPUT_VALUE);

        assertAll("에러 응답 검증",
                () -> assertFalse(response.isSuccess(), "실패 여부"),
                () -> assertEquals(400, response.getStatusCode(), "상태 코드"),
                () -> assertEquals("IIV", response.getCode(),  "응답 코드"),
                () -> assertEquals("입력 값이 잘못되었습니다.", response.getMessage(), "응답 메시지"),
                () -> assertNull(response.getData(), "응답 데이터 없음")
        );
    }
}
