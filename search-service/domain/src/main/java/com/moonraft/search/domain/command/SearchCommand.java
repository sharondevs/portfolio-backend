package com.moonraft.search.domain.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class SearchCommand extends Command {
    private String searchQuery;
    private String domain;
}
