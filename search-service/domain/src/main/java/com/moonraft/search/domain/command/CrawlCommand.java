package com.moonraft.search.domain.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class CrawlCommand extends Command {
    private String domain;
    private String seedUrl;
}
