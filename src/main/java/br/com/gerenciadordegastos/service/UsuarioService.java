package br.com.gerenciadordegastos.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.gerenciadordegastos.dto.LoginDTO;
import br.com.gerenciadordegastos.exception.CustomException;
import br.com.gerenciadordegastos.model.UsuarioModel;
import br.com.gerenciadordegastos.repository.UsuarioRepository;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private AuthService authService;

	public UsuarioModel cadastrar(UsuarioModel usuario) throws Exception {
		return this.usuarioRepository.cadastrar(usuario);
	}

	public LoginDTO login(UsuarioModel usuarioModel) throws InterruptedException, ExecutionException {

		UsuarioModel usuarioLogado = this.usuarioRepository.login(usuarioModel.getUsuario(), usuarioModel.getSenha());

		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setUsuarioModel(usuarioLogado);
		loginDTO.setToken(this.authService.gerarToken(usuarioLogado.getId()));
		return loginDTO;
	}

	public LoginDTO loginGoogle(UsuarioModel usuarioModel) throws Exception {

		UsuarioModel usuarioCadastrado = this.usuarioRepository.buscarUsuarioGooglePorEmail(usuarioModel.getEmail());

		if (usuarioCadastrado == null) {
			usuarioCadastrado = this.usuarioRepository.cadastrarUsuarioGoogle(usuarioModel);
		}

		if (usuarioCadastrado != null && !usuarioCadastrado.getId().equals(usuarioModel.getId())) {
			throw new CustomException("Já existe uma conta utilizando esse e-mail");
		}

		LoginDTO loginDTO = new LoginDTO();
		loginDTO.setUsuarioModel(usuarioCadastrado);
		loginDTO.setToken(this.authService.gerarToken(usuarioModel.getId()));
		return loginDTO;
	}

//	public void esqueceuSuaSenha(String email) throws Exception {
//		UsuarioModel usuarioEncontrado = this.usuarioRepository.buscarUsuarioPorEmail(email);
//		if (usuarioEncontrado != null) {
//			this.emailService.enviarEmail(usuarioEncontrado);
//		}
//	}

	public void alterarSenha(String novaSenha, String token) throws Exception {
		try {
			String idUsuario = this.authService.extractUserId(token);
			this.usuarioRepository.atualizarSenha(idUsuario, novaSenha);
		} catch (JWTVerificationException e) {
			throw new CustomException(
					"A validade desse link expirou. Por favor solicite uma nova redefinição de senha.");
		}
	}

	public void atualizarUsuario(UsuarioModel usuarioModel) throws Exception {
		this.usuarioRepository.atualizarUsuario(this.authService.uuidUsuarioLogado, usuarioModel);
	}

	public void atualizarUsuarioGoogle(UsuarioModel usuarioModel) throws Exception {
		this.usuarioRepository.atualizarUsuarioGoogle(this.authService.uuidUsuarioLogado, usuarioModel);
	}

	public UsuarioModel getDadosUsuario() {
		try {
			return this.usuarioRepository.getDadosUsuario(this.authService.uuidUsuarioLogado);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException("Erro ao pegar os dados do usuário logado.");
		}

	}

	public void deletarUsuarioPorId()
			throws FileNotFoundException, InterruptedException, ExecutionException, IOException {
		this.usuarioRepository.deletarUsuarioPorId(this.authService.uuidUsuarioLogado);
	}

}
