package br.com.gerenciadordegastos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.gerenciadordegastos.model.FirebaseConfigModel;
import br.com.gerenciadordegastos.model.ResponseModel;
import br.com.gerenciadordegastos.service.FirebaseService;

@RestController
@RequestMapping("/firebase")
public class FirebaseController {

	@Autowired
	private FirebaseService firebaseService;

	@GetMapping("/get-config")
	public ResponseEntity<ResponseModel<FirebaseConfigModel>> getConfig() {
		return new ResponseEntity<>(new ResponseModel<>(null, this.firebaseService.getConfig()), HttpStatus.OK);
	}

}
