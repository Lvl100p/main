package taskey.parser;

import java.util.ArrayList;
import taskey.logic.Task;
import taskey.storage.Storage;

public class Parser {
	
	public enum specialDays {
		tomorrow,today,next_week, tonight, this_weekend 
	}
	 
	
	public Parser() {
		//TODO 
	}
	
	/**
	 * Process the user's command and execute it accordingly 
	 * @param command: string of command keyed in by user
	 * @return message to be displayed
	 */
	public String parseInput(String stringInput) {
		String message=""; 
		String command = getCommand(stringInput); 
		String task = getTaskName(command, stringInput);
		ArrayList<String> dateRange = getDate(stringInput); 
		
		
		message = executeCommand(command, task);
		
		return message;  
	}
	
	//TODO: Double check if this will be handled by Logic or Parser. 
	public String executeCommand(String command, String task) {
		
		switch(command) {
			case "add": 
				//add task to storage!
				break;
			case "del":
				break;
			case "edit":
				break;
			case "view":
				break;
			case "sort":
				break;
			case "search":
				break; 
			
			default:
				//return an error message. 		
		
		}
		
		return ""; 
	}
	
	private String getCommand(String stringInput) {
		String[] splitString = stringInput.split(" ");

		return splitString[0].toLowerCase(); 
	}
	
	
	/**
	 * FOR FLOATING TASK: 
	 * Given a stringInput, remove the command from the string
	 * @param command
	 * @param stringInput
	 * @return taskName without command
	 */
	public String getTaskName(String command, String stringInput) {
		String task = stringInput.replaceFirst(command, "");
		
		return task.trim(); 
	}
	
	
	/**
	 * FOR TASK WITH DATES:
	 * Given a stringInput, remove the command and date from the string
	 * @param command
	 * @param date
	 * @param stringInput
	 * @return taskName without command and date 
	 */
	public String getTaskName(String command, String date, String stringInput) {
		String task = stringInput.replaceFirst(command, "");
		task = task.replaceFirst(date, ""); 
		//TODO: work on the logic: may not be entirely correct. 
		return task.trim(); 
	}
	
	//TODO: figure out when to remove keywords from commands 
	private String joinStringWithoutKeywords(ArrayList<String> dates) {
		 
		return ""; 
	}
	
	//TODO: Dummy for now  
	public ArrayList<String> getDate(String stringInput) {
			
		return new ArrayList<String>(); 
	}
	
	
	public static void main(String[] args) {
		//String myString = getTaskName("add","add taskey to github");
		//System.out.println(myString);
	}
}
