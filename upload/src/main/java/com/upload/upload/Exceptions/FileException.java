package com.upload.upload.Exceptions;


public class FileException extends RuntimeException {


    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileException(String message) {
        super(message);
    }
	

    public FileException() {
		super();
		// TODO Auto-generated constructor stub
	}


	public FileException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}


	public FileException(String message, Throwable cause) {
        super(message, cause);
    }
}