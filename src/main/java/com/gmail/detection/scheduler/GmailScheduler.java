package com.gmail.detection.scheduler;

import com.gmail.detection.service.GmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GmailScheduler {

    private final GmailService gmailService;

    public GmailScheduler(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    // Runs every 1 minute
    @Scheduled(fixedRate = 60000)
    public void syncEmails() {

        System.out.println("===============");
        System.out.println("Sync Started...");
        System.out.println("===============");

        gmailService.syncInbox();

        System.out.println("===============");
        System.out.println("Sync Completed");
        System.out.println("===============");
    }
}