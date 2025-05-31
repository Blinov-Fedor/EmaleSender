package su.blinov.emailsender;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class MailSender {
    private final String emailFrom;
    private final String nameFrom;
    private final String password;
    private final String subject;
    private final String serverHost;
    private final Integer serverPort;
    private final Boolean smtpAuth;
    private final Boolean smtpTLS;

    public MailSender(
            String emailFrom, String nameFrom, String password,
            String subject, String serverHost, Integer serverPort,
            Boolean smtpAuth, Boolean smtpTLS) {
        this.emailFrom = emailFrom;
        this.nameFrom = nameFrom;
        this.password = password;
        this.subject = subject;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.smtpAuth = smtpAuth;
        this.smtpTLS = smtpTLS;
    }


    public void sendEmail(String email, String template) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", this.smtpAuth);
        props.put("mail.smtp.starttls.enable", this.smtpTLS);
        props.put("mail.smtp.host", this.serverHost);
        props.put("mail.smtp.port", this.serverPort);
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailFrom, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(this.emailFrom, this.nameFrom));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject(this.subject);
            message.setText(template);

            Transport.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
