package br.com.gerenciadordegastos.exception;

public class CustomException extends RuntimeException {
//    private HttpStatus status;
//
//    public CustomException(String message, HttpStatus status) {
//        super(message);
//        this.status = status;
//    }
//
//    public HttpStatus getStatus() {
//        return status;
//    }
	  public CustomException(String message) {
	  super(message);
	}
}
