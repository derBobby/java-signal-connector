package eu.planlos.javasignalconnector.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SignalApiConfig.class})
public class SignalConfigBeanConfiguration {
}