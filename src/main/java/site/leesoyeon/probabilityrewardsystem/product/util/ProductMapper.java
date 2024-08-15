package site.leesoyeon.probabilityrewardsystem.product.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import site.leesoyeon.probabilityrewardsystem.product.dto.ProductCreateRequest;
import site.leesoyeon.probabilityrewardsystem.product.dto.ProductDetailResponse;
import site.leesoyeon.probabilityrewardsystem.product.dto.ProductUpdateRequest;
import site.leesoyeon.probabilityrewardsystem.product.entity.Product;
import site.leesoyeon.probabilityrewardsystem.product.enums.ProductStatus;
import site.leesoyeon.probabilityrewardsystem.product.enums.Rarity;

@Mapper(componentModel = "spring", imports = {ProductStatus.class, Rarity.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "version", ignore = true)
    Product toEntity(ProductCreateRequest request);

    @Mapping(target = "productId", ignore = true)
    void updateEntityFromRequest(ProductUpdateRequest request, @MappingTarget Product product);

    ProductDetailResponse toDetailResponse(Product product);

}
