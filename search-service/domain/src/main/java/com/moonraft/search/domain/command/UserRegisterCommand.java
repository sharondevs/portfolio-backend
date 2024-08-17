package com.moonraft.search.domain.command;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class UserRegisterCommand extends Command{
    private String username;
    private String password;
    private String domain;
    private String role;
}
