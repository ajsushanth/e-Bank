package com.project.springboot.e_bank.service.impl;

import com.project.springboot.e_bank.dto.TransactionDto;
import com.project.springboot.e_bank.entity.Account;
import com.project.springboot.e_bank.dto.AccountDto;
import com.project.springboot.e_bank.dto.TransferFundDto;
import com.project.springboot.e_bank.entity.Transactions;
import com.project.springboot.e_bank.exception.AccountException;
import com.project.springboot.e_bank.mapper.AccountMapper;
import com.project.springboot.e_bank.repository.AccountRepository;
import com.project.springboot.e_bank.repository.TransactionRepository;
import com.project.springboot.e_bank.service.AccountService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    private TransactionRepository transactionRepository;

    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";
    private static final String TRANSACTION_TYPE_DEBITED = "DEBITED";
    private static final String TRANSACTION_TYPE_CREDITED = "CREDITED";


    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto) {
        Account account = AccountMapper.mapToAccount(accountDto);
        Account savedAccount = accountRepository.save(account);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public AccountDto getAccountById(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));

        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public AccountDto deposit(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));

        double total = account.getBalance() + amount;
        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        Transactions transaction = new Transactions();
        transaction.setAccountId(id);
        transaction.setAccountHolderName(account.getAccountHolderName());
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);

    }

    @Override
    public AccountDto withdraw(Long id, double amount) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));

        double total = account.getBalance() - amount;
        if(total < 0) throw new AccountException("Insufficient balance");

        account.setBalance(total);
        Account savedAccount = accountRepository.save(account);

        Transactions transaction = new Transactions();
        transaction.setAccountId(id);
        transaction.setAccountHolderName(account.getAccountHolderName());
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    public List<AccountDto> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map((account) -> AccountMapper.mapToAccountDto(account))
                    .collect(Collectors.toList());
    }

    @Override
    public void deleteAccount(Long id) {
        Account account = accountRepository
                .findById(id)
                .orElseThrow(() -> new AccountException("Account does not exists"));

        accountRepository.deleteById(id);
    }

    @Override
    public void transferFunds(TransferFundDto transferFundDto) {

        //Retrieve the fromAccount
        Account fromAccount = accountRepository
                .findById(transferFundDto.fromAccountId())
                .orElseThrow(() -> new AccountException("Account does not exists"));

        //Retrieve the toAccount
        Account toAccount = accountRepository
                .findById(transferFundDto.toAccountId())
                .orElseThrow(() -> new AccountException("Account does not exists"));

        double total = fromAccount.getBalance() - transferFundDto.amount();
        if(total < 0) throw new AccountException("Payment cancelled! Due to insufficient balance");

        //Debiting the amount from fromAccount
        fromAccount.setBalance(total);
        accountRepository.save(fromAccount);

        //Crediting the amount to toAccount
        total = toAccount.getBalance() + transferFundDto.amount();
        toAccount.setBalance(total);
        accountRepository.save(toAccount);

        Transactions transaction = new Transactions();

        transaction.setAccountId(fromAccount.getId());
        transaction.setAccountHolderName(fromAccount.getAccountHolderName());
        transaction.setAmount(transferFundDto.amount());
        transaction.setTransactionType(TRANSACTION_TYPE_DEBITED);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);

        transaction.setAccountId(toAccount.getId());
        transaction.setAccountHolderName(toAccount.getAccountHolderName());
        transaction.setAmount(transferFundDto.amount());
        transaction.setTransactionType(TRANSACTION_TYPE_CREDITED);
        transaction.setTimestamp(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    public List<TransactionDto> getAccountTransactions(Long accountId) {
        List<Transactions> transactions = transactionRepository
                .findByAccountIdOrderByTimestampDesc(accountId);

        return transactions.stream()
                .map((transaction) -> convertEntityToDto(transaction))
                .collect(Collectors.toList());
    }

    private TransactionDto convertEntityToDto(Transactions transaction){
        TransactionDto transactionDto = new TransactionDto(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAccountHolderName(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getTimestamp()
        );

        return transactionDto;
    }

}
