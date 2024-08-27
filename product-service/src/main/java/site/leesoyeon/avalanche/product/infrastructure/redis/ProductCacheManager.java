package site.leesoyeon.avalanche.product.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductCacheManager {

    private final RedisTemplate<String, Object> redisTemplate;

    public List<Map<String, Object>> getRecentReservedOrders(int limit) {
        try {
            Set<String> keys = redisTemplate.keys("order:*");
            return keys.stream()
                    .map(key -> {
                        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
                        if ("RESERVED".equals(entries.get("status"))) {
                            Map<String, Object> orderDetails = new HashMap<>(entries.size() + 1);
                            entries.forEach((k, v) -> orderDetails.put(k.toString(), v));
                            orderDetails.put("orderId", key.replace("order:", ""));
                            return orderDetails;
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .sorted((o1, o2) -> {
                        LocalDateTime date1 = LocalDateTime.parse((String) o1.get("createAt"), DateTimeFormatter.ISO_DATE_TIME);
                        LocalDateTime date2 = LocalDateTime.parse((String) o2.get("createAt"), DateTimeFormatter.ISO_DATE_TIME);
                        return date2.compareTo(date1);
                    })
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching recent reserved orders", e);
            return Collections.emptyList();
        }
    }

    public void deleteOrders(List<String> orderIds) {
        for (String orderId : orderIds) {
            redisTemplate.delete("order:" + orderId);
        }
    }

    public boolean reserveStock(UUID productId, int quantity) {
        String key = "product:stock:" + productId;
        Integer currentStock = (Integer) redisTemplate.opsForValue().get(key);

        if (currentStock != null && currentStock >= quantity) {
            redisTemplate.opsForValue().decrement(key, quantity);
            return true;
        }
        return false;
    }

    public Long releaseStock(UUID productId, int quantity) {
        String key = "product:stock:" + productId;
        return redisTemplate.opsForValue().increment(key, quantity);
    }

    public void setOrderStatusAndQuantity(String productId, String orderId, String status, int quantity) {
        String orderKey = "order:" + orderId;
        String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        redisTemplate.opsForHash().put(orderKey, "productId", productId);
        redisTemplate.opsForHash().put(orderKey, "status", status);
        redisTemplate.opsForHash().put(orderKey, "quantity", quantity);
        redisTemplate.opsForHash().put(orderKey, "createAt", formattedDate);
    }

    public String getOrderStatus(String orderId) {
        String orderKey = "order:" + orderId;
        String status = (String) redisTemplate.opsForHash().get(orderKey, "status");

        if (status == null) {
            log.warn("Order {}의 상태가 Redis에서 존재하지 않습니다.", orderId);
            return null;
        }
        return status;
    }
}