package com.moonraft.search.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class WebPage {
    private List<String> url = new ArrayList<>();
    private List<String> page = new ArrayList<>();
    private List<String> title = new ArrayList<>();

    public void clearPage(){this.page = new ArrayList<>();}

    public void clearUrl(){this.url = new ArrayList<>();}
}