package com.solomatoff.chat.config;

import com.solomatoff.chat.filter.JWTAuthenticationFilter;
import com.solomatoff.chat.filter.JWTAuthorizationFilter;
import com.solomatoff.chat.jwt.TokenAuthenticationService;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.context.annotation.Bean;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String SIGN_UP_URL = "/api/v1/person/";

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenAuthenticationService tokenAuthenticationService;

    public SecurityConfig(UserDetailsService userDetailsService,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          TokenAuthenticationService tokenAuthenticationService) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenAuthenticationService = tokenAuthenticationService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                .antMatchers(HttpMethod.PATCH, SIGN_UP_URL + "*/role/*").permitAll() // Only for test
                .antMatchers(HttpMethod.GET, SIGN_UP_URL + "*/role/*").hasAnyRole("ADMIN", "SUPERVISOR")
                .antMatchers(HttpMethod.POST, SIGN_UP_URL + "*/role/*").hasAnyRole("ADMIN", "SUPERVISOR")
                .antMatchers(HttpMethod.PUT, SIGN_UP_URL + "*/role/*").hasAnyRole("ADMIN", "SUPERVISOR")
                .antMatchers(HttpMethod.DELETE, SIGN_UP_URL + "*/role/*").hasAnyRole("ADMIN", "SUPERVISOR")
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), tokenAuthenticationService))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), tokenAuthenticationService))
                /* this disables session creation on Spring Security */
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
