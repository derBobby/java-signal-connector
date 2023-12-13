package eu.planlos.javasignalconnector;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@TestPropertySource(locations = "classpath:signal-test.properties")
class SignalServiceIT {

    @Autowired
    private SignalService signalService;

    @Autowired
    @SpyBean
    private WebClient webClient;

    @Test
    void sendMessages_isSuccessful() {
        signalService.sendMessageToRecipients("Test message to recipients!");
        signalService.sendMessageToAdmin("Test message to admin!");
        verify(webClient, times(2)).post();
    }
}