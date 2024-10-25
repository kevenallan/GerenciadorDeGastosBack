package br.com.gerenciadordegastos.dto;

import br.com.gerenciadordegastos.model.UsuarioModel;
import lombok.Data;

@Data
public class LoginDTO {

	private UsuarioModel usuarioModel;
	private String token;

}
