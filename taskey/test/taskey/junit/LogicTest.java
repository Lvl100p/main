package taskey.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import taskey.constants.UiConstants.ContentBox;
import taskey.logic.Logic;
import taskey.logic.LogicConstants;
import taskey.logic.LogicConstants.ListID;
import taskey.logic.LogicFeedback;
import taskey.logic.ProcessedObject;
import taskey.logic.TagCategory;
import taskey.logic.Task;
import taskey.parser.Parser;
import taskey.parser.TimeConverter;

/**
 * @author Hubert
 */
public class LogicTest {
	public static final int NUM_SECONDS_1_DAY = 86400;
	public static final int NUM_SECONDS_1_WEEK = 604800;
	public static final int NUM_SECONDS_BUFFER_TIME = 100;
	public static final String STRING_ADD_FLOATING = "add g2 a?b ,  ";
	public static final String STRING_ADD_DEADLINE = "add g2 a?b ,   on %1$s";
	public static final String STRING_ADD_EVENT = "add g2 a?b ,   from %1$s to %2$s";
	public static final String STRING_DELETE_BY_INDEX = "del 1";
	public static final String STRING_DELETE_BY_INVALID_INDEX = "del 2";
	public static final String STRING_DELETE_BY_NAME = "del g2 a?b ,  ";
	public static final String STRING_DELETE_BY_INVALID_NAME = "del ayy lmao";
	public static final String STRING_SEARCH = "search ?B , ";
	public static final String STRING_SEARCH_NOT_FOUND = "search ayy lmao";
	public static final String STRING_SEARCH_EMPTY = "search ";
	public static final String STRING_DONE_BY_INDEX = "done 1";
	public static final String STRING_DONE_BY_INVALID_INDEX = "done 2";
	public static final String STRING_DONE_BY_NAME = "done g2 a?b ,  ";
	public static final String STRING_DONE_BY_INVALID_NAME = "del ayy lmao";
	public static final String STRING_UPDATE_BY_INDEX_CHANGE_NAME = "set 1 \"ayy lmao\"";
	public static final String STRING_UPDATE_BY_INDEX_CHANGE_DATE_DEADLINE = "set 1 [%1$s]";
	public static final String STRING_UPDATE_BY_INDEX_CHANGE_DATE_EVENT = "set 1 [%1$s, %2$s]";
	public static final String STRING_UPDATE_BY_INDEX_CHANGE_DATE_FLOATING = "set 1 [none]";
	public static final String STRING_UPDATE_BY_INDEX_CHANGE_BOTH = "set 1 \"ayy lmao\" [none]";
	public static final String STRING_UPDATE_BY_INVALID_INDEX = "set 2 \"ayy lmao\"";
	public static final String STRING_UPDATE_BY_NAME_CHANGE_NAME = "set g2 a?b ,   \"ayy lmao\"";
	public static final String STRING_UPDATE_BY_NAME_CHANGE_DATE_DEADLINE = "set add g2 a?b ,   [%1$s]";
	public static final String STRING_UPDATE_BY_NAME_CHANGE_DATE_EVENT = "set add g2 a?b ,   [%1$s, %2$s]";
	public static final String STRING_UPDATE_BY_NAME_CHANGE_DATE_FLOATING = "set add g2 a?b ,   [none]";
	public static final String STRING_UPDATE_BY_NAME_CHANGE_BOTH = "set g2 a?b ,   \"ayy lmao\" [none]";
	public static final String STRING_UPDATE_BY_INVALID_NAME = "set g3 a?b ,  \"ayy lmao\"";
	public static final String STRING_UNDO = "undo";
	
	private Logic logic;
	private ArrayList<ArrayList<Task>> originalCopy;
	private ArrayList<ArrayList<Task>> modifiedCopy;
	private Parser parser;
	private TimeConverter timeConverter;
	
	public static ArrayList<ArrayList<Task>> getEmptyLists() {
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		
		return temp;
	}
	
