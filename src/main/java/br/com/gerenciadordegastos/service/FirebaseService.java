package br.com.gerenciadordegastos.service;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.cloud.FirestoreClient;

import br.com.gerenciadordegastos.model.FirebaseConfigModel;

@Service
public class FirebaseService {

	@Value("${firebase.storage.bucket-name}")
	private String bucketName;

	@Value("${firebase.gerenciadordegastos.config}")
	private String firebaseGerenciadorDeGastosConfig;

	// FIREBASE-CONFIG
	@Value("${firebase.config.apiKey}")
	private String firebaseConfigApiKey;

	@Value("${firebase.config.authDomain}")
	private String firebaseConfigAuthDomain;

	@Value("${firebase.config.projectId}")
	private String firebaseConfigProjectId;

	@Value("${firebase.config.storageBucket}")
	private String firebaseConfigStorageBucket;

	@Value("${firebase.config.messagingSenderId}")
	private String firebaseConfigMessagingSenderId;

	@Value("${firebase.config.appId}")
	private String firebaseConfigAppId;

	public String getBucketName() {
		return this.bucketName;
	}

	public Firestore getConectionFirestoreDataBase() {
		return FirestoreClient.getFirestore();
	}

	public Storage initStorage() throws FileNotFoundException, IOException {
		InputStream firebaseConfigStream = new ByteArrayInputStream(
				firebaseGerenciadorDeGastosConfig.getBytes(StandardCharsets.UTF_8));
		return StorageOptions.newBuilder().setCredentials(GoogleCredentials.fromStream(firebaseConfigStream)).build()
				.getService();
	}

	public FirebaseConfigModel getConfig() {
		return new FirebaseConfigModel(firebaseConfigApiKey, firebaseConfigAuthDomain, firebaseConfigProjectId,
				firebaseConfigStorageBucket, firebaseConfigMessagingSenderId, firebaseConfigAppId);
	}

}
