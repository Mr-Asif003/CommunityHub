package com.communityhub.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailService {
    @Autowired
    private JavaMailSender sender;
    public void sendVerificationEmail(String email, String token) {
        String link="http://localhost:8080/api/auth/verify?token=" + token;
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Community Hub: Verify your account");
        msg.setText("click here: " +link);
        sender.send(msg);

    }
}
