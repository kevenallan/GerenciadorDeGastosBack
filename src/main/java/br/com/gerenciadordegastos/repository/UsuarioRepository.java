package br.com.gerenciadordegastos.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;

import br.com.gerenciadordegastos.exception.CustomException;
import br.com.gerenciadordegastos.model.UsuarioModel;

@Component
public class UsuarioRepository extends Repository {

	private final String COLLECTION_USUARIO = "USUARIO";

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UsuarioModel cadastrar(UsuarioModel usuarioModel) throws Exception {

		this.buscarUsuarioPorUsuarioEEmail(usuarioModel.getUsuario(), usuarioModel.getEmail());

		Firestore db = getConectionFirestoreDataBase();

		String idDocumento = UUID.randomUUID().toString();
		DocumentReference docRef = db.collection(COLLECTION_USUARIO).document(idDocumento);

		String senhaSemCriptografia = usuarioModel.getSenha();
		String senhaComCriptografia = passwordEncoder.encode(usuarioModel.getSenha());

		Map<String, Object> usuarioData = new HashMap<>();
		usuarioData.put("nome", usuarioModel.getNome());
		usuarioData.put("email", usuarioModel.getEmail());
		usuarioData.put("usuario", usuarioModel.getUsuario());
		usuarioData.put("senha", senhaComCriptografia);

		ApiFuture<WriteResult> result = docRef.set(usuarioData);

		result.get();

		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot document = future.get();

		if (document.exists()) {
			UsuarioModel usuarioCadastrado = getUsuarioModel(document);
			usuarioCadastrado.setSenha(senhaSemCriptografia);
			return usuarioCadastrado;
		} else {
			throw new CustomException("Falha ao cadastrar o usuário.");
		}
	}

	public UsuarioModel cadastrarUsuarioGoogle(UsuarioModel usuarioModel) throws Exception {

		Firestore db = getConectionFirestoreDataBase();

		String idDocumento = usuarioModel.getId();
		DocumentReference docRef = db.collection(COLLECTION_USUARIO).document(idDocumento);

		Map<String, Object> usuarioData = new HashMap<>();
		usuarioData.put("nome", usuarioModel.getNome());
		usuarioData.put("email", usuarioModel.getEmail());

		ApiFuture<WriteResult> result = docRef.set(usuarioData);

		result.get();

		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot document = future.get();

		if (document.exists()) {
			UsuarioModel usuarioCadastrado = getUsuarioModel(document);
			return usuarioCadastrado;
		} else {
			throw new CustomException("Falha ao cadastrar o usuário.");
		}
	}

	public UsuarioModel login(String usuario, String senha) throws InterruptedException, ExecutionException {
		Firestore db = getConectionFirestoreDataBase();

		Query query = db.collection(COLLECTION_USUARIO).whereEqualTo("usuario", usuario);

		ApiFuture<QuerySnapshot> querySnapshot = query.get();

		List<QueryDocumentSnapshot> documentos = querySnapshot.get().getDocuments();

		if (documentos.isEmpty()) {
			throw new CustomException("Usuário inválido.");
		}

		QueryDocumentSnapshot documento = documentos.get(0);

		UsuarioModel usuarioModel = getUsuarioModel(documento);

		if (!passwordEncoder.matches(senha, usuarioModel.getSenha())) {
			throw new CustomException("Senha inválida.");
		}

		return usuarioModel;

	}

	public void buscarUsuarioPorUsuarioEEmail(String usuario, String email) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		Query queryUsuario = dbFirestore.collection(COLLECTION_USUARIO).whereEqualTo("usuario", usuario);

		ApiFuture<QuerySnapshot> futureUsuario = queryUsuario.get();

		List<QueryDocumentSnapshot> documentosUsuario = futureUsuario.get().getDocuments();

		if (!documentosUsuario.isEmpty()) {
			throw new CustomException("Este nome de usuario já está em uso. Tente outro.");
		}

		Query queryEmail = dbFirestore.collection(COLLECTION_USUARIO).whereEqualTo("email", email);

		ApiFuture<QuerySnapshot> futureEmail = queryEmail.get();

		List<QueryDocumentSnapshot> documentosEmail = futureEmail.get().getDocuments();

