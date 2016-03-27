package taskey.parser;

import taskey.messenger.ProcessedObject;

/**
 * @@author A0107345L
 * Purpose is to parse the "undo" command 
 * @author Xue Hui
 *
 */
public class ParseUndo extends ParseCommand {
	
	public ParseUndo() {
		super(); 
	}

	/**
	 * Return ProcessedObject for Undo 
	 * @param command
	 * @return
	 */
	public ProcessedObject processUndo(String command) {
		return new ProcessedObject(command.toUpperCase()); 
	}
}
