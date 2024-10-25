package br.com.gerenciadordegastos.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

@Service
public class AuthService {

	@Value("${jwt.secret}")
	private String secretKey;

	private int jwtExpirationMs = 604800000; // Expira em 1 semana / 3600000 - Expira em 1 hora
	private int jwtExpirationRedefinirSenha = 300000; // 5 minutos

	public String uuidUsuarioLogado = "";

	public String gerarToken(String uuid) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		String token = JWT.create().withSubject(uuid)
				.withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
				.withExpiresAt(new Date((new java.util.Date()).getTime() + jwtExpirationMs)).sign(algorithm);

		return token;
	}

	public String gerarTokenRedefinicaoSenha(String uuid) {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		String token = JWT.create().withSubject(uuid)
				.withIssuedAt(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
				.withExpiresAt(new Date((new java.util.Date()).getTime() + jwtExpirationRedefinirSenha))
				.sign(algorithm);

		return token;
	}

	public DecodedJWT validateToken(String token) throws JWTVerificationException {
		Algorithm algorithm = Algorithm.HMAC256(secretKey);
		JWTVerifier verifier = JWT.require(algorithm).build();
		return verifier.verify(token);
	}

	public String extractUserId(String token) {
		return validateToken(token).getSubject();
	}
}