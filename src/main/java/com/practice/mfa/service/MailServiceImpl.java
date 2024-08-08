package com.practice.mfa.service;

import com.practice.mfa.dto.SendMessageDto;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendMessage(SendMessageDto sendMessageDto){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(sendMessageDto.email());
        message.setSubject(sendMessageDto.subject());
        message.setText(sendMessageDto.text());
        mailSender.send(message);
    }
}
