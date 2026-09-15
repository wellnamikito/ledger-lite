package com.ledgerlite.ledgerliteapp.transfer;

import com.ledgerlite.ledgerliteapp.transfer.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferDto> createTransfer(
            @RequestHeader("Idempotency-Key") UUID idempotencyKey,
            @Valid @RequestBody CreateTransferRequest request
    ){
        TransferDto result = transferService.transfer(
                request,
                idempotencyKey
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferDto> getById(
            @PathVariable Long id
    ){
        return ResponseEntity.ok(transferService.getById(id));
    }
}
