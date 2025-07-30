package site.leesoyeon.avalanche.point.application.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import site.leesoyeon.avalanche.avro.command.ApplyPointCommand;
import site.leesoyeon.avalanche.point.domain.model.PointTransaction;
import site.leesoyeon.avalanche.point.presentation.dto.ManualPointAdjustmentRequest;
import site.leesoyeon.avalanche.point.presentation.dto.PointTransactionDetailDto;
import site.leesoyeon.avalanche.point.shared.enums.ActivityType;

@Mapper(componentModel = "spring",
        imports = {ActivityType.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PointTransactionMapper {

    @Mapping(target = "balance", expression = "java(0)")
    PointTransaction toEntity(ManualPointAdjustmentRequest request);

    PointTransactionDetailDto toDto(PointTransaction pointTransaction);

    @Mapping(target = "transactionId", ignore = true)
    @Mapping(target = "userId", expression = "java(java.util.UUID.fromString(command.getUserId()))")
    @Mapping(target = "amount", expression = "java(-command.getAmount())")
    @Mapping(target = "balance", expression = "java(balance - command.getAmount())")
    @Mapping(target = "activityType", expression = "java(ActivityType.valueOf(command.getActivityType()))")
    @Mapping(target = "orderId", expression = "java(java.util.UUID.fromString(command.getOrderId()))")
    @Mapping(target = "description", source = "command.productName")
    @Mapping(target = "isCancelled", constant = "false")
    @Mapping(target = "expiryDate", ignore = true)
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "version", constant = "0L")
    PointTransaction toPointTransaction(ApplyPointCommand command, Integer balance);
}
