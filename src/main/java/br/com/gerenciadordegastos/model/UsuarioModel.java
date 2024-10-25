package br.com.gerenciadordegastos.model;

import java.util.List;

import com.google.cloud.firestore.annotation.Exclude;

import lombok.Data;

@Data
public class UsuarioModel {

	@Exclude
	private String id;
	private String nome;
	private String email;
	private String usuario;
	private String senha;

	private List<String> arquivosCompartilhados;

}
