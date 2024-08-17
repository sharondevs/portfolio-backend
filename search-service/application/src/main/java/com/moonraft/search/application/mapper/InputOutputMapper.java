package com.moonraft.search.application.mapper;

import com.moonraft.search.application.controller.request.CrawlRequest;
import com.moonraft.search.application.controller.request.RegistrationRequest;
import com.moonraft.search.application.controller.request.SearchRequest;
import com.moonraft.search.domain.command.Command;
import com.moonraft.search.domain.command.CrawlCommand;
import com.moonraft.search.domain.command.SearchCommand;
import com.moonraft.search.domain.command.UserRegisterCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.function.BiFunction;

@Slf4j
public final class InputOutputMapper {


    private InputOutputMapper() {

    }

    public static final BiFunction<Command, String, Command> addUser = (command, user) -> {
        OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
        command.setExecBy(user);
        command.setExecOn(utc.toString());
        return command;
    };

    public static final BiFunction<SearchRequest, String, SearchCommand> buildSearchCommandFromSearchRequest = (searchRequest, user) -> {
        SearchCommand searchCommand =  SearchCommand.builder().build();
        BeanUtils.copyProperties(searchRequest, searchCommand);
        addUser.apply(searchCommand, user);
        return searchCommand;
    };

    public static final BiFunction<CrawlRequest, String, CrawlCommand> buildCrawlCommandFromCrawlRequest = (crawlRequest, user) -> {
        CrawlCommand crawlCommand =  CrawlCommand.builder().build();
        BeanUtils.copyProperties(crawlRequest, crawlCommand);
        addUser.apply(crawlCommand, user);
        return crawlCommand;
    };

    public static final BiFunction<RegistrationRequest, String, UserRegisterCommand> buildUserCommandFromUserRequest = (registrationRequest, user) -> {
        UserRegisterCommand userRegisterCommand = UserRegisterCommand.builder().build();
        BeanUtils.copyProperties(registrationRequest, userRegisterCommand);
        return userRegisterCommand;
    };

}
