package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.example.security.TokenHelper;
import com.example.security.auth.RestAuthenticationEntryPoint;
import com.example.security.auth.TokenAuthenticationFilter;
import com.example.service.impl.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

	@Autowired
	private CustomUserDetailsService jwtUserDetailsService;

	@Autowired
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Autowired
	TokenHelper tokenHelper;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http.addFilterBefore(new TokenAuthenticationFilter(tokenHelper, jwtUserDetailsService),
				BasicAuthenticationFilter.class)
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers(HttpMethod.GET, "/", "/auth/**", "/webjars/**", "/*.html", "/favicon.ico",
								"/**/*.html", "/**/*.css", "/**/*.js")
						.permitAll()
						.requestMatchers("/auth/**", "/error").permitAll()
						.anyRequest().authenticated())
				.sessionManagement(sec -> sec.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(
						exceptionHandler -> exceptionHandler.authenticationEntryPoint(restAuthenticationEntryPoint))
				.csrf(csrf -> csrf.disable());

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		// TokenAuthenticationFilter will ignore the below paths
		return (web) -> web.ignoring()
				.requestMatchers(HttpMethod.POST, "/auth/login")
				.requestMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "/favicon.ico", "/**/*.html",
						"/**/*.css", "/**/*.js");
	}
}
