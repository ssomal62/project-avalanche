package site.leesoyeon.avalanche.product.application.util;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import site.leesoyeon.avalanche.product.presentation.dto.ProductCreateRequest;
import site.leesoyeon.avalanche.product.presentation.dto.ProductDetailResponse;
import site.leesoyeon.avalanche.product.presentation.dto.ProductUpdateRequest;
import site.leesoyeon.avalanche.product.domain.model.Product;
import site.leesoyeon.avalanche.product.shared.enums.ProductStatus;
import site.leesoyeon.avalanche.product.shared.enums.Rarity;


@Mapper(componentModel = "spring", imports = {ProductStatus.class, Rarity.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "version", ignore = true)
    Product toEntity(ProductCreateRequest request);

    @Mapping(target = "productId", ignore = true)
    void updateEntityFromRequest(ProductUpdateRequest request, @MappingTarget Product product);

    ProductDetailResponse toDetailResponse(Product product);

}
