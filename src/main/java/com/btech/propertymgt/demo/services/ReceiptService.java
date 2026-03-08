package com.btech.propertymgt.demo.services;

import com.btech.propertymgt.demo.models.Receipt;
import com.btech.propertymgt.demo.repositories.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;

    public Receipt createReceipt(Receipt receipt) {
        return receiptRepository.save(receipt);
    }

    public List<Receipt> getAllReceipts() {
        return receiptRepository.findAll();
    }

    public Optional<Receipt> getReceiptById(Long id) {
        return receiptRepository.findById(id);
    }

    public Optional<Receipt> getReceiptByReceiptNumber(String receiptNumber) {
        return receiptRepository.findByReceiptNumber(receiptNumber);
    }

    public void deleteReceipt(Long id) {
        receiptRepository.deleteById(id);
    }
}
