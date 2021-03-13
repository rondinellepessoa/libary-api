package com.cursotddrsilva.libraryapi.service;

import com.cursotddrsilva.libraryapi.model.entity.Loan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class ScheduleService {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * ?";

    @Value("${application.mail.lateLoan.message}")
    private String message;

    private final LoanService loanService;
    private final EmailService emailService;

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendMailToLateLoans(){
        List<Loan> allLateLoan = this.loanService.getAllLateLoan();
        List<String> mailList = allLateLoan.stream()
                .map(loan -> loan.getCustomerEmail())
                .collect(Collectors.toList());

        this.emailService.sendMails(message, mailList);
    }
}
