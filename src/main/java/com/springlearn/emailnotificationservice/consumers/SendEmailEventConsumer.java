package com.springlearn.emailnotificationservice.consumers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springlearn.emailnotificationservice.dtos.EmailEventDto;
import com.springlearn.emailnotificationservice.utils.EmailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class SendEmailEventConsumer {
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${email-app-username}")
    private String appUserName;
    @Value("${email-app-password}")
    private String appUserPassword;

    @KafkaListener(topics = "sendEmailEvent",  groupId = "emailServiceGroup")
    public void handleSendEmailEvent(String message) throws JsonProcessingException {
        EmailEventDto eventDto = objectMapper.readValue(
                message,
                EmailEventDto.class
        );

        String subject = eventDto.getSubject();
        String body = eventDto.getBody();
        String toEmail = eventDto.getToEmail();

        //Send an Email.
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        appUserName,
                        appUserPassword);
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, toEmail, subject, body);
    }
}