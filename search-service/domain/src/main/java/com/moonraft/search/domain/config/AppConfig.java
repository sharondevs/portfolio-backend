package com.moonraft.search.domain.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Component
public final class AppConfig {

    @Value("#{environment.VECTORIZE_API}")
    private String vectorizeApi;

    @Value("#{environment.ES_HOST}")
    private String elasticSearchHost;

    @Value("#{environment.ES_PORT}")
    private String elasticSearchPort;

    @Value("#{environment.ES_LOGIN}")
    private String esLogin;

    @Value("#{environment.ES_PASSWORD}")
    private String esPassword;

    @Value("#{environment.FASTAPI_HOST}")
    private String fastapiHost;

    @Value("#{environment.FASTAPI_PORT}")
    private String fastapiPort;

    @Value("#{environment.FRONT_END_DOMAIN}")
    private String reactHost;


}
