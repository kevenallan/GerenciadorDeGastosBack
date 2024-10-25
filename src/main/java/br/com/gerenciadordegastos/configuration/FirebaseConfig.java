package br.com.gerenciadordegastos.configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

	@Value("${firebase.gerenciadordegastos.config}")
	private String firebaseGerenciadorDeGastosConfig;

	@Bean
	FirebaseApp initializeFirebase() throws IOException {
		InputStream firebaseConfigStream = new ByteArrayInputStream(
				firebaseGerenciadorDeGastosConfig.getBytes(StandardCharsets.UTF_8));

		FirebaseOptions options = FirebaseOptions.builder()
				.setCredentials(GoogleCredentials.fromStream(firebaseConfigStream)).build();

		return FirebaseApp.initializeApp(options);
	}

}
