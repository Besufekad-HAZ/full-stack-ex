package com.mpesa.acquisition.config;

import com.mpesa.acquisition.entity.Bank;
import com.mpesa.acquisition.entity.Branch;
import com.mpesa.acquisition.repository.BankRepository;
import com.mpesa.acquisition.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Check if data already exists
        if (bankRepository.count() > 0) {
            return; // Data already initialized
        }

        // Initialize banks
        List<String> bankNames = Arrays.asList(
            "Kenya Commercial Bank (KCB)",
            "Co-operative Bank",
            "Equity Bank",
            "Standard Chartered",
            "Barclays Bank",
            "NCBA Bank",
            "Absa Bank Kenya",
            "I&M Bank",
            "Diamond Trust Bank (DTB)",
            "Family Bank"
        );

        for (String bankName : bankNames) {
            Bank bank = new Bank(bankName);
            bank = bankRepository.save(bank);

            // Add sample branches for each bank
            List<String> branchNames = Arrays.asList(
                "Main Branch",
                "Westlands Branch",
                "Mombasa Branch",
                "Kisumu Branch",
                "Nakuru Branch",
                "Eldoret Branch"
            );

            for (String branchName : branchNames) {
                Branch branch = new Branch(branchName, bank.getId());
                branchRepository.save(branch);
            }
        }

        System.out.println("Database initialized with sample banks and branches");
    }
}
