package com.moonraft.search.domain.service.api;

import com.moonraft.search.domain.command.UserRegisterCommand;
import com.moonraft.search.domain.model.User;
import com.moonraft.search.domain.model.UserDetailsResponse;

public interface UserService {

    UserDetailsResponse getUser(Long id);

    String registerUser(UserRegisterCommand userRegisterCommand);

    User getUser(String username);
}
