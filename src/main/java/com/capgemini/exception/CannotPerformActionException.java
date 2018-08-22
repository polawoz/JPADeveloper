package com.capgemini.exception;

public class CannotPerformActionException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;


	public CannotPerformActionException(){
		super();
	}
	
	
	public CannotPerformActionException(String message){
		
		super(message);
		
		
		
	}

}
