package com.moonraft.search.domain.service.api;

import com.moonraft.search.domain.command.VectorizeCommand;

import java.util.List;

public interface VectorService {

    List<String[]> getChunks();
    float[][] vectorize(VectorizeCommand vectorizeCommand, Integer requestID);
}
