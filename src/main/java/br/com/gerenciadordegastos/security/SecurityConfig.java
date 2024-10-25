package br.com.gerenciadordegastos.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Value("${cors.allowed-origins}")
	private String allowedOrigins;

	private final TokenInterceptorFilter tokenInterceptorFilter;

	public SecurityConfig(TokenInterceptorFilter tokenInterceptorFilter) {
		this.tokenInterceptorFilter = tokenInterceptorFilter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(request -> {
			var corsConfiguration = new CorsConfiguration();
			corsConfiguration.setAllowedOrigins(List.of(allowedOrigins.split(",")));
			corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
			corsConfiguration.setAllowedHeaders(List.of("*"));
			corsConfiguration.setAllowCredentials(true);
			return corsConfiguration;
		})).authorizeHttpRequests(auth -> auth
				.requestMatchers("/usuario/login", "/usuario/cadastrar", "/usuario/esqueceu-sua-senha",
						"/usuario/alterar-senha", "/firebase/get-config", "/usuario/login-google")
				.permitAll().anyRequest().authenticated())
				.addFilterBefore(tokenInterceptorFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}