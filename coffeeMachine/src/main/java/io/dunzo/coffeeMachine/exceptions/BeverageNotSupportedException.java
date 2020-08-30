package io.dunzo.coffeeMachine.exceptions;

/**
 * @author swatigojra
 *
 */
public class BeverageNotSupportedException extends Exception
{
	public BeverageNotSupportedException(String message){
		super(message);
	}
}
