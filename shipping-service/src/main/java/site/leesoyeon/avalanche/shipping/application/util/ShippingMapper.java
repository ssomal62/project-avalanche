package site.leesoyeon.avalanche.shipping.application.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import site.leesoyeon.avalanche.avro.command.ShippingData;
import site.leesoyeon.avalanche.shipping.domain.model.Shipping;
import site.leesoyeon.avalanche.shipping.shared.enums.ShippingStatus;

import java.util.UUID;

@Mapper(componentModel = "spring", imports = {ShippingStatus.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShippingMapper {

    ShippingMapper INSTANCE = Mappers.getMapper(ShippingMapper.class);

    @Mapping(target = "shippingId", ignore = true)
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "recipientName", source = "shippingData.recipientName")
    @Mapping(target = "recipientPhone", source = "shippingData.recipientPhone")
    @Mapping(target = "address", source = "shippingData.address")
    @Mapping(target = "detailedAddress", source = "shippingData.detailedAddress")
    @Mapping(target = "zipCode", source = "shippingData.zipCode")
    @Mapping(target = "status", constant = "PREPARING")
    Shipping toEntity(UUID orderId, ShippingData shippingData);
}
