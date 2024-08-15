package site.leesoyeon.probabilityrewardsystem.shipping.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import site.leesoyeon.probabilityrewardsystem.shipping.dto.ShippingInfo;
import site.leesoyeon.probabilityrewardsystem.shipping.entity.Shipping;
import site.leesoyeon.probabilityrewardsystem.shipping.enums.ShippingStatus;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = {ShippingStatus.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShippingMapper {

    ShippingMapper INSTANCE = Mappers.getMapper(ShippingMapper.class);

    @Mapping(target = "shippingId", ignore = true)
    @Mapping(target = "status", constant = "PREPARING")
    Shipping toEntity(UUID orderId, ShippingInfo shippingInfo);
}
