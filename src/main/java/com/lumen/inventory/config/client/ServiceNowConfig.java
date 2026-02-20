
package com.lumen.inventory.config;

import com.lumen.snow.service.SNOWRestClient;
import com.lumen.snow.service.impl.SNOWRestClientImpl;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ServiceNowConfig {

	@Value("${snow.api.base-url}")
	private String snowBaseUrl;
	@Value("${snow.api.app-key}")
	private String appKey;
	@Value("${snow.api.secret}")
	private String secret;
	@Value("${snow.api.username}")
	private String username;

	@SuppressWarnings("null")
    @Bean(name = "snowWebClient", autowireCandidate = false)
	public WebClient snowWebClient(ExchangeStrategies customExchangeStrategies,
								   ObservationRegistry observationRegistry,
								   ExchangeFilterFunction logRequest) {
		return WebClient.builder()
				.baseUrl(snowBaseUrl)
				.exchangeStrategies(customExchangeStrategies)
				.observationRegistry(observationRegistry)
				.filter(logRequest)
				.defaultHeader("Accept", "application/json")
				.defaultHeader("Content-Type", "application/json")
				.defaultHeader("App-Key", appKey)
				.defaultHeader("App-Secret", secret)
				.defaultHeader("Username", username)
				.build();
	}

	@Bean
	public SNOWRestClient snowRestClient(WebClient snowWebClient) {
		return new SNOWRestClientImpl(snowWebClient);
	}
}

