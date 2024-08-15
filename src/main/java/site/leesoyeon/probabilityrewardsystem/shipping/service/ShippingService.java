package site.leesoyeon.probabilityrewardsystem.shipping.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.probabilityrewardsystem.order.entity.Order;
import site.leesoyeon.probabilityrewardsystem.order.exception.OrderException;
import site.leesoyeon.probabilityrewardsystem.order.repository.OrderRepository;
import site.leesoyeon.probabilityrewardsystem.shipping.dto.ShippingInfo;
import site.leesoyeon.probabilityrewardsystem.shipping.dto.ShippingStatusDto;
import site.leesoyeon.probabilityrewardsystem.shipping.entity.Shipping;
import site.leesoyeon.probabilityrewardsystem.shipping.exception.ShippingException;
import site.leesoyeon.probabilityrewardsystem.shipping.repository.ShippingRepository;
import site.leesoyeon.probabilityrewardsystem.shipping.util.ShippingMapper;

import java.util.UUID;

import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.NOT_FOUND_ORDER;
import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.NOT_FOUND_SHIPPING_ADDRESS;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShippingService {

    private final ShippingRepository shippingRepository;
    private final OrderRepository orderRepository;
    private final ShippingMapper shippingMapper;

    @Transactional
    public void updateShippingStatus(ShippingStatusDto request) {
        Shipping shipping = findById(request.shippingId());
        shipping.updateStatus(request.status());

        Order order = orderRepository.findByShippingId(request.shippingId())
                .orElseThrow(() -> new OrderException(NOT_FOUND_ORDER));

        order.updateStatusBasedOnShipping(request.status());
        orderRepository.save(order);
    }

    @Transactional
    public Shipping saveShipping(UUID orderId, ShippingInfo shippingInfo) {
        Shipping shipping = shippingMapper.toEntity(orderId, shippingInfo);
        return shippingRepository.save(shipping);
    }

    @Transactional
    public void deleteById(UUID shippingId) {
        shippingRepository.deleteById(shippingId);
    }

//     ============================================
//                 Protected Methods
//     ============================================

    protected Shipping findById(UUID shippingId) {
        return shippingRepository.findById(shippingId).orElseThrow(() -> new ShippingException(NOT_FOUND_SHIPPING_ADDRESS));
    }
}
