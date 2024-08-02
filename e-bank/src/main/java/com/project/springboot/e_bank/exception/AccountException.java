package com.project.springboot.e_bank.exception;

public class AccountException extends RuntimeException{
    public AccountException(String message){
        super(message);
    }
}
