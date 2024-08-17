package com.moonraft.search.application.controller.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
public class CrawlRequest {
    private String domain;
    private String seedUrl;
}
