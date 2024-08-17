package com.moonraft.search.domain.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class VectorizeCommand extends Command{
    private String[] pages;
    private String[] titles;
    private int wordOverlap;
    private int chunkLength;
    private String[] Urls;
}
