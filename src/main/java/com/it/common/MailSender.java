/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.it.common;

import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.it.domain.History;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.it.config.MailConfig;

public class MailSender {
  private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
  public static MailSender mailSender = new MailSender();
  private Properties props;

  public static MailSender getInstance() {
    return mailSender;
  }

  public void init() {
    props = new Properties();
    props.put("mail.smtp.starttls.enable", MailConfig.getInstance().isStarttlsEnable());
    props.put("mail.smtp.auth", MailConfig.getInstance().isSmtpAuth());
    props.put("mail.smtp.host", MailConfig.getInstance().getSmtpHost());
    props.put("mail.smtp.port", MailConfig.getInstance().getSmtpPort());
  }

  public void send(String to, String subject, String content) {
    History.getInstance().save(subject, content);

    if (true) {
      // for test
      logger.info("Mail will be send!!!");
      logger.info("to: {}", to);
      logger.info("subject: {}", subject);
      logger.info("content: {}", content);
      return;
    }

    EmailAuthenticator authenticator =
        new EmailAuthenticator(MailConfig.getInstance().getId(), MailConfig.getInstance()
            .getPassword());
    Session session = Session.getInstance(props, authenticator);

    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(MailConfig.getInstance().getId()));
      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
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
