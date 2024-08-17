package com.moonraft.search.domain.security.filters;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.moonraft.search.domain.exception.EntityNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
//                response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//                response.setHeader("Access-Control-Allow-Credentials", "true");
//                response.setHeader("Access-Control-Allow-Methods",
//                        "ACL, CANCELUPLOAD, CHECKIN, CHECKOUT, COPY, DELETE, GET, HEAD, LOCK, MKCALENDAR, MKCOL, MOVE, OPTIONS, POST, PROPFIND, PROPPATCH, PUT, REPORT, SEARCH, UNCHECKOUT, UNLOCK, UPDATE, VERSION-CONTROL");
//                response.setHeader("Access-Control-Max-Age", "3600");
//                response.setHeader("Access-Control-Allow-Headers",
//                        "Origin, X-Requested-With, Content-Type, Accept, Key, Authorization");
//                response.setHeader("Access-Control-Expose-Headers", "Authorization");

                filterChain.doFilter(request,response);

        }catch (EntityNotFoundException e){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (JWTVerificationException e){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } catch (RuntimeException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
