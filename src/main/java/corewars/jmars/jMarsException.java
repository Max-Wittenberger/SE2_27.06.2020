package corewars.jmars;

public class jMarsException extends Exception{
	public jMarsException( ) {
		super();
	}
	
	public jMarsException(String message) {
		super(message);
	}
	
	public jMarsException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public void printMessage() {
	System.out.println(super.getMessage());
	}
}