		if (!documentosEmail.isEmpty()) {
			throw new CustomException("Este e-mail já está em uso. Tente outro.");
		}
	}

	public UsuarioModel buscarUsuarioPorEmail(String email) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		Query queryEmail = dbFirestore.collection(COLLECTION_USUARIO).whereEqualTo("email", email);

		ApiFuture<QuerySnapshot> futureEmail = queryEmail.get();

		List<QueryDocumentSnapshot> documentosEmail = futureEmail.get().getDocuments();

		if (documentosEmail.isEmpty()) {
			throw new CustomException("E-mail inválido");

		}
		QueryDocumentSnapshot documento = documentosEmail.get(0);

		return getUsuarioModel(documento);

	}

	public UsuarioModel buscarUsuarioGooglePorEmail(String email) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		Query queryEmail = dbFirestore.collection(COLLECTION_USUARIO).whereEqualTo("email", email);

		ApiFuture<QuerySnapshot> futureEmail = queryEmail.get();

		List<QueryDocumentSnapshot> documentosEmail = futureEmail.get().getDocuments();

		if (documentosEmail.isEmpty()) {
			return null;

		}
		QueryDocumentSnapshot documento = documentosEmail.get(0);

		return getUsuarioModel(documento);

	}

	public void atualizarSenha(String idUsuario, String novaSenha) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		DocumentReference docRef = dbFirestore.collection(COLLECTION_USUARIO).document(idUsuario);

		DocumentSnapshot documentSnapshot = docRef.get().get();
		if (documentSnapshot.exists()) {
			String senhaAtual = documentSnapshot.getString("senha");
			if (passwordEncoder.matches(novaSenha, senhaAtual)) {
				throw new CustomException("A nova senha não pode ser igual à senha atual.");
			}

			ApiFuture<WriteResult> futureUpdate = docRef.update("senha", passwordEncoder.encode(novaSenha));
			futureUpdate.get();
		} else {
			throw new CustomException("Erro na atualização da senha.");
		}
	}

	public UsuarioModel getDadosUsuario(String idUsuario) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		DocumentReference docRef = dbFirestore.collection(COLLECTION_USUARIO).document(idUsuario);

		DocumentSnapshot documentSnapshot = docRef.get().get();
		if (documentSnapshot.exists()) {
			return getUsuarioModel(documentSnapshot);
		} else {
			throw new CustomException("Erro ao buscar os dados do usuário.");
		}
	}

	public void atualizarUsuario(String idUsuario, UsuarioModel usuarioAtualizado) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		DocumentReference docRef = dbFirestore.collection(COLLECTION_USUARIO).document(idUsuario);

		DocumentSnapshot documentSnapshot = docRef.get().get();
		if (documentSnapshot.exists()) {

			String senhaAtual = documentSnapshot.getString("senha");
			ApiFuture<WriteResult> futureUpdate = null;
			if (passwordEncoder.matches(usuarioAtualizado.getSenha(), senhaAtual)
					|| usuarioAtualizado.getSenha().equals(senhaAtual)) {
				futureUpdate = docRef.update("nome", usuarioAtualizado.getNome(), "email", usuarioAtualizado.getEmail(),
						"usuario", usuarioAtualizado.getUsuario());
			} else {
				futureUpdate = docRef.update("nome", usuarioAtualizado.getNome(), "email", usuarioAtualizado.getEmail(),
						"usuario", usuarioAtualizado.getUsuario(), "senha",
						passwordEncoder.encode(usuarioAtualizado.getSenha()));
			}

			futureUpdate.get();
		} else {
			throw new CustomException("Erro na atualização do usuário.");
		}
	}

	public void atualizarUsuarioGoogle(String idUsuario, UsuarioModel usuarioAtualizado) throws Exception {
		Firestore dbFirestore = getConectionFirestoreDataBase();

		DocumentReference docRef = dbFirestore.collection(COLLECTION_USUARIO).document(idUsuario);

		DocumentSnapshot documentSnapshot = docRef.get().get();
		if (documentSnapshot.exists()) {
			ApiFuture<WriteResult> futureUpdate = null;

			futureUpdate = docRef.update("nome", usuarioAtualizado.getNome());

			futureUpdate.get();
		} else {
			throw new CustomException("Erro na atualização do nome do usuário.");
		}
	}

	public void deletarUsuarioPorId(String documentId) {
		try {
			Firestore dbFirestore = getConectionFirestoreDataBase();
			dbFirestore.collection(COLLECTION_USUARIO).document(documentId).delete().get();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CustomException("Erro ao tentar deletar o usuário");
		}
	}

	private UsuarioModel getUsuarioModel(DocumentSnapshot documentSnapshot) {
		UsuarioModel usuarioModel = documentSnapshot.toObject(UsuarioModel.class);
		usuarioModel.setId(documentSnapshot.getId());
		return usuarioModel;
	}

}
