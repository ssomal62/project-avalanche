package site.leesoyeon.avalanche.auth.shared.api;

import lombok.Getter;

/**
 * API 응답을 표현하는 제네릭 클래스.
 *
 * @param <T> 응답 데이터의 타입
 *
 * <p>이 클래스는 API 요청에 대한 표준 응답 구조를 제공합니다.
 * 성공 여부, HTTP 상태 코드, 응답 코드, 메시지, 데이터 등을 포함합니다.
 * </p>
 *
 * <p>주요 기능:
 * <ul>
 *   <li>성공 여부에 따라 응답을 생성합니다.</li>
 *   <li>응답 데이터가 존재하는 경우, 데이터와 함께 성공적인 응답을 생성합니다.</li>
 *   <li>응답 데이터가 없는 경우, 에러 응답을 생성합니다.</li>
 * </ul>
 * </p>
 */
@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final int statusCode;
    private final String code;
    private final String message;
    private final T data;

    private ApiResponse(ApiStatus apiStatus, T data) {
        this.success = apiStatus.getStatusCode() < 400; // 400 미만은 성공
        this.statusCode = apiStatus.getStatusCode();
        this.code = apiStatus.getCode();
        this.message = apiStatus.getMessage();
        this.data = data;
    }

    public static <T> ApiResponse<T> success(ApiStatus status, T data) {
        return new ApiResponse<>(status, data);
    }

    public static <T> ApiResponse<T> error(ApiStatus status) {
        return new ApiResponse<>(status, null);
    }
}