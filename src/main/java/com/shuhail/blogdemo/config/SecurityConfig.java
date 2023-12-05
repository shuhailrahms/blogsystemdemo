package com.shuhail.blogdemo.config;

import com.shuhail.blogdemo.domain.User;
import com.shuhail.blogdemo.service.BlogReactiveUserDetailsService;
import com.shuhail.blogdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.UUID;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    @Autowired
    private UserService userService;

    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http.authorizeExchange(configurer ->
                configurer
                        .pathMatchers(HttpMethod.GET, "/article", "/article/show/**", "/webjars/**", "/css/**", "/favicon.ico", "/").permitAll()
                        .pathMatchers(HttpMethod.POST, "/article").authenticated()
                        .pathMatchers("/article/edit/**", "/article/new", "/article/delete/**").authenticated())
                .csrf(csrfSpec -> csrfSpec.disable())
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .build();
    }

    @Bean
    public UserDetailsRepositoryReactiveAuthenticationManager authenticationManager (BlogReactiveUserDetailsService blogReactiveUserDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager userDetailsRepositoryReactiveAuthenticationManager = new
                UserDetailsRepositoryReactiveAuthenticationManager(blogReactiveUserDetailsService);
        userDetailsRepositoryReactiveAuthenticationManager.setPasswordEncoder(passwordEncoder());
        return userDetailsRepositoryReactiveAuthenticationManager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            userService.deleteAll();
            userService.save(new User(UUID.randomUUID().toString(), "user", passwordEncoder().encode("password"), "USER", "User of Blog"));
            userService.save(new User(UUID.randomUUID().toString(), "admin", passwordEncoder().encode("password"), "ADMIN", "Admin of Blog"));
        };
    }

}
