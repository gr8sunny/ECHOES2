package pedagogicComponent.data;

/**
 * Actions are recognised by String labels this exception is
 * thrown by ActionNameToWorldAction when the label does not match.
 * 
 * @author Katerina Avramides
 * @version 1.1. (April 2010)
 * 
 */
@SuppressWarnings("serial")
public class StringLabelException extends Exception {

	/**
	 * Create a new exception with the specified message.
	 * 
	 * @message the message
	 * 
	 **/
	public StringLabelException(String message) {
		super(message);
	}
}
