package com.it.common;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.it.config.MailConfig;

public class MailSender {
    public static MailSender mailSender = new MailSender();
    private Properties props;

    public static MailSender getInstance() {
        return mailSender;
    }

    public void init() {
        props = new Properties();
        props.put("mail.smtp.starttls.enable", MailConfig.getInstance()
                .isStarttlsEnable());
        props.put("mail.smtp.host", MailConfig.getInstance().getSmtpHost());
        props.put("mail.smtp.auth", MailConfig.getInstance().isSmtpAuth());
        props.put("mail.smtp.port", MailConfig.getInstance().getSmtpPort());
    }

    public void send(String to, String subject, String content) {
        EmailAuthenticator authenticator = new EmailAuthenticator(MailConfig
                .getInstance().getId(), MailConfig.getInstance().getPassword());
        Session session = Session.getInstance(props, authenticator);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(MailConfig.getInstance().getId()));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            msg.setSubject(subject);
            msg.setContent(content, "text/html; charset=UTF-8");
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    class EmailAuthenticator extends Authenticator {
        private String id;
        private String pw;

        public EmailAuthenticator(String id, String pw) {
            this.id = id;
            this.pw = pw;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(id, pw);
        }
    }
}