package com.solomatoff.chat.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solomatoff.chat.domain.Person;
import com.solomatoff.chat.jwt.TokenAuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.solomatoff.chat.jwt.TokenAuthenticationService.HEADER_STRING;
import static com.solomatoff.chat.jwt.TokenAuthenticationService.TOKEN_PREFIX;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenAuthenticationService tokenAuthenticationService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
                                   TokenAuthenticationService tokenAuthenticationService) {
        this.authenticationManager = authenticationManager;
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {
        try {
            Person person = new ObjectMapper()
                    .readValue(req.getInputStream(), Person.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            person.getLogin(),
                            person.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        String token = tokenAuthenticationService.generateToken(authentication);
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }

}