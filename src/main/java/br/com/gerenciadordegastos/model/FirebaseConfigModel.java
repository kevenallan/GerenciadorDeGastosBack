package br.com.gerenciadordegastos.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FirebaseConfigModel {

	private String apiKey;
	private String authDomain;
	private String projectId;
	private String storageBucket;
	private String messagingSenderId;
	private String appId;

}
