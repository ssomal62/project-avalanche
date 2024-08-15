package site.leesoyeon.probabilityrewardsystem.order.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import site.leesoyeon.probabilityrewardsystem.order.dto.OrderDetailDto;
import site.leesoyeon.probabilityrewardsystem.order.entity.Order;
import site.leesoyeon.probabilityrewardsystem.order.enums.OrderStatus;
import site.leesoyeon.probabilityrewardsystem.saga.dto.OrderContext;

@Mapper(componentModel = "spring", imports = {OrderStatus.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "shippingId", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(source = "orderItem.productId", target = "productId")
    @Mapping(source = "orderItem.quantity", target = "quantity")
    Order toEntity(OrderContext context);

    OrderDetailDto toOrderDetailDto(Order order);
}
