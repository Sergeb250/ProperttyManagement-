package com.btech.propertymgt.demo.controllers;

import com.btech.propertymgt.demo.models.Receipt;
import com.btech.propertymgt.demo.services.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping
    public ResponseEntity<Receipt> createReceipt(@RequestBody Receipt receipt) {
        return ResponseEntity.ok(receiptService.createReceipt(receipt));
    }

    @GetMapping
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        return ResponseEntity.ok(receiptService.getAllReceipts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Receipt> getReceiptById(@PathVariable Long id) {
        return receiptService.getReceiptById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/number/{receiptNumber}")
    public ResponseEntity<Receipt> getReceiptByNumber(@PathVariable String receiptNumber) {
        return receiptService.getReceiptByReceiptNumber(receiptNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return ResponseEntity.ok().build();
    }
}
