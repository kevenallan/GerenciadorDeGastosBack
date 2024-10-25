package br.com.gerenciadordegastos.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.gerenciadordegastos.dto.LoginDTO;
import br.com.gerenciadordegastos.model.ResponseModel;
import br.com.gerenciadordegastos.model.UsuarioModel;
import br.com.gerenciadordegastos.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@PostMapping("/cadastrar")
	public ResponseEntity<ResponseModel<LoginDTO>> cadastrar(@RequestBody UsuarioModel usuarioModel) throws Exception {
		UsuarioModel usuarioCadastro = this.usuarioService.cadastrar(usuarioModel);
		LoginDTO loginDTO = this.usuarioService.login(usuarioCadastro);
		ResponseModel<LoginDTO> response = new ResponseModel<LoginDTO>(null, loginDTO);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/dados-usuario")
	public ResponseEntity<ResponseModel<UsuarioModel>> dadosUsuario() {
		UsuarioModel usuario = this.usuarioService.getDadosUsuario();
		ResponseModel<UsuarioModel> response = new ResponseModel<>(null, usuario);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/atualizar-usuario")
	public ResponseEntity<ResponseModel<?>> atualizarUsuario(@RequestBody UsuarioModel usuarioModel) throws Exception {
		this.usuarioService.atualizarUsuario(usuarioModel);
		ResponseModel<?> response = new ResponseModel<>("Usuário atualizado com sucesso!", null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/atualizar-usuario-google")
	public ResponseEntity<ResponseModel<?>> atualizarUsuarioGoogle(@RequestBody UsuarioModel usuarioModel)
			throws Exception {
		this.usuarioService.atualizarUsuarioGoogle(usuarioModel);
		ResponseModel<?> response = new ResponseModel<>("Usuário atualizado com sucesso!", null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseModel<LoginDTO>> login(@RequestBody UsuarioModel usuarioModel)
			throws InterruptedException, ExecutionException {
		LoginDTO login = this.usuarioService.login(usuarioModel);
		ResponseModel<LoginDTO> response;
		response = new ResponseModel<>(null, login);

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping("/login-google")
	public ResponseEntity<ResponseModel<LoginDTO>> loginGoogle(@RequestBody UsuarioModel usuarioModel)
			throws Exception {
		LoginDTO login = this.usuarioService.loginGoogle(usuarioModel);
		ResponseModel<LoginDTO> response;

		response = new ResponseModel<>(null, login);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

//	@GetMapping("/esqueceu-sua-senha")
//	public ResponseEntity<ResponseModel<?>> esqueceuSuaSenha(@RequestParam("email") String email) throws Exception {
////		this.usuarioService.esqueceuSuaSenha(email);
//		ResponseModel<?> response = new ResponseModel<>("Email enviado. Verifique sua caixa de mensagens", null);
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}

	@PutMapping("/alterar-senha")
	public ResponseEntity<ResponseModel<?>> alterarSenha(@RequestParam("novaSenha") String novaSenha,
			@RequestParam("token") String token) throws Exception {
		this.usuarioService.alterarSenha(novaSenha, token);
		ResponseModel<?> response = new ResponseModel<>("Senha atualizada com sucesso!", null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/deletar")
	public ResponseEntity<ResponseModel<?>> deletar() throws Exception {
		this.usuarioService.deletarUsuarioPorId();
		ResponseModel<?> response = new ResponseModel<>("Usuário deletado com sucesso", null);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
