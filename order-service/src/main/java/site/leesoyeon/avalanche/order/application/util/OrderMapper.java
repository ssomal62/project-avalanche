package site.leesoyeon.avalanche.order.application.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import site.leesoyeon.avalanche.order.domain.model.Order;
import site.leesoyeon.avalanche.order.presentation.dto.ShippingInfo;
import site.leesoyeon.avalanche.order.presentation.dto.OrderDetailDto;
import site.leesoyeon.avalanche.order.presentation.dto.OrderRequest;
import site.leesoyeon.avalanche.order.shared.enums.OrderStatus;


@Mapper(componentModel = "spring", imports = {OrderStatus.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "productId", source = "productInfo.productId")
    Order toEntity(OrderRequest dto);

    OrderDetailDto toOrderDetailDto(Order order);

    ShippingInfo toShippingInfoDto(ShippingInfo shippingInfo);
}
