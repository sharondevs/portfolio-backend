package com.moonraft.search.domain.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESSearchRequest {

    private String index;
    private List<String> sourcesToFetch = new ArrayList<>();
    private String field;
    private int k;
    private int numOfCandidates;
    private Float[] queryVector;


}
