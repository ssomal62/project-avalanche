package site.leesoyeon.probabilityrewardsystem.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus;

import static org.junit.jupiter.api.Assertions.*;

public class ApiStatusTest {

    @Test
    @DisplayName("ApiStatus.SUCCESS의 상태 코드, 코드 값, 메시지가 올바르게 설정되었는지 테스트")
    void testSuccessApiStatusValues() {
        assertAll("SUCCESS 상태 검증",
                () -> assertEquals(200, ApiStatus.SUCCESS.getStatusCode(), "상태 코드"),
                () -> assertEquals("SU", ApiStatus.SUCCESS.getCode(), "응답 코드"),
                () -> assertEquals("Success", ApiStatus.SUCCESS.getMessage(), "응답 메시지")
        );
    }

    @Test
    @DisplayName("ApiStatus.INVALID_INPUT_VALUE의 상태 코드, 코드 값, 메시지가 올바르게 설정되었는지 테스트")
    void testInvalidInputApiStatusValues() {
        assertAll("INVALID_INPUT_VALUE 상태 검증",
                () -> assertEquals(400, ApiStatus.INVALID_INPUT_VALUE.getStatusCode(), "상태 코드"),
                () -> assertEquals("IIV", ApiStatus.INVALID_INPUT_VALUE.getCode(), "응답 코드"),
                () -> assertEquals("입력 값이 잘못되었습니다.", ApiStatus.INVALID_INPUT_VALUE.getMessage(), "응답 메시지")
        );
    }
}
