package site.leesoyeon.avalanche.shipping.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.avalanche.shipping.infrastructure.external.dto.OrderContext;
import site.leesoyeon.avalanche.shipping.presentation.dto.ShippingStatusDto;
import site.leesoyeon.avalanche.shipping.application.service.ShippingCreationService;
import site.leesoyeon.avalanche.shipping.application.service.ShippingService;


@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;
    private final ShippingCreationService shippingCreationService;

    @PostMapping("/create")
    public ResponseEntity<OrderContext> createShipping(@RequestBody OrderContext context) {
        return ResponseEntity.status(HttpStatus.OK).body(shippingCreationService.createShipping(context));
    }

    @PostMapping("/cancel")
    public ResponseEntity<OrderContext> cancelShipping(@RequestBody OrderContext context) {
        return ResponseEntity.status(HttpStatus.OK).body(shippingCreationService.cancelShipping(context));
    }

    @PutMapping
    public ResponseEntity<Void> updateShipping(@RequestBody ShippingStatusDto request) {
        shippingService.updateShippingStatus(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
