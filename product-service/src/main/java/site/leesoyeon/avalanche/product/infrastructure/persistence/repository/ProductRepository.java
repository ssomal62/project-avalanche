package site.leesoyeon.avalanche.product.infrastructure.persistence.repository;

import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import site.leesoyeon.avalanche.product.domain.model.Product;

import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.productId = :productId AND p.stock >= :quantity")
    int decreaseStock(@Param("productId") UUID productId, @Param("quantity") int quantity);

    @Query("SELECT p.stock FROM Product p WHERE p.productId = :productId")
    int findStockByProductId(@Param("productId") UUID productId);
}