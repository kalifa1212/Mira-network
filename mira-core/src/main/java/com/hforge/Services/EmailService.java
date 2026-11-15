package com.hforge.Services;

import jakarta.mail.MessagingException;

import java.io.IOException;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendHtmlEmail() throws MessagingException;
    void sendEmailWithAttachment(String to, String subject, String body) throws MessagingException, IOException;
}