	public static ArrayList<ArrayList<Task>> addTaskToLists(Task task) {
		TimeConverter timeConverter = new TimeConverter();
		ArrayList<ArrayList<Task>> temp = new ArrayList<ArrayList<Task>>();
		
		while (temp.size() < 7) {
			temp.add(new ArrayList<Task>());
		}
		
		String taskType = task.getTaskType();
		long currTime = timeConverter.getCurrTime();
		
		if (taskType.equals("FLOATING")) {
			temp.get(ListID.GENERAL.getIndex()).add(task);
		} else if (taskType.equals("DEADLINE")) {
			long deadline = task.getDeadlineEpoch();
			
			if (deadline < currTime) {
				return temp;
			}
			
			temp.get(ListID.DEADLINE.getIndex()).add(task);
			
			if (timeConverter.isSameWeek(deadline, currTime)) {
				temp.get(ListID.THIS_WEEK.getIndex()).add(task);
			}
		} else if (taskType.equals("EVENT")) {
			long endDate = task.getEndDateEpoch();
			
			if (endDate < currTime) {
				return temp;
			}
			
			temp.get(ListID.EVENT.getIndex()).add(task);
			
			if (timeConverter.isSameWeek(task.getStartDateEpoch(), currTime)) {
				temp.get(ListID.THIS_WEEK.getIndex()).add(task);
			}
		}
		
		temp.get(ListID.PENDING.getIndex()).add(task);
		
		return temp;
	}
	
	// Make sure clear command works because it is used in setUp().
	// clear command is supposed to clear all task and tag data in storage.
	@BeforeClass
	public static void testClear() {
		Logic logic = new Logic();
		ArrayList<ArrayList<Task>> originalCopy = logic.getAllTaskLists();
		ArrayList<ArrayList<Task>> modifiedCopy = logic.getAllTaskLists();
		LogicFeedback actual = logic.clear(originalCopy, modifiedCopy);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		LogicFeedback expected = new LogicFeedback(temp, new ProcessedObject("CLEAR"), null);
		assertEquals(expected, actual);
		assertTrue(logic.getTagList().isEmpty());
	}
	
	@Before
	public void setUp() {
		logic = new Logic();
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		parser = new Parser();
		timeConverter = new TimeConverter();
		logic.clear(originalCopy, modifiedCopy);
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
	}
	
