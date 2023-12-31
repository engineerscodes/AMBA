package org.amba.app.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfigurationSource;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {


    @Autowired
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private final AuthenticationProvider authenticationProvider;

    @Autowired
    CorsConfigurationSource corsConfigurationSource;

    // @Autowired// private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http.csrf(AbstractHttpConfigurer::disable).cors(httpSecurityCorsConfigurer -> {
         httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource);
    })         /*Need to add only admin usr to Admin routes - needs testing */
               .authorizeHttpRequests(auth ->
                       auth.requestMatchers("/auth/**").permitAll()
                               .requestMatchers("/admin/**").hasAuthority("ADMIN")
                               .anyRequest().authenticated()
               )
               .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
               .authenticationProvider(authenticationProvider)
               .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
              /* .logout(logout ->
                       logout.logoutUrl("/auth/logout")
                               .addLogoutHandler(logoutHandler)
                               .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
               ) */
       ;

       return http.build();
    }

}
