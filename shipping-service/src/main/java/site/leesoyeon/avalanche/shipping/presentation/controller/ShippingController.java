package site.leesoyeon.avalanche.shipping.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.leesoyeon.avalanche.avro.command.CancelShippingCommand;
import site.leesoyeon.avalanche.avro.command.PrepareShippingCommand;
import site.leesoyeon.avalanche.shipping.application.service.ShippingCreationService;
import site.leesoyeon.avalanche.shipping.application.service.ShippingService;
import site.leesoyeon.avalanche.shipping.presentation.dto.ShippingStatusDto;


@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;
    private final ShippingCreationService shippingCreationService;

    @PostMapping("/create")
    public ResponseEntity<Void> createShipping(@RequestBody PrepareShippingCommand command) {
        shippingCreationService.createShipping(command);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelShipping(@RequestBody CancelShippingCommand command) {
        shippingCreationService.cancelShipping(command);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateShipping(@RequestBody ShippingStatusDto request) {
        shippingService.updateShippingStatus(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
