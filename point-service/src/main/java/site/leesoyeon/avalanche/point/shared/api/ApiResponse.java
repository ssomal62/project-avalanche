package site.leesoyeon.avalanche.point.shared.api;

import lombok.Getter;

@Getter
public class ApiResponse<T> {

    private final boolean success;
    private final int statusCode;
    private final String message;
    private final String debugMessage;
    private final T data;

    private ApiResponse(ApiStatus apiStatus, T data, String debugMessage) {
        this.success = apiStatus.getStatusCode() < 400;
        this.statusCode = apiStatus.getStatusCode();
        this.message = apiStatus.getMessage();
        this.debugMessage = debugMessage;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(ApiStatus status, T data) {
        return new ApiResponse<>(status, data, null);
    }

    public static <T> ApiResponse<T> error(ApiStatus status, String debugMessage) {
        return new ApiResponse<>(status, null, debugMessage);
    }
}