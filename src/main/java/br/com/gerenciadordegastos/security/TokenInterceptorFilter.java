package br.com.gerenciadordegastos.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;

import br.com.gerenciadordegastos.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class TokenInterceptorFilter extends OncePerRequestFilter {

	@Autowired
	private AuthService authService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");
		String uri = request.getRequestURI();

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			String token = authorizationHeader.substring(7); // Remove "Bearer " do token
			try {
//                this.authService.validateToken(token);
				String userId = this.authService.extractUserId(token);
				this.authService.uuidUsuarioLogado = userId;
				// ??????????
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userId,
						null, null);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (JWTDecodeException e) {
				System.out.println("TOKEN JWT INVALIDO PARA A URI: " + uri + " - ERRO - " + e.getMessage());
				SecurityContextHolder.clearContext();
			} catch (TokenExpiredException e) {
				System.out.println("TOKEN EXPIRADO: " + e.getMessage());
				SecurityContextHolder.clearContext();
			} catch (Exception e) {
				System.out.println("Token Interceptor Error: " + e.getMessage());
				SecurityContextHolder.clearContext();
			}
		}

		// Continua com a cadeia de filtros
		filterChain.doFilter(request, response);
	}
}
