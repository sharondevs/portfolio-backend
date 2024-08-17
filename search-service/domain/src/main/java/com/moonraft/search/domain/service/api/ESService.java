package com.moonraft.search.domain.service.api;

import java.util.List;


public interface ESService {

    void processPage(String domain, float[][] vectorData, List<String[]> plainData, int numberOfDataPoints);


}
