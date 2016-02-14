package taskey.logic;

import taskey.parser.TimeConverter; 

/**
 * Task object holds all the details of the task 
 * 
 * taskType can have the following values:
 * 1. FLOATING
 * 2. EVENT (has a start and end time)
 * 3. DEADLINE 
 * 
 * isRecurring: set to true if event/deadline is recurring, 
 *              and set the recurring interval as well! 
 *         
 * 
 * date arrays have the following format:
 * dates*[0]: recurring interval (if recurring event/deadline) 
 * dates*[1]: start Time (events)
 * dates*[2]: end Time (events) 
 * dates*[3]: deadline 
 * idea for recurring : store the diff in time to the next recurrence in seconds
 * TODO: decide how to store the diff in time of a recurring event in human time. 
 * 
 * @author Xue Hui 
 *
 */

public class Task implements Comparable<Task> {
	public static final int NONE = -1; 
	public static final String EMPTY = ""; 
	
	private String taskName;
	private String taskDetails;
	private String taskType; 
	private long[] datesEpoch = {NONE,NONE,NONE,NONE}; 
	private String[] datesHuman = {EMPTY,EMPTY,EMPTY,EMPTY};
	private boolean isRecurring = false; 
	
	private TimeConverter timeConverter = new TimeConverter(); 
	
	//CONSTRUCTORS ==============================================
	public Task() {
		taskName = "";
		taskDetails = "";
		taskType = ""; 
	}
	
	public Task(String taskName) {
		this.taskName = taskName;
		taskDetails = "";	
		taskType = "";
	}
	
	public Task(String taskName, String taskDetails) {
		this.taskName = taskName;
		this.taskDetails = taskDetails;
		taskType = ""; 
	}
	
	
	//BASIC GET/SET METHODS =====================================
	public String getTaskName() {
		return taskName;
	}
	
	public void setTaskName(String taskName) {
		this.taskName = taskName; 
	}
	
	public String getTaskDetails() {
		return taskDetails; 
	}
	
	public void setTaskDetails(String taskDetails) {
		this.taskDetails = taskDetails; 
	}
	
	public String getTaskType() {
		return taskType; 
	}
	
	public void setTaskType(String taskType) {
		this.taskType = taskType; 
	}
	
	public String getStartDate() {
		return datesHuman[1]; 
	}
	
	public long getStartDateEpoch() {
		return datesEpoch[1]; 
	}
	
	public String getEndDate() {
		return datesHuman[2]; 
	}
	
	public long getEndDateEpoch() {
		return datesEpoch[2]; 
	}
	
	public String getDeadline() {
		return datesHuman[3]; 
	}
	
	public long getDeadlineEpoch() {
		return datesEpoch[3]; 
	}
	
	/**
	 * Given the startDate in the format dd MMM yyyy HH:mm:ss, 
	 * auto-key in the epoch time as well. 
	 * @param startDate
	 */
	public void setStartDate(String startDate) {
		datesHuman[1] = startDate; 
		datesEpoch[1] = timeConverter.toEpochTime(startDate); 	
	}
	
	/**
	 * Given the startDate in Epoch, 
	 * auto-key in the human time as well. 
	 * @param startDate
	 */
	public void setStartDate(long startDate) {
		datesEpoch[1] = startDate; 
		datesHuman[1] = timeConverter.toHumanTime(startDate); 
	}
	
	/**
	 * Given the endDate in the format dd MMM yyyy HH:mm:ss, 
	 * auto-key in the epoch time as well. 
	 * @param endDate
	 */
	public void setEndDate(String endDate) {
		datesHuman[2] = endDate; 
		datesEpoch[2] = timeConverter.toEpochTime(endDate); 	
	}
	
	/**
	 * Given the endDate in Epoch, 
	 * auto-key in the human time as well. 
	 * @param endDate
	 */
	public void setEndDate(long endDate) {
		datesEpoch[2] = endDate; 
		datesHuman[2] = timeConverter.toHumanTime(endDate); 		
	}
	
	/**
	 * Given the deadline in the format dd MMM yyyy HH:mm:ss, 
	 * auto-key in the epoch time as well. 
	 * @param deadline
	 */
	public void setDeadline(String  deadline) {
		datesHuman[3] = deadline; 
		datesEpoch[3] = timeConverter.toEpochTime(deadline); 	
	}
	
	/**
	 * Given the deadline in Epoch,
	 * auto-key in the human time as well. 
	 * @param deadline
	 */
	public void setDeadline(long deadline) {
		datesEpoch[3] = deadline; 
		datesHuman[3] = timeConverter.toHumanTime(deadline); 	
		
	}
	
	public boolean getIsRecurring() {
		return isRecurring; 
	}
	
	/*
	public void setIsRecurring(boolean isRecurring) {
		this.isRecurring = isRecurring; 
	}
	*/ 
	
	//NON-BASIC METHODS ==========================================
	
	
	public String[] getEventTime() {
		String[] eventTime = {datesHuman[1],datesHuman[2]};
		return eventTime; 
	}
	
	public long[] getEventTimeEpoch() {
		long[] eventTime = {datesEpoch[1],datesEpoch[2]}; 
		return eventTime; 
	}
	
	public String getRecurringTime() {
		return datesHuman[0]; 
	}
	
	public long getRecurringTimeEpoch() {
		return datesEpoch[0]; 
	}
	
	public void setRecurringTime(long interval) {
		datesEpoch[0] = interval;
		isRecurring = true; 
		//TODO: settle the human recurring time... 
	}
	
	public void removeRecurring() {
		datesEpoch[0] = NONE;
		datesHuman[0] = EMPTY;
		isRecurring = false; 
	}
	
	@Override
	/**
	 * tasks are comparable by their time. 
	 * used for sorting in ArrayList<Task>
	 * So that one can easily just call Collections.sort(taskList) 
	 */
	public int compareTo(Task anotherTask) {
		long startTime = datesEpoch[1]; 
		long otherStartTime = anotherTask.getStartDateEpoch(); 
		
		if (startTime > otherStartTime) {
			return 1; 
		} else if (startTime == otherStartTime) {
			return 0;
		} else {
			return -1;
		} 
	}
	
	/**
	 * tasks are the same if they have the same name
	 * used for UPDATE_BY_NAME and DELETE_BY_NAME
	 * @param anotherTask
	 * @return 0 if they have the same name, else return -1 
	 */
	public int equals(Task anotherTask) {
		if (taskName.compareTo(anotherTask.getTaskName()) == 0) {
			return 0; 
		}	
		return -1; 
	}

}
