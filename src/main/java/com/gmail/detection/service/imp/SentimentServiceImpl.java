package com.gmail.detection.service.imp;

import com.gmail.detection.entity.Email;
import com.gmail.detection.enums.Sentiment;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.EmailRepository;
import com.gmail.detection.service.SentimentService;
import org.springframework.stereotype.Service;

@Service
public class SentimentServiceImpl implements SentimentService {

    private final EmailRepository emailRepository;

    public SentimentServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    // ==========================================
    // Analyze Sentiment using Email ID
    // ==========================================

    @Override
    public String analyzeSentiment(Long emailId) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found with ID : " + emailId));

        String text = (email.getSubject() == null ? "" : email.getSubject())
                + " "
                + (email.getBody() == null ? "" : email.getBody());

        String sentiment = analyzeText(text);

        email.setSentiment(Sentiment.valueOf(sentiment));

        emailRepository.save(email);

        return sentiment;
    }

    // ==========================================
    // Analyze Text
    // ==========================================

    @Override
    public String analyzeText(String text) {

        if (text == null) {
            return Sentiment.NEUTRAL.name();
        }

        text = text.toLowerCase();

        // ==========================
        // Positive
        // ==========================

        if (text.contains("congratulations")
                || text.contains("selected")
                || text.contains("approved")
                || text.contains("success")
                || text.contains("thank you")
                || text.contains("excellent")
                || text.contains("great")
                || text.contains("happy")
                || text.contains("welcome")) {

            return Sentiment.POSITIVE.name();
        }

        // ==========================
        // Negative
        // ==========================

        if (text.contains("failed")
                || text.contains("rejected")
                || text.contains("cancelled")
                || text.contains("blocked")
                || text.contains("error")
                || text.contains("problem")
                || text.contains("issue")
                || text.contains("complaint")
                || text.contains("delay")) {

            return Sentiment.NEGATIVE.name();
        }

        // ==========================
        // Neutral
        // ==========================

        return Sentiment.NEUTRAL.name();
    }
}