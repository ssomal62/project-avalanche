package site.leesoyeon.probabilityrewardsystem.saga.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import site.leesoyeon.probabilityrewardsystem.order.service.OrderCreationService;
import site.leesoyeon.probabilityrewardsystem.product.service.InventoryService;
import site.leesoyeon.probabilityrewardsystem.saga.coordinator.OrderSagaCoordinator;
import site.leesoyeon.probabilityrewardsystem.saga.event.EventPublisher;
import site.leesoyeon.probabilityrewardsystem.saga.event.impl.SagaEventPublisher;
import site.leesoyeon.probabilityrewardsystem.saga.step.InventoryDeductionStep;
import site.leesoyeon.probabilityrewardsystem.saga.step.OrderCreationStep;
import site.leesoyeon.probabilityrewardsystem.shipping.service.ShippingCreationService;

import javax.sql.DataSource;

@Configuration
public class SagaConfig {

    @Bean
    public InventoryDeductionStep inventoryDeductionStep(InventoryService inventoryService, EventPublisher eventPublisher, TransactionTemplate transactionTemplate) {
        return new InventoryDeductionStep(inventoryService, eventPublisher, transactionTemplate);
    }

    @Bean
    public OrderCreationStep orderCreationStep(OrderCreationService orderCreationService, ShippingCreationService shippingService, EventPublisher eventPublisher, TransactionTemplate transactionTemplate) {
        return new OrderCreationStep(orderCreationService, shippingService, eventPublisher, transactionTemplate);
    }

    @Bean
    public OrderSagaCoordinator orderSagaCoordinator(
            InventoryDeductionStep inventoryDeductionStep,
            OrderCreationStep orderCreationStep,
            EventPublisher eventPublisher,
            TransactionTemplate transactionTemplate) {
        return new OrderSagaCoordinator(inventoryDeductionStep, orderCreationStep, eventPublisher, transactionTemplate);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public TransactionTemplate transactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    public EventPublisher eventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new SagaEventPublisher(applicationEventPublisher);
    }
}