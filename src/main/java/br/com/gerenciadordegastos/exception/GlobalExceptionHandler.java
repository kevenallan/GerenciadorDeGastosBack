package br.com.gerenciadordegastos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<String> handleCustomException(CustomException ex) {
		// Retorna a mensagem da exceção e o status definido na exceção
		// return new ResponseEntity<>(ex.getMessage(), ex.getStatus());
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ResponseEntity<String> handleMissingPart(MissingServletRequestPartException ex) {
		String errorMessage = "Funcionalidade com problemas, por favor contatar o suporte";
		return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Trata erros inesperados (genéricos)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		ex.printStackTrace();
		return new ResponseEntity<>("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.",
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
