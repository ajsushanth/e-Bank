package com.project.springboot.e_bank.service;


import com.project.springboot.e_bank.dto.AccountDto;
import com.project.springboot.e_bank.dto.TransactionDto;
import com.project.springboot.e_bank.dto.TransferFundDto;

import java.util.List;

public interface AccountService {
    AccountDto createAccount(AccountDto accountDto);

    AccountDto getAccountById(Long Id);

    AccountDto deposit(Long id, double amount);

    AccountDto withdraw(Long id, double amount);

    List<AccountDto> getAllAccounts();

    void deleteAccount(Long id);

    void transferFunds(TransferFundDto transferFundDto);

    List<TransactionDto> getAccountTransactions(Long accountId);

}
