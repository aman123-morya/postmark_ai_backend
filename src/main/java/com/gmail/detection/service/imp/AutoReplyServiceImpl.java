package com.gmail.detection.service.imp;

import com.gmail.detection.dto.AutoReplyDTO;
import com.gmail.detection.entity.Email;
import com.gmail.detection.exception.ResourceNotFoundException;
import com.gmail.detection.repository.EmailRepository;
import com.gmail.detection.service.AutoReplyService;
import org.springframework.stereotype.Service;

@Service
public class AutoReplyServiceImpl implements AutoReplyService {

    private final EmailRepository emailRepository;

    public AutoReplyServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public AutoReplyDTO generateReply(Long emailId) {

        Email email = emailRepository.findById(emailId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Email not found with ID : " + emailId));

        AutoReplyDTO dto = new AutoReplyDTO();

        dto.setEmailId(email.getId());

        dto.setReceiver(email.getSender());

        dto.setSubject("Re : " + email.getSubject());

        dto.setReplyMessage(
                generateReplyMessage(
                        email.getSentiment().name()));

        return dto;
    }

    @Override
    public String generateReplyMessage(String sentiment) {

        switch (sentiment.toUpperCase()) {

            case "POSITIVE":

                return """
                        Dear Customer,

                        Thank you for your valuable feedback.

                        We are happy to know that you are satisfied with our service.

                        Have a wonderful day.

                        Regards,
                        Gmail AI Team
                        """;

            case "NEGATIVE":

                return """
                        Dear Customer,

                        We sincerely apologize for the inconvenience.

                        Your issue has been forwarded to our support team.

                        We will contact you shortly.

                        Regards,
                        Gmail AI Team
                        """;

            default:

                return """
                        Dear Customer,

                        Thank you for contacting us.

                        We have received your email and will review it shortly.

                        Regards,
                        Gmail AI Team
                        """;
        }
    }
}