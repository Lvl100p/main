package taskey.parser;

import java.util.ArrayList;
import java.util.HashMap;

import taskey.constants.ParserConstants;
import taskey.logic.ProcessedAC;
import taskey.logic.TagCategory;

/**
 * @@author A0107345L
 * This class processes what words should be shown in the dropdown
 * menu if the user types into the CLI - it allows the user to auto-complete
 * his commands. 
 * @author Xue Hui
 *
 */
public class AutoComplete {
	private HashMap<String,String> commandList = new HashMap<String,String>();
	private ArrayList<String> specialDays = new ArrayList<String>(); 
	private ArrayList<String> commands = new ArrayList<String>();
	private ArrayList<String> viewList = new ArrayList<String>();
	
	public AutoComplete() {
		commands.add("add");
		commands.add("view");
		commands.add("del");
		commands.add("set");
		commands.add("search");
		commands.add("done");
		commands.add("undo");
		commands.add("file_loc");
		
		commandList.put("add","add");
		commandList.put("view","view");
		commandList.put("del", "del");
		commandList.put("set","set");
		commandList.put("search","search");
		commandList.put("done","done");
		commandList.put("undo","undo");
		commandList.put("file_loc","file_loc");
		
		viewList.add("all");
		viewList.add("general");
		viewList.add("deadlines");
		viewList.add("events");
		viewList.add("archive");
		viewList.add("help");
		
		specialDays.add("sun");
		specialDays.add("mon");
		specialDays.add("tue");
		specialDays.add("wed");
		specialDays.add("thu");
		specialDays.add("fri");
		specialDays.add("sat");
		
	}
	
	public ProcessedAC getSuggestions(String rawPhrase, UserTagDatabase utd) {
		ProcessedAC suggestions = null;
		String phrase = rawPhrase.toLowerCase().trim();
		String[] split = phrase.split(" ");
		
		if (split.length == 1) {
			//likely only command 
			suggestions = completeCommand(phrase); 
			return suggestions; 
		}
		
		//look for the correct word to auto-suggest and return 
		switch (split[0].trim()) {
			case "view": 
				suggestions = completeView(phrase, utd); 
				break;
			
			case "add": 
				completeAdd(phrase);
				break;
				
			case "set":
				completeEdit(phrase);
				break;
				
			default:
				suggestions = new ProcessedAC(ParserConstants.NO_SUCH_COMMAND);
				break; 
		}
		return suggestions; 
	}
	
	/**
	 * Given a partial input of the command, return a list of commands 
	 * that contain that input as a sub-string. 
	 * @param phrase
	 * @return If no such command exists, return null
	 */
	public ProcessedAC completeCommand(String phrase) {
		phrase = phrase.toLowerCase().trim(); 
		
		//if the command is completed, don't need to process
		if (commandList.containsKey("phrase")) {
			return new ProcessedAC(ParserConstants.FINISHED_COMMAND);
		}
		
		ArrayList<String> availCommands = new ArrayList<String>();
		
		//if only one letter, don't search the wrong part of the command
		if (phrase.length() == 1) {
			for(int i = 0; i < commands.size(); i++) {
				String tempCommand = commands.get(i); 
				int temp = tempCommand.indexOf(phrase);
				if (temp == 0) { //first pos 
					availCommands.add(tempCommand); 		
				}
			}
			if (!availCommands.isEmpty()) {
				return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availCommands);
			}
			return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND); 
		}
		
		//find list normally 
		for(int i = 0; i < commands.size(); i++) {
			if (commands.get(i).contains(phrase)) {
				availCommands.add(commands.get(i)); 
			}
		}
		
		if (!availCommands.isEmpty()) {
			return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availCommands);
		} 
		//no such command containing that sub-string
		return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND); 
	}
	
	/**
	 * Given a partial input that contains "view xxxx xxx",
	 * return a list of available view types that the user can access in his 
	 * latest word 
	 * @param phrase
	 * @return If no such list of views is available, return null 
	 */
	public ProcessedAC completeView(String phrase, UserTagDatabase utd) {
		ArrayList<TagCategory> tagDB = utd.getTagList(); 
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("view", ""); 
		String[] parts = phrase.split(" ");
		//only want to autocomplete the latest word
		String word = parts[parts.length-1].trim(); 
		
		//check if basic view exists 
		for(int i = 0; i < viewList.size(); i++) {
			if (viewList.get(i).contains(word)) {
				availViews.add(viewList.get(i)); 
			}
		}
		
		//check if tag view exists 
		for(int i = 0; i < tagDB.size(); i++) {
			String tag = tagDB.get(i).getTagName(); 
			if (tag.contains(phrase)) {
				availViews.add(tag); 
			}
		}
		
		if (!availViews.isEmpty()) {
			return new ProcessedAC(ParserConstants.DISPLAY_COMMAND, availViews); 
		}
		return new ProcessedAC(ParserConstants.NO_SUCH_COMMAND); 
	}
	
	//TODO
	public void completeAdd(String phrase) {
		//help user to fill in date if any 
	}
	
	//TODO
	public void completeEdit(String phrase) {
		//help user to fill in !!! or date 
	}
	
	/* @@author A0107345L-unused
	 * Decided not to use the code below as we decided to change 
	 * what the AutoComplete should display 
	 */
	
	/**
	 * Given a partial input that contains "del xxxx",
	 * display a list of tasks that the user can delete 
	 * (he can delete by task name or number)
	 * @param phrase
	 * @return If no such list of tasks is available, return null 
	 */
	public ArrayList<String> completeDelete(String phrase, ArrayList<String> tasks) {
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("del", ""); 
		
		for(int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).contains(phrase)) {
				availViews.add(tasks.get(i)); 
			}
		}
		
		if (!availViews.isEmpty()) {
			return availViews; 
		}
		return null; 
	}
	
	/**
	 * Given a partial input that contains "done xxxx",
	 * display a list of tasks that the user can set as done 
	 * (he can set done by task name or number)
	 * @param phrase
	 * @return If no such list of tasks is available, return null 
	 */
	public ArrayList<String> completeDone(String phrase, ArrayList<String> tasks) {
		ArrayList<String> availViews = new ArrayList<String>();
		phrase = phrase.toLowerCase();
		phrase = phrase.replaceFirst("done", ""); 
		
		for(int i = 0; i < tasks.size(); i++) {
			if (tasks.get(i).contains(phrase)) {
				availViews.add(tasks.get(i)); 
			}
		}
		
		if (!availViews.isEmpty()) {
			return availViews; 
		}
		return null; 
	}
}
