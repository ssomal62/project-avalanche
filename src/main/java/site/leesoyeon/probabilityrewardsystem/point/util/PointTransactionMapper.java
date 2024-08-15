package site.leesoyeon.probabilityrewardsystem.point.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import site.leesoyeon.probabilityrewardsystem.point.dto.ManualPointAdjustmentRequest;
import site.leesoyeon.probabilityrewardsystem.point.dto.PointTransactionDetailDto;
import site.leesoyeon.probabilityrewardsystem.point.entity.PointTransaction;
import site.leesoyeon.probabilityrewardsystem.point.enums.ActivityType;

@Mapper(componentModel = "spring",
        imports = {ActivityType.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PointTransactionMapper {

    @Mapping(target = "balance", expression = "java(0)")
    PointTransaction toEntity(ManualPointAdjustmentRequest request);

    PointTransactionDetailDto toDto(PointTransaction pointTransaction);
}
