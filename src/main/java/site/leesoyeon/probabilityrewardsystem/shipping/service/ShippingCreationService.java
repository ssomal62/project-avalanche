package site.leesoyeon.probabilityrewardsystem.shipping.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;
import site.leesoyeon.probabilityrewardsystem.shipping.entity.Shipping;
import site.leesoyeon.probabilityrewardsystem.shipping.exception.ShippingInfoInvalidException;
import site.leesoyeon.probabilityrewardsystem.shipping.repository.ShippingRepository;
import site.leesoyeon.probabilityrewardsystem.shipping.util.ShippingMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShippingCreationService {

    private final ShippingRepository shippingRepository;
    private final ShippingMapper shippingMapper;

    @Transactional
    public OrderContext createShipping(OrderContext context) {
        try {
            if (context.shippingInfo() == null ||
                    context.shippingInfo().recipientName() == null ||
                    context.shippingInfo().recipientName().isEmpty()) {
                throw new ShippingInfoInvalidException("배송 정보가 올바르지 않습니다.");
            }

            Shipping shipping = shippingMapper.toEntity(context.orderId(), context.shippingInfo());
            shipping = shippingRepository.save(shipping);

            return context.shippingCreated(shipping.getShippingId());
        } catch (Exception e) {
            return context.fail("배송 생성 중 오류 발생: " + e.getMessage());
        }
    }

    @Transactional
    public OrderContext cancelShipping(OrderContext context) {
        try {
            if (context.shippingInfo() != null && context.shippingInfo().shippingId() != null) {
                shippingRepository.deleteById(context.shippingInfo().shippingId());
                log.info("주문에 대해 배송이 취소되었습니다: {}", context.orderId());
            }
            return context.shippingCancelled();
        } catch (Exception e) {
            return context.fail("배송 취소 중 오류 발생: " + e.getMessage());
        }
    }
}
