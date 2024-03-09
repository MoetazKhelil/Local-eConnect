package com.localeconnect.app.user.service;

import com.localeconnect.app.user.dto.UserDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserConfirmationEmail {

    private final JavaMailSender mailSender;

    @Autowired
    public UserConfirmationEmail(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    public void sendConfirmationEmail(UserDTO userDTO) {
        sendEmail(userDTO.getEmail(),"Welcome to LocaleConnect!", getConfirmationMailBody(userDTO), true);
    }

    public void sendEmail(String to, String subject, String body, boolean isHtml) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        try{
            if (isHtml) {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
                helper.setText(body, true); // true indicates HTML content
                helper.setTo(to);
                helper.setSubject(subject);

                mailSender.send(mimeMessage);
            } else {
                message.setText(body);
                mailSender.send(message);
            }
        } catch (MessagingException e) {
            log.error("Error sending email", e);
            throw new RuntimeException("Error sending email", e);
        }
    }

    private String getConfirmationMailBody(UserDTO userDTO) {
        return   "<html><body>"
                + "<h1>Welcome to LocaleConnect, " + userDTO.getFirstName() + "!</h1>"
                + "<p>We're excited to have you on board. Your account has been successfully created.</p>"
                + "<p>Sincerely,<br>Your LocaleConnect Team</p>"
                + "<hr>"
                + "<footer>"
                + "<p><a href='URL_TO_PRIVACY_POLICY'>Privacy Policy</a> | <a href='URL_TO_TERMS'>Terms of Service</a></p>"
                + "</footer>"
                + "</body></html>";
    }
}

