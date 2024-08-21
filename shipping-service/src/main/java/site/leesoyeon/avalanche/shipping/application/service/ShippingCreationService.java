package site.leesoyeon.avalanche.shipping.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.shipping.infrastructure.external.dto.OrderContext;
import site.leesoyeon.avalanche.shipping.domain.model.Shipping;
import site.leesoyeon.avalanche.shipping.infrastructure.exception.ShippingInfoInvalidException;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShippingCreationService {

    private final ShippingService shippingService;

    @Transactional
    public OrderContext createShipping(OrderContext context) {
        try {
            if (context.shippingInfo() == null ||
                context.shippingInfo().recipientName() == null ||
                context.shippingInfo().recipientName().isEmpty()) {
                throw new ShippingInfoInvalidException("배송 정보가 올바르지 않습니다.");
            }

            Shipping shipping = shippingService.saveShipping(context.orderId(), context.shippingInfo());
            log.info("배송지 생성 완료: {}", shipping.getShippingId());
            return context.shippingCreated(shipping.getShippingId());
        } catch (Exception e) {
            return context.shippingCreationFailed("배송 생성 중 오류 발생: " + e.getMessage());
        }
    }

    @Transactional
    public OrderContext cancelShipping(OrderContext context) {
        try {
            if (context.shippingInfo() != null && context.shippingInfo().shippingId() != null) {
                shippingService.deleteById(context.shippingInfo().shippingId());
            }
            log.info("배송지 삭제가 완료되었습니다");
            return context.shippingCancelled();
        } catch (Exception e) {
            return context.shippingCreationFailed("배송 취소 중 오류 발생: " + e.getMessage());
        }
    }
}
