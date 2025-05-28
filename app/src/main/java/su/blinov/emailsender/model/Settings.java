package su.blinov.emailsender.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings")
public class Settings {
    @PrimaryKey
    @NonNull
    private final String emailFrom;
    private final String nameFrom;
    private final String password;
    private final String subject;
    private final String serverHost;
    private final Integer serverPort;
    private final Boolean smtpAuth;
    private final Boolean smtpTLS;

    public Settings(@NonNull String emailFrom, String nameFrom, String password, String subject,
                    String serverHost, int serverPort, Boolean smtpAuth, Boolean smtpTLS) {
        this.emailFrom = emailFrom;
        this.nameFrom = nameFrom;
        this.password = password;
        this.subject = subject;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.smtpAuth = smtpAuth;
        this.smtpTLS = smtpTLS;
    }

    @NonNull
    public String getEmailFrom() {
        return emailFrom;
    }

    public String getNameFrom() {
        return nameFrom;
    }

    public String getPassword() {
        return password;
    }

    public String getSubject() {
        return subject;
    }

    public String getServerHost() {
        return serverHost;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public Boolean getSmtpAuth() {
        return smtpAuth;
    }

    public Boolean getSmtpTLS() {
        return smtpTLS;
    }
}
