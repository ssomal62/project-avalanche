package site.leesoyeon.avalanche.order.application.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import site.leesoyeon.avalanche.order.presentation.dto.OrderDetailDto;
import site.leesoyeon.avalanche.order.domain.model.Order;
import site.leesoyeon.avalanche.order.shared.enums.OrderStatus;
import site.leesoyeon.avalanche.order.saga.dto.OrderContext;


@Mapper(componentModel = "spring", imports = {OrderStatus.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "shippingId", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(source = "productInfo.productId", target = "productId")
    Order toEntity(OrderContext context);

    OrderDetailDto toOrderDetailDto(Order order);
}
