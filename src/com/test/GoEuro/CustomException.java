package com.test.GoEuro;

/**
 * A custom exception calls which can be extended to throw specific exceptions
 */
public abstract class CustomException extends Exception{
	public CustomException(String mess){
		super(mess);
	}
}
