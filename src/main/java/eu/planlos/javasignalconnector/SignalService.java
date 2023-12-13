package eu.planlos.javasignalconnector;

import eu.planlos.javasignalconnector.config.SignalApiConfig;
import eu.planlos.javasignalconnector.model.SignalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;

import static eu.planlos.javasignalconnector.model.SignalException.IS_NULL;

@Slf4j
@Service
public class SignalService {

    public static final String SIGNAL_JSON = "{\"message\": \"%s\", \"number\": \"%s\", \"recipients\": [ \"%s\" ]}";

    private final SignalApiConfig config;
    private final WebClient webClient;

    /*
     * Startup
     */
    public SignalService(SignalApiConfig config, @Qualifier("SignalWebClient") WebClient webClient) {
        this.config = config;
        this.webClient = webClient;
    }

    /*
     * Functions
     */

    @Async
    public void sendMessageToAdmin(String message) {
        sendMessage(message, config.phoneAdmin());
    }

    @Async
    public void sendMessageToRecipients(String message) {
        sendMessage(message, config.phoneReceiver());
    }

    private void sendMessage(String message, String receiverCsv) {
        try {
            send(message, receiverCsv);
            logNotificationOK();
        } catch (Exception e) {
            logNotificationError(e);
            throw new SignalException(String.format("Something went wrong: %s", e.getMessage()));
        }
    }

    private void send(String message, String receiverCsv) {

        if (!config.active()) {
            log.info("Signal notifications are disabled. Skip sending");
            return;
        }
        log.info("Signal notifications are enabled. Sending");

        String jsonMessage = String.format(SIGNAL_JSON, prefixMessage(message), config.phoneSender(), receiverCsv);

        String apiResponse = webClient
                .post()
                .uri("/v2/send")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonMessage)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.fixedDelay(config.retryCount(), Duration.ofSeconds(config.retryInterval())))
                .doOnError(error -> log.error("Sending notification has failed."))
                .block();
        if (apiResponse != null) {
            log.info("Response is: {}", apiResponse);
            return;
        }

        throw new SignalException(IS_NULL);
    }

    /*
     * HELPER
     */

    private String prefixMessage(String message) {
        return String.format("%s\\n%s", config.messagePrefix(), message);
    }

    private void logNotificationOK() {
        log.debug("Notification has been sent, if enabled.");
    }

    private void logNotificationError(Exception e) {
        log.error("Notification could not been sent: {}", e.getMessage());
        if(log.isDebugEnabled()) {
            e.printStackTrace();
        }
    }
}

