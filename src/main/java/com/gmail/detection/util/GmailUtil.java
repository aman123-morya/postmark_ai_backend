package com.gmail.detection.util;

import com.google.api.services.gmail.model.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Properties;

public class GmailUtil {

    public static Message createMessage(
            String from,
            String to,
            String subject,
            String body
    ) throws Exception {

        Properties props = new Properties();

        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));

        email.addRecipient(
                jakarta.mail.Message.RecipientType.TO,
                new InternetAddress(to)
        );

        email.setSubject(subject);

        email.setText(body);

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        email.writeTo(buffer);

        byte[] bytes = buffer.toByteArray();

        String encoded =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(bytes);

        Message message = new Message();

        message.setRaw(encoded);

        return message;
    }

}