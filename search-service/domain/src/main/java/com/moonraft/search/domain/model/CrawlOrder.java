package com.moonraft.search.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Component
public class CrawlOrder {

    private long linkCount;
    private long textSize;
    private int pageCount;

    private WebPage webPages = new WebPage();
    private String seedURL;
    private OrderStatus orderStatus;

    public void incrementPageCount() {
        this.pageCount++;
    }

    public void incrementLinkCount(int count) {
        this.linkCount += count;
    }

    public void incrementTextSize(int size) {
        this.textSize += size;
    }

}
