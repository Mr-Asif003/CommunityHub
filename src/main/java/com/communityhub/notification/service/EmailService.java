package com.communityhub.notification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender sender;
    public void sendVerificationEmail(String email, String token) {
        String link="http://localhost:8080/api/auth/verify?token=" + token;
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Community Hub: Verify your account");
        msg.setText("Hi User!\n Please Verify your account by clicking on this link: " +link);
        sender.send(msg);
    }
    public void sendMessage(String email,String message){
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Community Hub");
        msg.setText("Hi User"+message);
        sender.send(msg);
    }

}
