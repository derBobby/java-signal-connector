package eu.planlos.javasignalconnector;

import eu.planlos.javasignalconnector.config.SignalApiConfig;
import eu.planlos.javasignalconnector.model.SignalErrorRetryFilter;
import eu.planlos.javasignalconnector.model.SignalException;
import eu.planlos.javaspringwebutilities.web.WebClientRetryFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.stream.Collectors;

import static eu.planlos.javasignalconnector.SignalService.Recipient.ADMIN;
import static eu.planlos.javasignalconnector.SignalService.Recipient.RECIPIENTS;
import static eu.planlos.javasignalconnector.model.SignalException.IS_NULL;

@Slf4j
@Service
public class SignalService {

    enum Recipient {
        ADMIN,
        RECIPIENTS
    }

    public static final String SIGNAL_JSON = "{\"message\": \"%s\", \"number\": \"%s\", \"recipients\": [ %s ]}";

    private final SignalApiConfig config;
    private final WebClient webClient;
    private final String recipientsString;
    private final String adminString;

    /*
     * Startup
     */
    public SignalService(SignalApiConfig config, @Qualifier("SignalWebClient") WebClient webClient) {
        this.config = config;
        this.webClient = webClient;
        this.recipientsString = config.phoneRecipients().stream()
                .map(number -> "\"" + number + "\"")
                .collect(Collectors.joining(","));
        this.adminString = String.format("\"%s\"", config.phoneAdmin());
    }

    /*
     * Functions
     */

    @Async
    public void sendMessageToAdmin(String message) {
        sendMessage(message, ADMIN);
    }

    @Async
    public void sendMessageToRecipients(String message) {
        sendMessage(message, RECIPIENTS);
    }

    private void sendMessage(String message, Recipient recipient) {
        try {
            send(message, recipient);
            logNotificationOK();
        } catch (Exception e) {
            logNotificationError(e);
            throw new SignalException(String.format("Something went wrong: %s", e.getMessage()));
        }
    }

    private void send(String message, Recipient recipient) {

        String jsonMessage = buildJson(message, recipient);

        if (!config.active()) {
            log.info("Signal notifications are disabled. Skip sending");
            log.info("This would have been the message: {}", jsonMessage);
            return;
        }
        log.info("Signal notifications are enabled. Sending");

        String apiResponse = webClient
                .post()
                .uri("/v2/send")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonMessage)
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry
                        .fixedDelay(config.retryCount(), Duration.ofSeconds(config.retryInterval()))
                        .filter(WebClientRetryFilter::shouldRetry)
                        .filter(SignalErrorRetryFilter::shouldRetry)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new SignalException("Sending notification has failed: {}" + retrySignal.failure());
                        }))
                .doOnError(error -> log.error("Sending notification has failed: {}", error.getMessage()))
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

    private String buildJson(String message, Recipient recipient) {

        // if(recipient==ADMIN)
        String actualRecipient = adminString;
        if (recipient == RECIPIENTS) {
            actualRecipient = recipientsString;
        }

        String json = String.format(SIGNAL_JSON, prefixMessage(message), config.phoneSender(), actualRecipient);
        log.info("Built message is: {}", json);
        return json;
    }

    private String prefixMessage(String message) {
        return String.format("%s\\n%s", config.messagePrefix(), message);
    }

    private void logNotificationOK() {
        log.debug("Notification has been sent, if enabled.");
    }

    private void logNotificationError(Exception e) {
        log.error("Notification could not been sent: {}", e.getMessage());
    }
}
