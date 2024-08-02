package com.project.springboot.e_bank.repository;

import com.project.springboot.e_bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {


}
