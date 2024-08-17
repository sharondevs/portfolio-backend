package com.moonraft.search.domain.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppConstants {
    public static final String USER = "defaultUser";
    public static final String CERT_PATH = "http_ca.crt";

    public static final String INDEX = "webdomain";
    public static final int INSTANCES = 16;

    public static final int VECTORDIM = 768;
    public static final String FASTAPI_CRAWL_PATH = "vectorize_instructor_doc";

    public static final String FASTAPI_SEARCH_PATH = "vectorize_instructor_query";

    public static final List<String> SEARCH_RESULT_SOURCES = new ArrayList<String>(Arrays.asList("content", "url"));

    public static final int K = 50;

    public static final int NUM_OF_CANDIDATES = 200;

    public static final String FASTAPI_CONTENT_LIMIT = "content_limit";

    public static final String ALLOWED_METHODS = "ACL, CANCELUPLOAD, CHECKIN, CHECKOUT, COPY, DELETE, GET, HEAD, LOCK, MKCALENDAR, MKCOL, MOVE, OPTIONS, POST, PROPFIND, PROPPATCH, PUT, REPORT, SEARCH, UNCHECKOUT, UNLOCK, UPDATE, VERSION-CONTROL";

    public static final String ALLOWED_HEADERS = "Origin, X-Requested-With, Content-Type, Accept, Key, Authorization";

    public static final String EXPOSED_HEADERS = "Authorization";

    public static final int MAX_CRAWL_THREADS = 20;
}

