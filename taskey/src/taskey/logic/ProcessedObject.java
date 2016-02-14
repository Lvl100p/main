package taskey.logic;

/**
 * This class will be used for facilitating transfer of a processed task from 
 * Parser to Logic. 
 * 
 * command types:
 * 1. ADD_FLOATING
 * 2. ADD_DEADLINE
 * 3. ADD_EVENT
 * 4. ADD_RECURRING
 * 5. DELETE_BY_INDEX
 * 6. DELETE_BY_NAME 
 * 7. UPDATE_BY_INDEX
 * 8. UPDATE_BY_NAME 
 * 9. VIEW 
 * 
 * @author Xue Hui
 *
 */
public class ProcessedObject {
	private String command;
	private Task task = null; 
	private int index = -1; 
	//index is only used if command type is DELETE_BY_INDEX or UPDATE_BY_INDEX
	
	//CONSTRUCTORS ====================================================
	/**
	 * Constructor for VIEW 
	 * @param command
	 */
	public ProcessedObject(String command) {
		this.command = command; 
	}
	
	/**
	 * Constructor for ADD_FLOATING, ADD_DEADLINE, ADD_EVENT, ADD_RECURRING,
	 * DELETE_BY_NAME, UPDATE_BY_NAME
	 * @param command
	 * @param task
	 */
	public ProcessedObject(String command, Task task) {
		this.command = command;
		this.task = task; 
	}
	
	/**
	 * Constructor for DELETE_BY_INDEX, UPDATE_BY_INDEX 
	 * @param command
	 * @param task
	 * @param index
	 */
	public ProcessedObject(String command, Task task, int index) {
		this.command = command;
		this.task = task; 
		this.index = index; 
	}
	
	//=================================================================
	
	//Corresponding GET/SET methods ===================================
	public String getCommand() {
		return command;
	}
	
	public void setCommand(String command) {
		this.command = command; 
	}
	
	public Task getTask() {
		return task;
	}
	
	public void setTask(Task task) {
		this.task = task; 
	}
	
	public int getIndex() {
		return index; 
	}
	
	public void setIndex(int index) {
		this.index = index; 
	}

}
