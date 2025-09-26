package com.devsuperior.demo.config;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    // Ainda precisamos desses valores para construir UserCredentials
    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.refresh.token}")
    private String refreshToken;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth.mechanisms", "XOAUTH2");

        Authenticator authenticator = createOAuth2Authenticator();
        Session session = Session.getInstance(props, authenticator);
        mailSender.setSession(session);

        return mailSender;
    }
    private Authenticator createOAuth2Authenticator() {
        try {
            // Cria as credenciais do usuário com clientId, clientSecret e refreshToken
            UserCredentials credentials = UserCredentials.newBuilder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRefreshToken(refreshToken)
                    .build();

            return new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    try {
                        // Renova o Access Token se necessário
                        AccessToken accessToken = credentials.refreshAccessToken();
                        return new PasswordAuthentication(username, accessToken.getTokenValue());
                    } catch (Exception e) {
                        throw new RuntimeException("Falha ao obter o Access Token OAuth2 para SMTP.", e);
                    }
                }
            };
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar o Authenticator OAuth2.", e);
        }
    }
}