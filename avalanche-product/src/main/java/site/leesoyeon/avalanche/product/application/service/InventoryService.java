package site.leesoyeon.avalanche.product.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.leesoyeon.avalanche.product.infrastructure.external.dto.OrderContext;
import site.leesoyeon.avalanche.product.domain.model.ProductInfo;
import site.leesoyeon.avalanche.product.domain.model.Product;


@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ProductService productService;

    @Transactional
    public OrderContext deductInventory(OrderContext context) {
        try {
            ProductInfo orderItem = context.productInfo();
            Product product = productService.findById(orderItem.productId());

            if (product.getStock() < context.quantity()) {
                log.error("재고 부족: 제품 ID: {}, 요청 수량: {}, 현재 재고: {}",
                        product.getProductId(), context.quantity(), product.getStock());
                return context.inventoryDeductionFailed("재고 부족: 요청된 수량을 처리할 수 없습니다.");
            }

            int originalStock = product.getStock();
            product.reduceStock(context.quantity());
            productService.saveProduct(product);

            log.info("재고 감소 완료. 제품 ID: {}, 감소량: {}, 이전 재고: {}, 현재 재고: {}",
                    product.getProductId(), context.quantity(), originalStock, product.getStock());

            return context.inventoryDeducted();

        } catch (Exception e) {
            return context.inventoryDeductionFailed("재고 감소 중 오류 발생: " + e.getMessage());
        }
    }

    @Transactional
    public OrderContext refundInventory(OrderContext context) {
        try {
            ProductInfo orderItem = context.productInfo();
            Product product = productService.findById(orderItem.productId());

            int originalStock = product.getStock();
            product.increaseStock(context.quantity());
            productService.saveProduct(product);

            log.info("보상 트랜잭션: 재고 복구 완료. 제품 ID: {}, 증가량: {}, 이전 재고: {}, 현재 재고: {}",
                    product.getProductId(), context.quantity(), originalStock, product.getStock());

            return context.inventoryRefunded();
        } catch (Exception e) {
            return context.inventoryDeductionFailed("재고 복구 중 오류 발생: " + e.getMessage());
        }
    }
}