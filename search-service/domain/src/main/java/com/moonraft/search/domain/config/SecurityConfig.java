package com.moonraft.search.domain.config;

import com.moonraft.search.domain.security.filters.AuthenticationFilter;
import com.moonraft.search.domain.security.filters.ExceptionHandlerFilter;
import com.moonraft.search.domain.security.filters.JWTAuthorizationFilter;
import com.moonraft.search.domain.service.api.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    AuthenticationManager authenticationManager;
    UserService userService;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, authenticationManager);
        authenticationFilter.setFilterProcessesUrl("/authenticate");

        http.csrf((csrf)->csrf.disable())
                .authorizeHttpRequests((authz)->authz.requestMatchers(HttpMethod.GET,"userinfo","crawl/status").permitAll()
                        .requestMatchers(HttpMethod.POST,"register").permitAll().requestMatchers(HttpMethod.POST,"crawl").hasAuthority(SecurityConstants.ROLES.ADMIN.toString())
                        .requestMatchers(HttpMethod.POST,"search").hasAnyAuthority(SecurityConstants.ROLES.ADMIN.toString(), SecurityConstants.ROLES.USER.toString())
                        .requestMatchers(HttpMethod.OPTIONS,"authenticate").permitAll()
                        .anyRequest().permitAll())
                .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
                .addFilter(authenticationFilter)
                .addFilterAfter(new JWTAuthorizationFilter(), AuthenticationFilter.class)
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public UserDetailsService users(){
        UserDetails admin = User.builder().username("admin").password("password").build();

        return new InMemoryUserDetailsManager(admin);
    }

}