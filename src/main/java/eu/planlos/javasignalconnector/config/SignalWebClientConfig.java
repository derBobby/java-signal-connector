package eu.planlos.javasignalconnector.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Slf4j
public class SignalWebClientConfig {

    @Bean
    @Qualifier("SignalWebClient")
    public static WebClient configureSignalWebClient(SignalApiConfig apiConfig) {

        log.info("Creating WebClient using:");
        log.info("- Signal address: {}", apiConfig.address());
        log.info("- Signal username: {}", apiConfig.user());

        return WebClient.builder()
                .baseUrl(apiConfig.address())
                //TODO
//                .filter(WebClientRequestFilter.logRequest())
//                .filter(WebClientResponseFilter.logResponse())
//                .filter(WebClientResponseFilter.handleError())
                .filter(ExchangeFilterFunctions.basicAuthentication(apiConfig.user(), apiConfig.password()))
                .defaultHeaders(httpHeaders -> httpHeaders.set(HttpHeaders.ACCEPT, "application/json"))
                .build();
    }
}