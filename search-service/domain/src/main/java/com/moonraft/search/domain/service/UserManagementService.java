package com.moonraft.search.domain.service;

import com.moonraft.search.domain.command.UserRegisterCommand;
import com.moonraft.search.domain.exception.EntityNotFoundException;
import com.moonraft.search.domain.model.User;
import com.moonraft.search.domain.model.UserDetailsResponse;
import com.moonraft.search.domain.repository.UserRepository;
import com.moonraft.search.domain.service.api.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class UserManagementService implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserDetailsResponse getUser(Long id) {
        Optional<User> entity = userRepository.findById(id);
        log.info("Fetching User details for id : {}", id.intValue());
        User user = unwrapUser(entity, id);
        return UserDetailsResponse.builder().username(user.getUsername()).domain(user.getDomain()).build();
      }

      @Override
      public User getUser(String username){
         return unwrapUser(userRepository.findByUsername(username), Long.valueOf(404));
      }

    @Override
    public String registerUser(UserRegisterCommand userRegisterCommand){
        log.info("The password supplied is : {}", userRegisterCommand.getPassword());
        userRepository.save(User.builder().password(bCryptPasswordEncoder.encode(userRegisterCommand.getPassword())).role(userRegisterCommand.getRole()).domain(userRegisterCommand.getDomain()).username(userRegisterCommand.getUsername()).build());
        log.info("Processing registration of User {} ...", userRegisterCommand.getUsername());
        return "REGISTRATION_SUCCESSFUL";
    }

    public static User unwrapUser(Optional<User> entity, Long id){
        if(entity.isPresent()) return entity.get();
        else throw new EntityNotFoundException(id, User.class);
    }
}
