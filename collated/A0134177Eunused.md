# A0134177Eunused
###### taskey\src\taskey\logic\HubertUnusedCodes.java
``` java
 * This file is used to store all my unused codes so that they do not clutter the active codebase.
 */
public class HubertUnusedCodes {
	/* Team decided to do away from updating tasks by name because done by index is sufficient and can handle
	 * duplicate task names.
	// Updates an named task's name on the current tab and saves the updated lists to disk.
	// TODO: support "set" from the "ACTION" tab. 
	public LogicFeedback updateByNameChangeName(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
										 ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		String oldTaskName = po.getTask().getTaskName();
		String newTaskName = po.getNewTaskName();
		String exceptionMsg;
		
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		
		if (getTaskByName(targetList, oldTaskName) == null) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, oldTaskName);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		updateAllLists(modifiedCopy, oldTaskName, newTaskName);
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}
	
	// Updates an named task's date on the current tab and saves the updated lists to disk.
	// TODO: support "set" from the "ACTION" tab. 
	public LogicFeedback updateByNameChangeDate(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			 							 ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		Task changedTask = po.getTask();
		String taskName = changedTask.getTaskName();
		String exceptionMsg;
		
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		
		if (getTaskByName(targetList, taskName) == null) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, taskName);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		updateAllLists(modifiedCopy, taskName, changedTask);
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}
	

	// Updates an named task's name and date on the current tab and saves the updated lists to disk.
	// TODO: support "set" from the "ACTION" tab. 
	public LogicFeedback updateByNameChangeBoth(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
			 							 ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		Task changedTask = po.getTask();
		String oldTaskName = changedTask.getTaskName();
		String newTaskName = po.getNewTaskName();
		String exceptionMsg;
		
		if (!(currentContent.equals(ContentBox.THIS_WEEK) || currentContent.equals(ContentBox.PENDING))) {
			exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}

		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		
		if (getTaskByName(targetList, oldTaskName) == null) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, oldTaskName);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		changedTask.setTaskName(newTaskName);
		updateAllLists(modifiedCopy, oldTaskName, changedTask);
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		
		return new LogicFeedback(modifiedCopy, po, null);
	}*/
	
	/* Team decided to do away from completing tasks by name because done by index is sufficient and can handle
	 * duplicate task names.
	// Marks an named task from the current tab as done and saves the updated lists to disk.
	// TODO: support "done" from the "ACTION" tab. 
	public LogicFeedback doneByName(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
    						 ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
		String taskName = po.getTask().getTaskName();
		String exceptionMsg;	
		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
		ArrayList<Task> tasksToComplete = getTasksByName(targetList, taskName);
		
		if (currentContent.equals(ContentBox.ACTION) && mostRecentActionCommand.equals("VIEW ARCHIVE")) { //Stub
			exceptionMsg = LogicConstants.MSG_EXCEPTION_DONE_INVALID;
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		}
		
		if (tasksToComplete.isEmpty()) {
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, taskName);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		} else if (tasksToComplete.size() > 1) { // Multiple tasks with the same name found, prompt user to choose which 
			                                     // one to complete
			ArrayList<ArrayList<Task>> lists = getEmptyLists();
			lists.set(ListID.ACTION.getIndex(), tasksToComplete);
			mostRecentActionList = tasksToComplete;
			exceptionMsg = LogicConstants.MSG_EXCEPTION_DUPLICATE_TASK_NAMES;
			return new LogicFeedback(lists, po, new Exception(exceptionMsg));
		} else { // Only one task with the given name
			Task toComplete = tasksToComplete.get(0);
			removeFromAllLists(modifiedCopy, toComplete);
			modifiedCopy.get(ListID.COMPLETED.getIndex()).add(toComplete);
			ArrayList<String> taskTags = toComplete.getTaskTags();
			
			if (taskTags != null) {
				for (String s : taskTags) {
					utd.removeTag(s);
				}
				
				if (!utd.saveTagDatabase()) {
					return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS)); 
				}
			}	
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
	}*/
	
	/* Team decided to do away from deleting tasks by name because delete by index is sufficient and can handle
	 * duplicate task names.
	// Removes an indexed task from the current tab and saves the updated lists to disk.
	public LogicFeedback deleteByName(ContentBox currentContent, ArrayList<ArrayList<Task>> originalCopy, 
            				   		  ArrayList<ArrayList<Task>> modifiedCopy, ProcessedObject po) {
    	
		ArrayList<Task> targetList = getListFromContentBox(modifiedCopy, currentContent);
    	String taskName = po.getTask().getTaskName();
		ArrayList<Task> tasksToDelete = getTasksByName(targetList, taskName);
		String exceptionMsg;

		if (tasksToDelete.isEmpty()) { // Name not found
			exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, taskName);
			return new LogicFeedback(originalCopy, po, new Exception(exceptionMsg));
		} else if (tasksToDelete.size() > 1) { // Multiple tasks with the same name found, prompt user to choose which 
			                                   // one to delete
			ArrayList<ArrayList<Task>> lists = getEmptyLists();
			lists.set(ListID.ACTION.getIndex(), tasksToDelete);
			mostRecentActionList = tasksToDelete;
			exceptionMsg = LogicConstants.MSG_EXCEPTION_DUPLICATE_TASK_NAMES;
			return new LogicFeedback(lists, po, new Exception(exceptionMsg));
		} else { // Only one task with the given name
			Task toDelete = tasksToDelete.get(0);
			removeFromAllLists(modifiedCopy, toDelete);
			ArrayList<String> taskTags = toDelete.getTaskTags();
			
			if (taskTags != null) {
				for (String s : taskTags) {
					utd.removeTag(s);
				}
				
				if (!utd.saveTagDatabase()) {
					return new LogicFeedback(originalCopy, po, new Exception(LogicConstants.MSG_EXCEPTION_SAVING_TAGS)); 
				}
			}	
		}
		
		try {
			saveAllTasks(modifiedCopy);
		} catch (Exception e) {
			return new LogicFeedback(originalCopy, po, e);
		}
		
		taskLists = cloneLists(modifiedCopy);
		return new LogicFeedback(modifiedCopy, po, null);
	}*/
	
	/* This is the code I wrote before my team decided to switch to ArrayLists instead of using HashMaps.
	 * There are many other instances where previous code I wrote was deleted, but I only chose to include this here
	 * because this particular code segment took me many days of effort to write and refactor.
	 * It also highlights the difference between the way Logic was implemented then and now.
	 * Notice that, in this old version, there was significant coupling between Logic and UI. 
	 * We have since removed the coupling.
	 * TODO: class description
	 * 
	 * @author Hubert Wong
	public class Logic {
		//List of status codes. Other components like Ui can use Logic.statusCode to access this list.
		public static final int SUCCESS_VIEW = 1;
		public static final int SUCCESS_ADD = 2;
		public static final int SUCCESS_DELETE = 3;
		public static final int SUCCESS_UPDATE = 4;
		public static final int SUCCESS_DONE = 5;
		public static final int SUCCESS_SEARCH = 6;
		public static final int SUCCESS_UNDO = 7;
		public static final int ERROR_VIEW = -1;
		public static final int ERROR_ADD = -2;
		public static final int ERROR_DELETE = -3;
		public static final int ERROR_UPDATE = -4;
		public static final int ERROR_DONE = -5;
		public static final int ERROR_SEARCH = -6;
		public static final int ERROR_UNDO = -7;
		
		private static Logic instance = null;
		private Parser parser;
		private Storage storage;
		private UiController uiController;
		
		//The most recent command which is not VIEW, UNDO, SEARCH or ERROR
		private String mostRecentUndoableCommand = null;
		
		private Task mostRecentTask = null;
		private Task mostRecentUpdatedTask = null; //To facilitate the reversal of updates
		
		//The current view type that Ui is displaying, e.g. deadline, events
		private String uiCurrentViewType = null;
		
		//Task lists retrieved from Storage at startup 
		private ArrayList<ArrayList<Task>> listsFromStorage = null;
		
		//Number of task lists in listsFromStorage
		private static final int NUM_TASK_LISTS = 6;
		
		//Indices of each Task list in listsFromStorage
		private static final int INDEX_PENDING_LIST = 0;
		private static final int INDEX_FLOATING_LIST = 1;
		private static final int INDEX_DEADLINE_LIST = 2;
		private static final int INDEX_EVENT_LIST = 3;
		private static final int INDEX_DONE_LIST = 4;
		private static final int INDEX_EXPIRED_LIST = 5;
		
		//Names of save file for each Task list. Can be moved to Storage later on.
		private static final String NAME_PENDING_SAVE_FILE = "pending tasks";
		private static final String NAME_FLOATING_SAVE_FILE = "floating tasks";
		private static final String NAME_DEADLINE_SAVE_FILE = "deadline tasks";
		private static final String NAME_EVENT_SAVE_FILE = "event tasks";
		private static final String NAME_DONE_SAVE_FILE = "done tasks";
		private static final String NAME_EXPIRED_SAVE_FILE = "expired tasks";
		
		//HashMaps containing Task data for each Task category. 
		//The key String holds the name of Task, and the value Task is the corresponding Task object.
		private HashMap<String, Task> pendingMap = null;
		private HashMap<String, Task> floatingMap = null;
		private HashMap<String, Task> deadlineMap = null;
		private HashMap<String, Task> eventMap = null;
		private HashMap<String, Task> doneMap = null;
		private HashMap<String, Task> expiredMap = null;
		
		//Collections of Task objects backed by the above HashMaps 
		private ArrayList<Task> pendingCollection = null; 
		private ArrayList<Task> floatingCollection = null;
		private ArrayList<Task> deadlineCollection = null;
		private ArrayList<Task> eventCollection = null;
		private ArrayList<Task> doneCollection = null;
		private ArrayList<Task> expiredCollection = null;
		
		public static void main(String[] args) throws IOException {
			Logic logicTest = Logic.getInstance();
			Task t = new Task("a new test task");
			logicTest.floatingMap = new HashMap<String, Task>();
			logicTest.pendingMap = new HashMap<String, Task>();
			logicTest.addFloatingToStorage(t, "a new test task");
			logicTest.getListsFromStorage();
		}
		
		/**
		 * Gets an instance of the Logic class if an instance does not already exist.
		 * 
		 * @return an instance of the Logic class
		 * 
		public static Logic getInstance() {
			if (instance == null) {
	    		instance = new Logic();
	    		instance.parser = new Parser();
	    		instance.storage = Storage.getInstance();
	    		instance.uiController = UiMain.getInstance().getController();
	    	}
	    	return instance;
	    }
		
		/**
		 * Get the list of pending tasks. Note that this list may not be sorted.
		 * 
		 * @return list of pending tasks\
		 * 
		public ArrayList<Task> getPendingTasks() {
			return pendingCollection;
		}
		
		/**
		 * Get the list of floating tasks. Note that this list may not be sorted.
		 * 
		 * @return list of floating tasks
		 * 
		public ArrayList<Task> getFloatingTasks() {
			return floatingCollection;
		}
		
		/**
		 * Get the list of deadline tasks. Note that this list may not be sorted.
		 * 
		 * @return list of deadline tasks
		 * 
		public ArrayList<Task> getDeadlineTasks() {
			return deadlineCollection;
		}
		
		/**
		 * Get the list of event tasks. Note that this list may not be sorted.
		 * 
		 * @return list of event tasks
		 * 
		public ArrayList<Task> getEventTasks() {
			return eventCollection;
		}
		
		/**
		 * Get the list of done tasks. Note that this list may not be sorted.
		 * 
		 * @return list of done tasks
		 *
		public ArrayList<Task> getDoneTasks() {
			return doneCollection;
		}
		
		/**
		 * Get the list of expired tasks. Note that this list may not be sorted.
		 * 
		 * @return list of expired tasks
		 *
		public ArrayList<Task> getExpiredTasks() {
			return expiredCollection;
		}
		
		/** 
		 * Updates Logic with the view type that Ui is currently in.
		 * 
		 * @param viewType
		 *
		public void updateViewType(String viewType) {
			uiCurrentViewType = viewType;
		}
		
		/**
		 * Initializes Ui with lists of each task category.
		 * 
		 * @return status code reflecting the outcome of command execution
		 *
		public int initializeUi() {
			int statusCode = getListsFromStorage();
			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
			uiController.updateDisplay(doneCollection, UiConstants.ContentBox.COMPLETED);
			uiController.updateDisplay(expiredCollection, UiConstants.ContentBox.EXPIRED);
			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
			uiCurrentViewType = "ALL";
			
			return statusCode;
		}
		
	    /**
	     * Attempts to execute a command specified by the input string.
	     * 
	     * @param input the input string
	     * @return      status code reflecting the outcome of command execution
	     * @throws IOException 
	     *
	    public int executeCommand(String input) throws IOException {
	    	int statusCode = 0; //Stub
	    	ProcessedObject po = parser.parseInput(input);
	    	String command = po.getCommand();
	    	Task task = po.getTask();
	    	int taskIndex = po.getIndex() - 1; //Only used for commands that specify the index of a task
	    	String viewType = po.getViewType(); //Only used for view commands
	    	String errorType = po.getErrorType(); //Only used for invalid commands
	    	String searchPhrase = po.getSearchPhrase(); //Only used for search commands
	    	String newTaskName = po.getNewTaskName(); //Only used for commands that change the name of a task
	    	String taskName = task.getTaskName();
	   	
	    	switch (command) {
	    		case "VIEW":
	    			statusCode = view(viewType);
	    			break;
	    			
	    		case "ADD_FLOATING":
	    			statusCode = addFloatingToStorage(task, taskName);
	    			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "ADD_DEADLINE":
	    			statusCode = addDeadlineToStorage(task, taskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "ADD_EVENT":
	    			statusCode = addEventToStorage(task, taskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "DELETE_BY_INDEX":
	    			statusCode = deleteIndexedTaskFromStorage(taskIndex);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    		
	    		case "DELETE_BY_NAME":
	    			statusCode = deleteNamedTaskFromStorage(taskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    		
	    		case "UPDATE_BY_INDEX_CHANGE_NAME":
	    			statusCode = updateIndexedTaskNameInStorage(taskIndex, newTaskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "UPDATE_BY_INDEX_CHANGE_DATE":
	    			statusCode = updateIndexedTaskDateInStorage(task, taskIndex);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    		
	    		case "UPDATE_BY_NAME_CHANGE_NAME":
	    			statusCode = updateNamedTaskNameInStorage(taskName, newTaskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "UPDATE_BY_NAME_CHANGE_DATE":
	    			statusCode = updateNamedTaskDateInStorage(task, taskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    				
	    		case "DONE_BY_INDEX":
	    			statusCode = markIndexedTaskAsDoneInStorage(taskIndex);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	       			uiController.updateDisplay(doneCollection, UiConstants.ContentBox.COMPLETED);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    		
	    		case "DONE_BY_NAME":
	    			statusCode = markNamedTaskAsDoneInStorage(taskName);
	       			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	       			uiController.updateDisplay(doneCollection, UiConstants.ContentBox.COMPLETED);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    			
	    		case "SEARCH":
	    			Task t = search(searchPhrase);
	    			if (t == null) { //Task not found
	    				statusCode = -1; //Stub
	    			} else {
	    				ArrayList<Task> matches = new ArrayList<Task>();
	    				matches.add(t);
	    				uiController.updateActionDisplay(matches, UiConstants.ActionContentMode.TASKLIST);
	    				statusCode = -1; //Stub
	    			}
	    			break;
	    		
	    		case "UNDO":
	    			statusCode = undo();
	    			uiController.updateDisplay(pendingCollection, UiConstants.ContentBox.PENDING);
	    			uiController.updateDisplay(doneCollection, UiConstants.ContentBox.COMPLETED);
	    			uiController.updateDisplay(expiredCollection, UiConstants.ContentBox.EXPIRED);
	    			uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
	    			break;
	    		
	    		case "ERROR": //TODO
	    			statusCode = -1; //Stub
	    			break;
	    			
	    		default:
	    	}
	    	
	    	if (isUndoableCommand(command)) {
	    		mostRecentUndoableCommand = command;
	    	}
	    	
	    	return statusCode; 
	    }
	    
	    //Updates Ui with a list of Tasks sorted by date, corresponding to the view type.
	    //Assumes that the collections are not null.
	    //Returns a status code representing outcome of action.
	    private int view(String viewType) throws IOException {
	    	int statusCode = -1; //Stub 
	    	
	    	if (viewType.equals("ALL")) {
				Collections.sort(pendingCollection);
				uiController.updateActionDisplay(pendingCollection, UiConstants.ActionContentMode.TASKLIST);
			} else if (viewType.equals("GENERAL")) {
				Collections.sort(floatingCollection);
				uiController.updateActionDisplay(floatingCollection, UiConstants.ActionContentMode.TASKLIST);
			} else if (viewType.equals("DEADLINES")) {
				Collections.sort(deadlineCollection);
				uiController.updateActionDisplay(deadlineCollection, UiConstants.ActionContentMode.TASKLIST);
			} else if (viewType.equals("EVENTS")) {
				Collections.sort(eventCollection);
				uiController.updateActionDisplay(eventCollection, UiConstants.ActionContentMode.TASKLIST);
			} else if (viewType.equals("DONE")) {
				Collections.sort(doneCollection);
				uiController.updateActionDisplay(doneCollection, UiConstants.ActionContentMode.TASKLIST);
			} else { //Expired tasks
				Collections.sort(expiredCollection);
				uiController.updateActionDisplay(expiredCollection, UiConstants.ActionContentMode.TASKLIST);
			}
	    	uiCurrentViewType = viewType;
	    	
	    	return statusCode; 
	    }
	    
	    //Add the floating Task to Storage. Returns a status code representing outcome of action.
	    private int addFloatingToStorage(Task task, String taskName) throws IOException {
			floatingMap.put(taskName, task); 
			floatingCollection = new ArrayList<Task>(floatingMap.values());
			storage.saveTaskList(floatingCollection, NAME_FLOATING_SAVE_FILE);	
			pendingMap.put(taskName, task);
			pendingCollection = new ArrayList<Task>(pendingMap.values());
			storage.saveTaskList(pendingCollection, NAME_PENDING_SAVE_FILE);
			mostRecentTask = task;
			
			return -1; //Stub
	    }
	    
	    //Add the deadline Task to Storage. Returns a status code representing outcome of action.
	    private int addDeadlineToStorage(Task task, String taskName) throws IOException {
			deadlineMap.put(taskName, task); 
			deadlineCollection = new ArrayList<Task>(deadlineMap.values());
			storage.saveTaskList(deadlineCollection, NAME_DEADLINE_SAVE_FILE);	
			pendingMap.put(taskName, task);
			pendingCollection = new ArrayList<Task>(pendingMap.values());
			storage.saveTaskList(pendingCollection, NAME_PENDING_SAVE_FILE);
			mostRecentTask = task;
			
			return -1; //Stub
	    }
	    
	  //Add the event Task to Storage. Returns a status code representing outcome of action.
	    private int addEventToStorage(Task task, String taskName) throws IOException {
			eventMap.put(taskName, task); 
			eventCollection = new ArrayList<Task>(eventMap.values());
			storage.saveTaskList(eventCollection, NAME_EVENT_SAVE_FILE);	
			pendingMap.put(taskName, task);
			pendingCollection = new ArrayList<Task>(pendingMap.values());
			storage.saveTaskList(pendingCollection, NAME_PENDING_SAVE_FILE);
			mostRecentTask = task;
			
			return -1; //Stub
	    }
	    
	    //Undo the most recent action that was not view, undo, search or error.
	    //Returns a status code representing outcome of action.
		private int undo() throws IOException {
			if (mostRecentUndoableCommand == null) { //No undoable commands since startup
				return -1; //Stub
			}

			String mostRecentTaskType = mostRecentTask.getTaskType();
			String mostRecentTaskName = mostRecentTask.getTaskName();
			
			switch (mostRecentUndoableCommand) {
				case "ADD_FLOATING":
				case "ADD_DEADLINE":
				case "ADD_EVENT":
					return removeTaskFromMaps(mostRecentTaskName, mostRecentTaskType);
				
				case "DELETE_BY_INDEX":
				case "DELETE_BY_NAME":
					return putTaskInMaps(mostRecentTask, mostRecentTaskName, mostRecentTaskType);
				
				case "UPDATE_BY_INDEX":
				case "UPDATE_BY_NAME":
					String mostRecentUpdatedTaskName = mostRecentUpdatedTask.getTaskName();
					String mostRecentUpdatedTaskType = mostRecentUpdatedTask.getTaskType();
					removeTaskFromMaps(mostRecentUpdatedTaskName, mostRecentUpdatedTaskType);
					return putTaskInMaps(mostRecentTask, mostRecentTaskName, mostRecentTaskType);
					
				case "DONE_BY_INDEX":
				case "DONE_BY_NAME":
					doneMap.remove(mostRecentTaskName);
					doneCollection = new ArrayList<Task>(doneMap.values());
					storage.saveTaskList(doneCollection, NAME_DONE_SAVE_FILE);		
					return putTaskInMaps(mostRecentTask, mostRecentTaskName, mostRecentTaskType);			
				
				default:
			}
			
			return -1; //Stub
		}
		
		//Deletes the Task specified by taskIndex from Storage.
		//Returns a status code reflecting outcome of command execution.
		private int deleteIndexedTaskFromStorage(int taskIndex) throws IOException {
			Task toDelete = getIndexedTask(taskIndex);
			
			if (toDelete == null) { //Index is invalid
				return -1; //Stub
			}
			
			String toDeleteType = toDelete.getTaskType();
			String toDeleteName = toDelete.getTaskName();
			mostRecentTask = toDelete;
			
			return removeTaskFromMaps(toDeleteName, toDeleteType);
		}
		
		//Deletes the Task specified by taskName from Storage.
		//Returns a status code reflecting outcome of command execution.
		private int deleteNamedTaskFromStorage(String taskName) throws IOException {
			if (pendingMap.containsKey(taskName)) {
				Task toDelete = pendingMap.get(taskName);
				String taskType = toDelete.getTaskType();
				mostRecentTask = toDelete;
				return removeTaskFromMaps(taskName, taskType);
			} else { //Task to delete does not exist
				return -1; //Stub
			}
		}
		
		//Updates the Task specified by taskIndex in Storage with newTaskName.
		//Returns a status code reflecting outcome of command execution.
		private int updateIndexedTaskNameInStorage(int taskIndex, String newTaskName) throws IOException {
			Task toUpdate = getIndexedTask(taskIndex);
			
			if (toUpdate == null) {
				return -1; //Stub
			}
			
			String toUpdateType = toUpdate.getTaskType();
			String toUpdateName = toUpdate.getTaskName();
			removeTaskFromMaps(toUpdateName, toUpdateType);
			mostRecentTask = toUpdate;
			Task updated = toUpdate.getDuplicate();
			updated.setTaskName(newTaskName);
			mostRecentUpdatedTask = updated;
			
			return putTaskInMaps(toUpdate, newTaskName, toUpdateType);
		}
		
		//Updates the Task specified by taskIndex in Storage with task which contains the new date.
		//Returns a status code reflecting outcome of command execution.
		private int updateIndexedTaskDateInStorage(Task task, int taskIndex) throws IOException {
			Task toUpdate = getIndexedTask(taskIndex);
			
			if (toUpdate == null) {
				return -1; //Stub
			}
			
			String toUpdateType = toUpdate.getTaskType();
			String toUpdateName = toUpdate.getTaskName();
			String newTaskType = task.getTaskType();
			removeTaskFromMaps(toUpdateName, toUpdateType);
			mostRecentTask = toUpdate;
			task.setTaskName(toUpdateName);
			mostRecentUpdatedTask = task;
			
			return putTaskInMaps(task, toUpdateName, newTaskType);
		}
		
		//Updates the Task specified by taskName in Storage with newTaskName.
		//Returns a status code reflecting outcome of command execution.
		private int updateNamedTaskNameInStorage(String oldTaskName, String newTaskName) throws IOException {
			if (pendingMap.containsKey(oldTaskName)) {
				Task toUpdate = pendingMap.get(oldTaskName);
				String toUpdateType = toUpdate.getTaskType();
				removeTaskFromMaps(oldTaskName, toUpdateType);
				mostRecentTask = toUpdate;
				Task updated = toUpdate.getDuplicate();
				updated.setTaskName(newTaskName);
				mostRecentUpdatedTask = updated;
				return putTaskInMaps(toUpdate, newTaskName, toUpdateType);
			} else { //Task to update does not exist
				return -1; //Stub
			}
		}
		
		//Updates the Task specified by taskName in Storage with task which contains the new date.
		//Returns a status code reflecting outcome of command execution.
		private int updateNamedTaskDateInStorage(Task task, String taskName) throws IOException {
			if (pendingMap.containsKey(taskName)) {
				Task toUpdate = pendingMap.get(taskName);
				String toUpdateType = toUpdate.getTaskType();
				String newTaskType = task.getTaskType();
				removeTaskFromMaps(taskName, toUpdateType);
				mostRecentTask = toUpdate;
				mostRecentUpdatedTask = task;
				return putTaskInMaps(task, taskName, newTaskType);
			} else { //Task to update does not exist
				return -1; //Stub
			}
		}
		
		//Marks the Task specified by taskIndex as done in Storage.
		//Returns a status code reflecting outcome of command execution.
		private int markIndexedTaskAsDoneInStorage(int taskIndex) throws IOException {
			Task toMark = getIndexedTask(taskIndex);
				
			if (toMark == null) { //Index is invalid
				return -1; //Stub
			}
				
			String toMarkType = toMark.getTaskType();
			String toMarkName = toMark.getTaskName();
			removeTaskFromMaps(toMarkName, toMarkType);
			mostRecentTask = toMark;
			doneMap.put(toMarkName, toMark);
			doneCollection = new ArrayList<Task>(doneMap.values());
			storage.saveTaskList(doneCollection, NAME_DONE_SAVE_FILE);
				
			return -1; //Stub
		}
		
		//Marks the Task specified by taskName as done in Storage.
		//Returns a status code reflecting outcome of command execution.
		private int markNamedTaskAsDoneInStorage(String taskName) throws IOException {
			if (pendingMap.containsKey(taskName)) {
				Task toMark= pendingMap.get(taskName);
				String toMarkName = toMark.getTaskName();
				String toMarkType = toMark.getTaskType();
				removeTaskFromMaps(taskName, toMarkType);
				mostRecentTask = toMark;
				doneMap.put(toMarkName, toMark);
				doneCollection = new ArrayList<Task>(doneMap.values());
				storage.saveTaskList(doneCollection, NAME_DONE_SAVE_FILE);
				return -1; //Stub
			} else { //Task name does not exist
				return -1; //Stub
			}
		}
		
		//Returns an existing Task whose name matches searchPhrase, or null if no matches are found.
		private Task search(String searchPhrase) {
			if (pendingMap.containsKey(searchPhrase)) { //Only works if searchPhrase matches taskName
				return pendingMap.get(searchPhrase);
			} else {
				return null;
			}
		}
		
		//Adds the specified <taskName, task> mappings to the relevant HashMaps determined by taskType.
		//This method also saves the updated Task collections to Storage.
		//Returns a status code reflecting outcome of command execution.
		private int putTaskInMaps(Task task, String taskName, String taskType) throws IOException {
			if (taskType.equals("FLOATING")) {
				floatingMap.put(taskName, task);
				floatingCollection = new ArrayList<Task>(floatingMap.values());
				storage.saveTaskList(floatingCollection, NAME_FLOATING_SAVE_FILE);
			} else if (taskType.equals("DEADLINE")) {
				deadlineMap.put(taskName, task);
				deadlineCollection = new ArrayList<Task>(deadlineMap.values());
				storage.saveTaskList(deadlineCollection, NAME_DEADLINE_SAVE_FILE);
			} else if (taskType.equals("EVENT")) {
				eventMap.put(taskName, task);
				eventCollection = new ArrayList<Task>(eventMap.values());
				storage.saveTaskList(eventCollection, NAME_EVENT_SAVE_FILE);
			}
			
			pendingMap.put(taskName, task);
			pendingCollection = new ArrayList<Task>(pendingMap.values());
			storage.saveTaskList(pendingCollection, NAME_PENDING_SAVE_FILE);
			
			return -1; //stub
		}
		
		//Removes the mappings whose keys are specified by taskName from the relevant HashMaps.
		//The HashMaps to remove the mappings from are determined by taskType.
		//This method also saves the updated Task collections to Storage.
		//Returns a status code reflecting outcome of command execution.
		private int removeTaskFromMaps(String taskName, String taskType) throws IOException {
			if (taskType.equals("FLOATING")) {
				floatingMap.remove(taskName);
				floatingCollection = new ArrayList<Task>(floatingMap.values());
				storage.saveTaskList(floatingCollection, NAME_FLOATING_SAVE_FILE);
			} else if (taskType.equals("DEADLINE")) {
				deadlineMap.remove(taskName);
				deadlineCollection = new ArrayList<Task>(deadlineMap.values());
				storage.saveTaskList(deadlineCollection, NAME_DEADLINE_SAVE_FILE);
			} else if (taskType.equals("EVENT")) {
				eventMap.remove(taskName);
				eventCollection = new ArrayList<Task>(eventMap.values());
				storage.saveTaskList(eventCollection, NAME_EVENT_SAVE_FILE);
			}
			
			pendingMap.remove(taskName);
			pendingCollection = new ArrayList<Task>(pendingMap.values());
			storage.saveTaskList(pendingCollection, NAME_PENDING_SAVE_FILE);
			
			return -1; //stub
		}
		
		//Returns a reference to the indexed Task based on the current Ui view type, or null if index is invalid.
		private Task getIndexedTask(int taskIndex) {
			if (outOfBounds(taskIndex)) {
				return null;
			}
			
			if (uiCurrentViewType.equals("ALL")) {
				return pendingCollection.get(taskIndex);
			} else if (uiCurrentViewType.equals("FLOATING")) {
				return floatingCollection.get(taskIndex);
			} else if (uiCurrentViewType.equals("DEADLINE")) {
				return deadlineCollection.get(taskIndex);
			} else if (uiCurrentViewType.equals("EVENT")) {
				return eventCollection.get(taskIndex);
			} else { //May need to add more cases like "EXPIRED", "DONE" etc.
				return null;
			}
		}
		
		//Determines if the given Task index is valid for the current Ui view type.
		//Returns true if the index is out of bounds (invalid).
		private boolean outOfBounds(int taskIndex) {
			if (uiCurrentViewType.equals("ALL")) {
				return (taskIndex < pendingCollection.size()) ? false : true;
			} else if (uiCurrentViewType.equals("FLOATING")) {
				return (taskIndex < floatingCollection.size()) ? false : true;
			} else if (uiCurrentViewType.equals("DEADLINE")) {
				return (taskIndex < deadlineCollection.size()) ? false : true;
			} else if (uiCurrentViewType.equals("EVENT")) {
				return (taskIndex < eventCollection.size()) ? false : true;
			} else { //May need to add more cases like "EXPIRED", "DONE" etc.
				return false;
			}
		}
		
		//Get Task lists from Storage at startup and populate the HashMaps and their corresponding collections.
	    //Returns a status code representing outcome of action.
	    private int getListsFromStorage() {
	    	listsFromStorage = new ArrayList<ArrayList<Task>>(NUM_TASK_LISTS);
	    	
	    	
	    	//Get PENDING list from Storage
	    	listsFromStorage.add(INDEX_PENDING_LIST, storage.getTaskList(NAME_PENDING_SAVE_FILE));
	    	pendingMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_PENDING_LIST)) {
	    		pendingMap.put(t.getTaskName(), t);
	    	}
	    	pendingCollection = new ArrayList<Task>(pendingMap.values());
	    	
	    	//Get FLOATING list from Storage
	    	listsFromStorage.add(INDEX_FLOATING_LIST, storage.getTaskList(NAME_FLOATING_SAVE_FILE));
	    	floatingMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_FLOATING_LIST)) {
	    		floatingMap.put(t.getTaskName(), t);
	    	}
	    	floatingCollection = new ArrayList<Task>(floatingMap.values());
	    	
	    	//Get DEADLINE list from Storage
	    	listsFromStorage.add(INDEX_DEADLINE_LIST, storage.getTaskList(NAME_DEADLINE_SAVE_FILE));
	    	deadlineMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_DEADLINE_LIST)) {
	    		deadlineMap.put(t.getTaskName(), t);
	    	}
	    	deadlineCollection = new ArrayList<Task>(deadlineMap.values());
	    	
	    	//Get EVENT list from Storage
	    	listsFromStorage.add(INDEX_EVENT_LIST, storage.getTaskList(NAME_EVENT_SAVE_FILE));
	    	eventMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_EVENT_LIST)) {
	    		eventMap.put(t.getTaskName(), t);
	    	}
	    	eventCollection = new ArrayList<Task>(eventMap.values());
	    	
	    	//Get DONE list from Storage
	    	listsFromStorage.add(INDEX_DONE_LIST, storage.getTaskList(NAME_DONE_SAVE_FILE));
	    	doneMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_DONE_LIST)) {
	    		doneMap.put(t.getTaskName(), t);
	    	}
	    	doneCollection = new ArrayList<Task>(doneMap.values());
	    	
	    	//Get EXPIRED list from Storage
	    	listsFromStorage.add(INDEX_EXPIRED_LIST, storage.getTaskList(NAME_EXPIRED_SAVE_FILE));
	    	expiredMap = new HashMap<String, Task>();
	    	for (Task t : listsFromStorage.get(INDEX_EXPIRED_LIST)) {
	    		expiredMap.put(t.getTaskName(), t);
	    	}
	    	expiredCollection = new ArrayList<Task>(expiredMap.values());
	    	
	    	return -1; //Stub
	    }
		
	    //Returns true if the supplied command can be undone.
	    private boolean isUndoableCommand(String command) {
	    	switch (command) {
	    		case "VIEW":
	    		case "SEARCH":
	    		case "UNDO":
	    		case "ERROR":
	    			return false;
	    		
	    		default:
	    	}
	    	
	    	return true;
	    }
	}*/
	
	/* I stopped using this because I felt that it was better for constants to be localized in the classes they were
	 * used in, for easier cross referencing, rather than placed in a central file which is tedious to refer to
	public class LogicConstants {
		
		public enum ListID {
			THIS_WEEK(0), PENDING(1), EXPIRED(2), GENERAL(3), DEADLINE(4), EVENT(5), COMPLETED(6), ACTION(7);
			
			private int index;
			
			private ListID(int index) {
				this.index = index;
			}
			
			public int getIndex() {
				return index;
			}
		}
		
		public enum CategoryID {
			GENERAL(0), DEADLINE(1), EVENT(2), COMPLETED(3);
			
			private int index;
			
			private CategoryID(int index) {
				this.index = index;
			}
			
			public int getIndex() {
				return index;
			}
		}
	}*/
	
	/* Previously used to compare LogicFeedback objects for testing purposes. Now not in use.
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LogicFeedback)) {
			System.out.println("Objects not of the same type");
		    return false;
		  }
		
		LogicFeedback other = (LogicFeedback) obj;
		
		if (this.taskLists.size() != other.taskLists.size()) {
			System.out.println("List sizes not equal");
			System.out.println(this.taskLists.size());
			System.out.println(other.taskLists.size());
			return false;
		}
		
		//Check if both objects' task lists are equal and in the same order
		for (int i = 0; i < this.taskLists.size(); i++) {
			if (!(this.taskLists.get(i).equals(other.taskLists.get(i)))) {
				System.out.println("Lists unequal at list index " + i);
				System.out.println(this.taskLists.get(i));
				System.out.println(other.taskLists.get(i));
				return false;
			}
		}
		
		if (!po.equals(other.po)) {
			System.out.println("Processed objects not equal");
			return false;
		}
		
		if (e == null && other.e != null || e != null 
			&& (other.e == null || !e.getMessage().equals(other.e.getMessage()))) {
			System.out.println("Exceptions not equal");
			return false;
		}
		
		return true;
	}*/

	/*
```
###### taskey\src\taskey\logic\HubertUnusedCodes.java
``` java
	 * Previously used to compare LogicFeedback objects for testing purposes. Now not in use.
	 *
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ProcessedObject)) {
		    return false;
		  }
		
		ProcessedObject other = (ProcessedObject) obj;
		
		if ((command == null && other.command != null) || (command != null 
			 && (other.command == null || !command.equals(other.command)))) {
			return false;
		}	
		
		if ((task == null && other.task != null) || (task != null 
			 && (other.task == null || !task.equals(other.task)))) {
			return false;
		}	
		
		if (index != other.index) {
			return false;
		}
		
		if ((viewType == null && other.viewType != null) || (viewType != null 
			 && (other.viewType == null || !viewType.equals(other.viewType)))) {
			return false;
		}	
		
		if ((errorType == null && other.errorType != null) || (errorType != null 
			 && (other.errorType == null || !errorType.equals(other.errorType)))) {
			return false;
		}	
		
		if ((searchPhrase == null && other.searchPhrase != null) || (searchPhrase != null 
			 && (other.searchPhrase == null || !searchPhrase.equals(other.searchPhrase)))) {
			return false;
		}	
		
		if ((newTaskName == null && other.newTaskName != null) || (newTaskName != null 
			 && (other.newTaskName == null || !newTaskName.equals(other.newTaskName)))) {
			return false;
		}	
		
		if ((newFileLoc == null && other.newFileLoc != null) || (newFileLoc != null 
			 && (other.newFileLoc == null || !newFileLoc.equals(other.newFileLoc)))) {
			return false;
		}	
		
		return true;
	}*/
}
```
###### taskey\src\taskey\messenger\UserTagDatabase.java
``` java
	public UserTagDatabase(Storage storage) {
		db = storage;
		//initialise the database of tags. 
		userTags = cloneTagList(db.loadTaglist());  
	}
	
	public void setTags(ArrayList<TagCategory> tagList) {
		assert(tagList != null);
		userTags = cloneTagList(tagList);
	}
	
	/**
	 * Removes all tags from the userTagDatabase
	 */
	public void deleteAllTags() {
		userTags.clear();
	}
	
	/**
```
###### taskey\src\taskey\messenger\UserTagDatabase.java
``` java
	public boolean removeTagCategory(String tag) {
		for (Iterator<TagCategory> it = userTags.iterator(); it.hasNext();) {
			TagCategory tc = it.next();
			
			if (tc.getTagName().equals(tag)) {
				it.remove();
				return true;
			}
		}
		
		return false;
	}

	/**
```
###### taskey\src\taskey\messenger\UserTagDatabase.java
``` java
	 * Save the tag hash map into a file for persistent storage. 
	 * @return true if save was successful; false otherwise
	 */
	/*public boolean saveTagDatabase() {
		try {
			db.saveTaglist(cloneTagList(userTags));
			return true;
		} catch (IOException e) {
			userTags = cloneTagList(db.getHistory().peekTags()); //To revert changes to userTags
			return false; 
		} 
	}*/
	
	public ArrayList<TagCategory> cloneTagList(ArrayList<TagCategory> tagList) {
		ArrayList<TagCategory> clone = new ArrayList<TagCategory>();
		for (TagCategory tc : tagList) {
			clone.add(new TagCategory(tc));
		}
		
		return clone;
	}
	
	/**
	 * Checks if the user tag database has a particular tag
	 * @param tag
	 * @return true if the tag exists in database
	 */
	public boolean containsTagName(String name) {
		for (TagCategory tc : userTags) {
			if (tc.getTagName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
   
    /*
```