	@Test
	public void testAddFloating() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		LogicFeedback actual = logic.addFloating(originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(po.getTask());
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	 
	@Test
	public void testAddDeadlineForThisWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addDeadline(originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(po.getTask());
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddDeadlineOutsideThisWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addDeadline(originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(po.getTask());
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddEventForThisWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_EVENT, timeConverter.getDate(currTime),
				                     timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addEvent(originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(po.getTask());
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddEventOutsideThisWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_EVENT, timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK),
				                     timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK + NUM_SECONDS_1_DAY));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addEvent(originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(po.getTask());
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddExpiredDeadlineTask() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime - NUM_SECONDS_1_DAY));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addDeadline(originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, task.getDeadline());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddExpiredEventTask() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_EVENT, timeConverter.getDate(currTime - NUM_SECONDS_1_WEEK),
				                     timeConverter.getDate(currTime - NUM_SECONDS_1_DAY));
		ProcessedObject po = parser.parseInput(input);
		LogicFeedback actual = logic.addEvent(originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, task.getEndDate());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAddDuplicateTaskName() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		LogicFeedback actual = logic.addFloating(originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DUPLICATE_TASKS, task.getTaskName());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDeleteTaskByIndex() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_INDEX);
		LogicFeedback actual = logic.deleteByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDeleteTaskByName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_NAME);
		LogicFeedback actual = logic.deleteByName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDeleteFromWrongTab() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_NAME);
		LogicFeedback actual = logic.deleteByName(ContentBox.ACTION, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(LogicConstants.MSG_EXCEPTION_DELETE_INVALID_TAB));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDeleteTaskByInvalidIndex() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_INVALID_INDEX);
		LogicFeedback actual = logic.deleteByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, po.getIndex() + 1);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDeleteTaskByInvalidName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_INVALID_NAME);
		LogicFeedback actual = logic.deleteByName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, po.getTask().getTaskName());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSearchPhraseFound() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		Task task = po.getTask();
		logic.addFloating(originalCopy, modifiedCopy, po);

		originalCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_SEARCH);
		LogicFeedback actual = logic.search(originalCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		temp.get(0).add(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSearchPhraseNotFound() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		logic.addFloating(originalCopy, modifiedCopy, po);

		originalCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_SEARCH_NOT_FOUND);
		LogicFeedback actual = logic.search(originalCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testSearchPhraseEmpty() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		logic.addFloating(originalCopy, modifiedCopy, po);

		originalCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_SEARCH_EMPTY);
		LogicFeedback actual = logic.search(originalCopy, po);
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_SEARCH_PHRASE_EMPTY;
		LogicFeedback expected = new LogicFeedback(modifiedCopy, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDoneTaskByIndex() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_INDEX);
		LogicFeedback actual = logic.doneByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		temp.get(ListID.COMPLETED.getIndex()).add(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDoneTaskFromWrongTab() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_INDEX);
		LogicFeedback actual = logic.doneByIndex(ContentBox.EXPIRED, originalCopy, modifiedCopy, po);
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_DONE_INVALID_TAB;
		LogicFeedback expected = new LogicFeedback(modifiedCopy, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDoneTaskByInvalidIndex() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_INVALID_INDEX);
		LogicFeedback actual = logic.doneByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, po.getIndex() + 1);
		LogicFeedback expected = new LogicFeedback(modifiedCopy, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDoneTaskByName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_NAME);
		LogicFeedback actual = logic.doneByName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		temp.get(ListID.COMPLETED.getIndex()).add(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDoneTaskByInvalidName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_INVALID_NAME);
		LogicFeedback actual = logic.doneByName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND;
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByIndexChangeName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INDEX_CHANGE_NAME);
		LogicFeedback actual = logic.updateByIndexChangeName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		task.setTaskName(po.getNewTaskName());
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByIndexFromWrongTab() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INDEX_CHANGE_NAME);
		LogicFeedback actual = logic.updateByIndexChangeName(ContentBox.EXPIRED, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByIndexOutOfBounds() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INVALID_INDEX);
		LogicFeedback actual = logic.updateByIndexChangeName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_INVALID_INDEX, po.getIndex() + 1);
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateSameWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		String taskName = po.getTask().getTaskName();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		input = String.format(STRING_UPDATE_BY_INDEX_CHANGE_DATE_DEADLINE, timeConverter.getDate(currTime 
				                                                                                 + NUM_SECONDS_BUFFER_TIME));
		po = parser.parseInput(input);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(taskName);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateDiffWeek() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		String taskName = po.getTask().getTaskName();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		input = String.format(STRING_UPDATE_BY_INDEX_CHANGE_DATE_DEADLINE, timeConverter.getDate(currTime 
				                                                                                 + NUM_SECONDS_1_WEEK
				                                                                                 + NUM_SECONDS_BUFFER_TIME));
		                                                                                         // Boundary value
		po = parser.parseInput(input);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(taskName);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateFromFloatingToDeadline() {
		long currTime = timeConverter.getCurrTime();
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		String taskName = po.getTask().getTaskName();
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		String input = String.format(STRING_UPDATE_BY_INDEX_CHANGE_DATE_DEADLINE, timeConverter.getDate(currTime 
				                                                                                        + NUM_SECONDS_1_WEEK));
		po = parser.parseInput(input);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(taskName);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateFromDeadlineToEvent() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		String taskName = po.getTask().getTaskName();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		input = String.format(STRING_UPDATE_BY_INDEX_CHANGE_DATE_EVENT, timeConverter.getDate(currTime + NUM_SECONDS_1_DAY),
				              timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK));
		po = parser.parseInput(input);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(taskName);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByIndexChangeDateFromEventToFloating() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_EVENT, timeConverter.getDate(currTime), 
				                     timeConverter.getDate(currTime + NUM_SECONDS_1_WEEK));
		ProcessedObject po = parser.parseInput(input);
		String taskName = po.getTask().getTaskName();
		logic.addEvent(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INDEX_CHANGE_DATE_FLOATING);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(taskName);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateEventTaskByIndexChangeDateExpired() {
		long currTime = timeConverter.getCurrTime();
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		Task task = po.getTask();
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		String input = String.format(STRING_UPDATE_BY_INDEX_CHANGE_DATE_DEADLINE, timeConverter.getDate(currTime 
				                                                                                        - NUM_SECONDS_1_DAY));
		po = parser.parseInput(input);
		LogicFeedback actual = logic.updateByIndexChangeDate(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_DATE_EXPIRED, po.getTask().getDeadline());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByNameChangeName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_NAME_CHANGE_NAME);
		LogicFeedback actual = logic.updateByNameChangeName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		task.setTaskName(po.getNewTaskName());
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByNameFromWrongTab() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_NAME_CHANGE_NAME);
		LogicFeedback actual = logic.updateByNameChangeName(ContentBox.EXPIRED, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = LogicConstants.MSG_EXCEPTION_UPDATE_INVALID_TAB;
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByInvalidName() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		Task task = po.getTask();
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INVALID_NAME);
		LogicFeedback actual = logic.updateByNameChangeName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		String exceptionMsg = String.format(LogicConstants.MSG_EXCEPTION_NAME_NOT_FOUND, po.getTask().getTaskName());
		LogicFeedback expected = new LogicFeedback(temp, po, new Exception(exceptionMsg));
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByIndexChangeBoth() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INDEX_CHANGE_BOTH);
		LogicFeedback actual = logic.updateByIndexChangeBoth(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(po.getNewTaskName());
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUpdateTaskByNameChangeBoth() {
		long currTime = timeConverter.getCurrTime();
		String input = String.format(STRING_ADD_DEADLINE, timeConverter.getDate(currTime));
		ProcessedObject po = parser.parseInput(input);
		logic.addDeadline(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_NAME_CHANGE_BOTH);
		LogicFeedback actual = logic.updateByNameChangeBoth(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		Task task = po.getTask();
		task.setTaskName(po.getNewTaskName());
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUndoAdd() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UNDO);
		LogicFeedback actual = logic.undo(po);
		ArrayList<ArrayList<Task>> temp = getEmptyLists();
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUndoDelete() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		Task task = po.getTask();
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DELETE_BY_INDEX);
		logic.deleteByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UNDO);
		LogicFeedback actual = logic.undo(po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUndoUpdate() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		Task task = po.getTask();
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UPDATE_BY_INDEX_CHANGE_BOTH);
		logic.updateByIndexChangeBoth(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UNDO);
		LogicFeedback actual = logic.undo(po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void testUndoDone() {
		ProcessedObject po = parser.parseInput(STRING_ADD_FLOATING);
		Task task = po.getTask();
		logic.addFloating(originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_DONE_BY_INDEX);
		logic.doneByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		
		originalCopy = logic.getAllTaskLists();
		modifiedCopy = logic.getAllTaskLists();
		po = parser.parseInput(STRING_UNDO);
		LogicFeedback actual = logic.undo(po);
		ArrayList<ArrayList<Task>> temp = addTaskToLists(task);
		LogicFeedback expected = new LogicFeedback(temp, po, null);
		
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingTaggedFloatingTaskShouldUpdateTagDatabase() {
		ProcessedObject po = parser.parseInput("add task #tag1 #tag2");
		logic.addFloating(originalCopy, modifiedCopy, po);
		ArrayList<TagCategory> expected = new ArrayList<TagCategory>();
		expected.add(new TagCategory("tag1"));
		expected.add(new TagCategory("tag2"));
		ArrayList<TagCategory> actual = logic.getTagList();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingTaggedDeadlineTaskShouldUpdateTagDatabase() {
		ProcessedObject po = parser.parseInput("add task on 31 dec 5pm #tag1 #tag2");
		logic.addDeadline(originalCopy, modifiedCopy, po);
		ArrayList<TagCategory> expected = new ArrayList<TagCategory>();
		expected.add(new TagCategory("tag1"));
		expected.add(new TagCategory("tag2"));
		ArrayList<TagCategory> actual = logic.getTagList();
		assertEquals(expected, actual);
	}
	
	@Test
	public void addingTaggedEventTaskShouldUpdateTagDatabase() {
		ProcessedObject po = parser.parseInput("add task from 30 dec 5pm to 31 dec 5pm #tag1 #tag2");
		logic.addEvent(originalCopy, modifiedCopy, po);
		ArrayList<TagCategory> expected = new ArrayList<TagCategory>();
		expected.add(new TagCategory("tag1"));
		expected.add(new TagCategory("tag2"));
		ArrayList<TagCategory> actual = logic.getTagList();
		assertEquals(expected, actual);
	}
	
	@Test
	public void deletingTaggedTaskByIndexShouldUpdateTagDatabase() {
		ProcessedObject po = parser.parseInput("add task #tag1 #tag2");
		logic.addFloating(originalCopy, modifiedCopy, po);
		po = parser.parseInput("del 1");
		logic.deleteByIndex(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		assertTrue(logic.getTagList().isEmpty());
	}
	
	@Test
	public void deletingTaggedTaskByNameShouldUpdateTagDatabase() {
		ProcessedObject po = parser.parseInput("add task #tag1 #tag2");
		logic.addFloating(originalCopy, modifiedCopy, po);
		po = parser.parseInput("del task");
		logic.deleteByName(ContentBox.PENDING, originalCopy, modifiedCopy, po);
		assertTrue(logic.getTagList().isEmpty());
	}
	
	@Test
	public void deletingTagCategoryShouldOnlyRemoveAllTasksWithThatTag() {
		logic.executeCommand(ContentBox.PENDING, "add task #tag1");
		logic.executeCommand(ContentBox.PENDING, "add task2 on 31 dec 3pm #tag2 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task3 from 30 dec 1pm to 31 dec 2pm #tag1 #tag3");
		logic.executeCommand(ContentBox.PENDING, "del #tag3");
		ArrayList<ArrayList<Task>> expected = getEmptyLists();
		Task task = parser.parseInput("add task #tag1").getTask();
		expected.get(ListID.PENDING.getIndex()).add(task);
		expected.get(ListID.GENERAL.getIndex()).add(task);
		ArrayList<ArrayList<Task>> actual = logic.getAllTaskLists();
		assertEquals(expected, actual);
	}
	
	@Test
	public void deletingTagCategoryShouldUpdateTagDatabase() {
		logic.executeCommand(ContentBox.PENDING, "add task #tag1");
		logic.executeCommand(ContentBox.PENDING, "add task2 on 31 dec 3pm #tag2 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task3 from 30 dec 1pm to 31 dec 2pm #tag1 #tag3");
		logic.executeCommand(ContentBox.PENDING, "del #tag3");
		ArrayList<TagCategory> expected = new ArrayList<TagCategory>();
		expected.add(new TagCategory("tag1")); // #tag2 and #tag3 should not be in tag database
		assertEquals(expected, logic.getTagList());
	}
	
	// The order of displayed tasks is not tested here.
	// This test also checks that there are no duplicate tasks in the displayed list.
	@Test
	public void viewingTagsShouldOnlyDisplayAllTasksWithAtLeastOneOfThoseTags() {
		logic.executeCommand(ContentBox.PENDING, "add task1 #tag1");
		logic.executeCommand(ContentBox.PENDING, "add task2 on 31 dec 3pm #tag2 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task3 from 30 dec 1pm to 31 dec 2pm #tag1 #tag3");
		logic.executeCommand(ContentBox.PENDING, "add task4 #tag2 #tag4");
		LogicFeedback lf = logic.executeCommand(ContentBox.PENDING, "view #tag1 #tag3 #tag5");
		ArrayList<Task> viewList = lf.getTaskLists().get(ListID.VIEW.getIndex());
		Task task1 = parser.parseInput("add task1 #tag1").getTask();
		assertTrue(viewList.contains(task1));
		Task task2 = parser.parseInput("add task2 on 31 dec 3pm #tag2 #tag3").getTask();
		assertTrue(viewList.contains(task2));
		Task task3 = parser.parseInput("add task3 from 30 dec 1pm to 31 dec 2pm #tag1 #tag3").getTask();
		assertTrue(viewList.contains(task3));
		assertTrue(viewList.size() == 3); // Should not contain task4
	}
}