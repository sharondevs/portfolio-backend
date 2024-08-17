package com.moonraft.search.domain.config;

public class SecurityConstants {
    public static final String SECRET_KEY = "c6d742c388d243e82e2c3cdfe783314115a5079e9b3612f99dd9082fbac4bf254184af4291ab27aa40ab86dede1562868152366394d9afe4ed6733b76471cbab";
    public static final int TOKEN_EXPIRATION = 7200000; // Milli seconds would be 2hrs, token expiry period
    public static final String ALLOW_ORIGIN = "http://localhost:3000";

    public static enum ROLES {ADMIN, USER};
    public static final String BEARER = "BEARER";
    public static final String AUTHORIZATION = "Authorization";

}
