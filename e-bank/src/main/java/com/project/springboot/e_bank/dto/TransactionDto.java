package com.project.springboot.e_bank.dto;


import java.time.LocalDateTime;

public record TransactionDto(Long id,
                             Long amountId,
                             String accountHolderName,
                             double amount,
                             String transactionType,
                             LocalDateTime timestamp){

}