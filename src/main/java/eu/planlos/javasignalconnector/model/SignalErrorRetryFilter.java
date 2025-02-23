package eu.planlos.javasignalconnector.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
public class SignalErrorRetryFilter {
    public SignalErrorRetryFilter() {
    }

    public static boolean shouldRetry(Throwable throwable) {
        log.warn("Testing exception for retry. Class is {}", throwable.getClass());
        if (throwable instanceof WebClientResponseException responseException) {
            return isNotUntrustedIdentityIssue(responseException);
        }

        return true;
    }

    private static boolean isNotUntrustedIdentityIssue(WebClientResponseException responseException) {
        boolean isNotUntrustedIdentityIssue = !responseException.getMessage().contains("Untrusted Identity");
        log.warn("Is NOT untrusted identity issue -> No retry: {}", isNotUntrustedIdentityIssue);
        return isNotUntrustedIdentityIssue;
    }
}
