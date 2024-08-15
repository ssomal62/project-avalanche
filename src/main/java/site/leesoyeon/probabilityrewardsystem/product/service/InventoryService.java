package site.leesoyeon.probabilityrewardsystem.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.probabilityrewardsystem.order.dto.OrderItem;
import site.leesoyeon.probabilityrewardsystem.product.entity.Product;
import site.leesoyeon.probabilityrewardsystem.product.exception.ProductException;
import site.leesoyeon.probabilityrewardsystem.product.repository.ProductRepository;
import site.leesoyeon.probabilityrewardsystem.product.util.ProductMapper;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;

import java.util.UUID;

import static site.leesoyeon.probabilityrewardsystem.common.enums.ApiStatus.NOT_FOUND_PRODUCT;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional
    public OrderContext deductInventory(OrderContext context) {
        try {
            OrderItem orderItem = context.orderItem();
            Product product = findById(orderItem.productId());

            if (product.getStock() < orderItem.quantity()) {
                log.error("재고 부족: 제품 ID: {}, 요청 수량: {}, 현재 재고: {}",
                        product.getProductId(), orderItem.quantity(), product.getStock());
                return context.inventoryDepleted("재고 부족: 요청된 수량을 처리할 수 없습니다.");
            }

            int originalStock = product.getStock();
            product.reduceStock(orderItem.quantity());
            productRepository.save(product);

            log.info("재고 감소 완료. 제품 ID: {}, 감소량: {}, 이전 재고: {}, 현재 재고: {}",
                    product.getProductId(), orderItem.quantity(), originalStock, product.getStock());

            return context.inventoryDeducted();

        } catch (Exception e) {
            return context.fail("재고 감소 중 오류 발생: " + e.getMessage());
        }
    }

    @Transactional
    public OrderContext refundInventory(OrderContext context) {
        try {
            OrderItem orderItem = context.orderItem();
            Product product = findById(orderItem.productId());

            int originalStock = product.getStock();
            product.increaseStock(orderItem.quantity());
            productRepository.save(product);

            log.info("보상 트랜잭션: 재고 증가 완료. 제품 ID: {}, 증가량: {}, 이전 재고: {}, 현재 재고: {}",
                    product.getProductId(), orderItem.quantity(), originalStock, product.getStock());

            return context.inventoryRefunded();
        } catch (Exception e) {
            return context.fail("재고 증가 중 오류 발생: " + e.getMessage());
        }
    }

//     ============================================
//                  Private Methods
//     ============================================

    private Product findById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductException(NOT_FOUND_PRODUCT));
    }
}