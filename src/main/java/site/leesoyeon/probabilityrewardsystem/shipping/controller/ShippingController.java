package site.leesoyeon.probabilityrewardsystem.shipping.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.leesoyeon.probabilityrewardsystem.shipping.dto.ShippingStatusDto;
import site.leesoyeon.probabilityrewardsystem.shipping.service.ShippingService;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

    @PutMapping
    public ResponseEntity<Void> updateShipping(@RequestBody ShippingStatusDto request) {
        shippingService.updateShippingStatus(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
