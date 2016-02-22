package taskey.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.util.Duration;

/**
 * This class performs a background service, which updates the UI clock
 * @author JunWei
 *
 */
public class UiClockService extends ScheduledService<Void> {
	
	private Label timeLabelRef;
	private Label dateLabelRef;

	public UiClockService( Label timeLabel, Label dateLabel ) {
		timeLabelRef = timeLabel;
		dateLabelRef = dateLabel;
		this.setDelay(new Duration(0));
		this.setPeriod(new Duration(UiConstants.UPDATE_INTERVAL));
	}
	
	@Override
	protected Task<Void> createTask() {
    	Task<Void> myTask = new Task<Void>() {
			@Override public Void call () {
				Calendar cal = Calendar.getInstance(); // need to get a new updated instance every time
				Platform.runLater(new Runnable() { // let main thread handle the update
					@Override
					public void run() {
						timeLabelRef.setText(formatTime(cal));
						dateLabelRef.setText(UiConstants.CLOCK_DATE_FORMAT.format(cal.getTime()));
					}
				});
				return null;
			}
		};
		return myTask;
	}
	
	public String formatTime(Calendar cal) {
		String myTime = "";
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		String minutePrefix = minute < 10 ? "0" : "";
		String timeOfDay = (cal.get(Calendar.AM_PM) == 1 ? UiConstants.PM_SUFFIX : UiConstants.AM_SUFFIX); // AM or PM
		myTime += hour + ":" + minutePrefix + minute + " " + timeOfDay;
		return myTime;
	}
	
	
	public int getDayOfMonth () { 
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}
	public int getMonthOfYear () { 
		return Calendar.getInstance().get(Calendar.MONTH);
	}
	public int getYear () { 
		return Calendar.getInstance().get(Calendar.MONTH);
	}
}
