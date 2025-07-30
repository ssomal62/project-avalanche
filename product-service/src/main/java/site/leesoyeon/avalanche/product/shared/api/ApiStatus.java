package site.leesoyeon.avalanche.product.shared.api;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApiStatus {

    // 200 - Success
    SUCCESS(200, "SU", "Success"),

    // 400 - Bad Request : 잘못된 요청
    INVALID_INPUT_VALUE(400, "IIV", "입력 값이 잘못되었습니다."),
    NOT_ENOUGH_STOCK(409, "NES", "재고가 부족합니다."),
    INVALID_PRODUCT_ID(400, "IPI", "유효하지 않은 상품 ID입니다."),
    INVALID_PRODUCT_STATUS(400, "IPS", "유효하지 않은 상품 상태입니다."),
    DUPLICATE_PRODUCT_NAME(400, "DPN", "중복된 상품 이름입니다."),

    // 404 - Not Found : 잘못된 리소스 접근
    NOT_FOUND_PRODUCT(404, "NFP", "존재하지 않는 상품입니다."),

    // 409 - Conflict : 중복 데이터
    PRODUCT_ALREADY_EXISTS(409, "PAE", "이미 존재하는 상품입니다."),
    CONFLICTING_PRODUCT_UPDATE(409, "CPU", "상품 업데이트 도중 충돌이 발생했습니다."),

    // 500 - Internal Server Error
    INTERNAL_SERVER_ERROR(500,"ISE", "데이터베이스 연결 실패"),
    DATABASE_ERROR(500, "DBE", "데이터베이스 오류"),
    FAILED_TO_SAVE_PRODUCT(500, "FSP", "상품 저장에 실패했습니다."),
    FAILED_TO_UPDATE_PRODUCT(500, "FUP", "상품 업데이트에 실패했습니다."),
    FAILED_TO_DELETE_PRODUCT(500, "FDP", "상품 삭제에 실패했습니다.");

    private final int statusCode;
    private final String code;
    private final String message;
}
