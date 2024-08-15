package site.leesoyeon.probabilityrewardsystem.point.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import site.leesoyeon.probabilityrewardsystem.point.dto.ManualPointAdjustmentRequest;
import site.leesoyeon.probabilityrewardsystem.point.dto.PointTransactionDetailDto;
import site.leesoyeon.probabilityrewardsystem.point.entity.PointTransaction;
import site.leesoyeon.probabilityrewardsystem.point.enums.ActivityType;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;

@Mapper(componentModel = "spring",
        imports = {ActivityType.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PointTransactionMapper {

    @Mapping(target = "balance", expression = "java(0)")
    PointTransaction toEntity(ManualPointAdjustmentRequest request);

    PointTransactionDetailDto toDto(PointTransaction pointTransaction);

    @Mapping(target = "userId", source = "orderContext.userId")
    @Mapping(target = "amount", expression = "java(-orderContext.transactionInfo().amount())")
    @Mapping(target = "balance", expression = "java(balance - orderContext.transactionInfo().amount())")
    @Mapping(target = "activityType", source = "orderContext.transactionInfo.activityType")
    @Mapping(target = "description", expression = "java(orderContext.transactionInfo().activityType().getDescription() + \" : \" + orderContext.productInfo().name())")
    @Mapping(target = "orderId", source = "orderContext.orderId")
    @Mapping(target = "productId", source = "orderContext.productInfo.productId")
    @Mapping(target = "isCancelled", constant = "false")
    PointTransaction toPointTransaction(OrderContext orderContext, Integer balance);
}
