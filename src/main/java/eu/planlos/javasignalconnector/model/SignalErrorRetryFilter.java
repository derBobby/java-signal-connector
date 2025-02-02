package eu.planlos.javasignalconnector.model;

import org.springframework.web.reactive.function.client.WebClientResponseException;

public class SignalErrorRetryFilter {
    public SignalErrorRetryFilter() {
    }

    public static boolean shouldRetry(Throwable throwable) {
        if (throwable instanceof WebClientResponseException responseException) {
            return isNotUntrustedIdentityIssue(responseException);
        }

        return true;
    }

    private static boolean isNotUntrustedIdentityIssue(WebClientResponseException responseException) {
        return !responseException.getMessage().contains("Untrusted Identity");
    }
}
