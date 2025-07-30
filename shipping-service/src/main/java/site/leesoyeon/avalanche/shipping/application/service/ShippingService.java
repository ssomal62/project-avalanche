package site.leesoyeon.avalanche.shipping.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.avro.command.ShippingData;
import site.leesoyeon.avalanche.shipping.application.util.ShippingMapper;
import site.leesoyeon.avalanche.shipping.domain.model.Shipping;
import site.leesoyeon.avalanche.shipping.infrastructure.exception.ShippingException;
import site.leesoyeon.avalanche.shipping.infrastructure.external.client.OrderServiceClient;
import site.leesoyeon.avalanche.shipping.infrastructure.external.dto.OrderDto;
import site.leesoyeon.avalanche.shipping.infrastructure.persistence.repository.ShippingRepository;
import site.leesoyeon.avalanche.shipping.presentation.dto.ShippingStatusDto;
import site.leesoyeon.avalanche.shipping.shared.api.ApiStatus;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ShippingService {

    private final ShippingRepository shippingRepository;
    private final OrderServiceClient orderServiceClient;
    private final ShippingMapper shippingMapper;

    @Transactional
    public void updateShippingStatus(ShippingStatusDto request) {
        Shipping shipping = findById(request.shippingId());
        shipping.updateStatus(request.status());

        OrderDto order = orderServiceClient.findByShippingId(request.shippingId())
                .orElseThrow(() -> new ShippingException(ApiStatus.NOT_FOUND_ORDER));

        order = order.toBuilder()
                .status(String.valueOf(request.status()))
                .build();

        orderServiceClient.updateOrder(order);
    }

    @Transactional
    public Shipping saveShipping(UUID orderId, ShippingData shippingData) {
        Shipping shipping = shippingMapper.toEntity(orderId, shippingData);
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
        return shippingRepository.findById(shippingId).orElseThrow(() -> new ShippingException(ApiStatus.NOT_FOUND_SHIPPING_ADDRESS));
    }

    public Shipping findByOrderId(UUID orderId) {
        return shippingRepository.findByOrderId(orderId).orElseThrow(() -> new ShippingException(ApiStatus.NOT_FOUND_SHIPPING_ADDRESS));
    }
}
