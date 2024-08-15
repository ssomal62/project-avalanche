package site.leesoyeon.probabilityrewardsystem.shipping.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.shipping.entity.Shipping;
import site.leesoyeon.probabilityrewardsystem.shipping.exception.ShippingInfoInvalidException;

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
            return context.shippingCreated(shipping.getShippingId());
        } catch (Exception e) {
            return context.fail("배송 생성 중 오류 발생: " + e.getMessage());
        }
    }

    @Transactional
    public OrderContext cancelShipping(OrderContext context) {
        try {
            if (context.shippingInfo() != null && context.shippingInfo().shippingId() != null) {
                shippingService.deleteById(context.shippingInfo().shippingId());
            }
            return context.shippingCancelled();
        } catch (Exception e) {
            return context.fail("배송 취소 중 오류 발생: " + e.getMessage());
        }
    }
}
