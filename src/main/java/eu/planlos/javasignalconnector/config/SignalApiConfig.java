package eu.planlos.javasignalconnector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.ConstructorBinding;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

//@ConfigurationProperties(prefix = "signal.api")
//public record SignalApiConfig(boolean active, String address, String user, String password, String phoneSender,
//                              String phoneRecipients, String adminPhone, String messagePrefix, int retryCount, int retryInterval) {
//    public boolean inactive() {
//        return !active;
//    }
//}

@ConfigurationProperties(prefix = "signal.api")
public record SignalApiConfig(Boolean active, String address, String user, String password, String phoneSender,
                              @Value("#{'${signal.api.phoneRecipients}'.split(',\\s*')}") List<String> phoneRecipients, String phoneAdmin, String messagePrefix, Integer retryCount,
                              Integer retryInterval) {

    @ConstructorBinding
    public SignalApiConfig(Boolean active, String address, String user, String password, String phoneSender,
                           List<String> phoneRecipients, String phoneAdmin, String messagePrefix, Integer retryCount,
                           Integer retryInterval) {
        this.active = Optional.ofNullable(active).orElse(false);
        this.address = Optional.ofNullable(address).orElse("not configured");
        this.user = Optional.ofNullable(user).orElse("not configured");
        this.password = Optional.ofNullable(password).orElse("not configured");
        this.phoneSender = Optional.ofNullable(phoneSender).orElse("not configured");
        this.phoneRecipients = Optional.ofNullable(phoneRecipients).orElse(Collections.emptyList());
        this.phoneAdmin = Optional.ofNullable(phoneAdmin).orElse("not configured");
        this.messagePrefix = Optional.ofNullable(messagePrefix).orElse("not configured");
        this.retryCount = Optional.ofNullable(retryCount).orElse(1);
        this.retryInterval = Optional.ofNullable(retryInterval).orElse(1);
    }

    public boolean inactive() {
        return !active;
    }
}