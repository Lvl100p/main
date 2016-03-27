package taskey.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

import taskey.messenger.TagCategory;
import taskey.messenger.Task;
import taskey.storage.StorageReader.InvalidTaskException;


/**
 * @@author A0121618M
 * This class exposes public IO methods for saving tasklists and tags.
 * It also manages the storage directory.
 */
public class Storage {
	private StorageReader storageReader;
	private StorageWriter storageWriter;
	private File directory;

	private static final File DEFAULT_DIRECTORY = new File("Taskey savefiles");
	private static final String FILENAME_TAGS = "USER_TAG_DB.taskey";
	public static final String FILENAME_DIRCONFIG = "last_used_directory.taskeyconfig"; //name of the directory config file
	public static final String FILENAME_EXTENSION = ".taskey";						    //public for unit tests
	private static final int NUM_TASKLISTS_FROM_LOGIC = taskey.logic.LogicMemory.NUM_TASK_LISTS;

	public enum TasklistEnum {
		// Index 0 (THIS_WEEK list) and 7 (ACTION list) from Logic is to be ignored
		PENDING		("PENDING.taskey", 1),
		EXPIRED		("EXPIRED.taskey", 2),
		GENERAL		("GENERAL.taskey", 3),	//TODO: consolidate files?
		DEADLINE	("DEADLINE.taskey", 4),	//
		EVENT		("EVENT.taskey", 5),	//
		COMPLETED	("COMPLETED.taskey", 6);

		private static final int size = TasklistEnum.values().length;
		public static int size() {
			return size;
		}

		private final String filename;
		private final int index;

		TasklistEnum(String filename, int index) {
			this.filename = filename;
			this.index = index;
		}

		public String filename() {
			return filename;
		}

		public int index() {
			return index;
		}

		/**
		 * Checks whether TasklistEnum contains the given filename string.
		 * @param fileName
		 * @return true if the given filename matches any of the enum fields; false otherwise
		 */
		public static boolean contains(String fileName) {
			for (TasklistEnum e : TasklistEnum.values()) {
				if (e.filename.equals(fileName)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Get the corresponding index of the given filename string.
		 * @param fileName
		 * @return the index of the enum constant that has the given filename;
		 * 		   -1 is returned if the given filename does not exist in the enum
		 */
		public static int indexOf(String fileName) {
			for (TasklistEnum e : TasklistEnum.values()) {
				if (e.filename.equals(fileName)) {
					return e.index;
				}
			}
			return -1;
		}
	}

	/**
	 * For testing
	 */
	public static void main(String args[]) {
		// The default or last-used directory is automatically set in the constructor method.
		Storage storage = new Storage();

		// Can optionally set the directory again, if requested by user.
		//System.out.println(storage.setDirectory("c:\\taskey"));

		// Initialize tasklists
		ArrayList<ArrayList<Task>> loadedLists = storage.loadAllTasklists();
		print(loadedLists);
	}
	
	/**
	 * For testing
	 */
	private static void print(ArrayList<ArrayList<Task>> lists) {
		int i=1;
		for (ArrayList<Task> list : lists) {
			System.out.println(i++);
			for (Task t : list) {
				System.out.println(t);
			}
		}
		System.out.println("end print");
	}


	/*=============*
	 * Constructor *
	 *=============*/
	/**
	 * Storage constructor and initializer.
	 * Attempts to load and set the last used directory.
	 * If none was found, DEFAULT_DIRECTORY will be set instead.
	 * Post-condition: all the fields of Storage have been instantiated.
	 */
	public Storage() {
		storageReader = new StorageReader();
		storageWriter = new StorageWriter();
		
		File loadedDirectory = storageReader.loadDirectoryConfigFile(FILENAME_DIRCONFIG);
		if (loadedDirectory != null) {
			if (createDirectory(loadedDirectory) == true) {
				directory = loadedDirectory;
				System.out.println("{Storage directory loaded} " + directory.getAbsolutePath());
			} else { //loaded directory was invalid
				directory = DEFAULT_DIRECTORY;
			}
		} else { //directory config file not found
			createDirectory(DEFAULT_DIRECTORY);
			directory = DEFAULT_DIRECTORY;
		}
	}

	
	/*=====================*
	 * Load/Save tasklists *
	 *=====================*/
	/**
	 * Returns the superlist of tasklists loaded from Storage.
	 * Logic calls this on program startup.
	 * <p>Post-conditions:
	 * <br>- The lists in the returned superlist are in the same order as the enum constants in TasklistEnum.
	 * <br>- These lists are read from disk and hence do not include the THIS_WEEK and ACTION list.
	 * <br>- If any one list was not found, or is invalid, an empty superlist is returned.
	 * @return the superlist of tasklists read from disk, or an empty superlist
	 */
	public ArrayList<ArrayList<Task>> loadAllTasklists() {
		ArrayList<ArrayList<Task>> superlist = new ArrayList<ArrayList<Task>>();

		for (TasklistEnum tasklist : TasklistEnum.values()) {
			try {
				File src = new File(directory, tasklist.filename());
				ArrayList<Task> loadedList = storageReader.loadTasklist(src);
				superlist.add(loadedList);
			} catch (FileNotFoundException | InvalidTaskException e) {
				superlist.clear();
				while (superlist.size() < TasklistEnum.size()) {
					superlist.add(new ArrayList<Task>());
				}
				return superlist;
			}
		}
		return superlist;
	}

	/**
	 * Saves the superlist of tasklists to Storage.
	 * Logic calls this after every operation.
	 * <p>Pre-conditions:
	 * <br>- Starting from index 1, the lists in the given superlist
	 * 		 must be in the same order as the enum constants in TasklistEnum.
	 * <br>- Index 0 is reserved for the THIS_WEEK list and is not saved to disk because it is time-dependent.
	 * <br>- Index 7 is reserved for the ACTION list and is not saved to disk because it is session-dependent.
	 * @param superlist the list of tasklists to be saved
	 * @throws IOException
	 */
	public void saveAllTasklists(ArrayList<ArrayList<Task>> superlist) throws IOException {
		assert (superlist.size() == NUM_TASKLISTS_FROM_LOGIC);

		for (TasklistEnum tasklist : TasklistEnum.values()) {
			ArrayList<Task> listToSave = superlist.get(tasklist.index());
			File dest = new File(directory, tasklist.filename());
			storageWriter.saveTasklist(listToSave, dest);
		}
	}


	/*================*
	 * Load/Save tags *
	 *================*/
	/**
	 * Returns the ArrayList of Tags loaded from Storage.
	 * An empty ArrayList is returned if the tags file was not found.
	 * @return the ArrayList of user-defined tags, or an empty ArrayList if the file was not found
	 */
	public ArrayList<TagCategory> loadTaglist() {
		File src = new File(directory, FILENAME_TAGS);
		return storageReader.loadTaglist(src);
	}

	/**
	 * Saves the given ArrayList of Tags to Storage.
	 * @param tags the ArrayList containing the user-defined tags
	 * @throws IOException in case Logic wants to handle the exception
	 */
	public void saveTaglist(ArrayList<TagCategory> tags) throws IOException {
		assert (tags != null);
		File dest = new File(directory, FILENAME_TAGS);
		storageWriter.saveTaglist(tags, dest);
	}


	/*=====================*
	 * Set/get directories *
	 *=====================*/
	/**
	 * Returns the current storage directory.
	 * When the user asks to change directory, Logic can return it as feedback.
	 * @return absolute path of the default or user-set directory
	 */
	public String getDirectory() {
		return directory.getAbsolutePath();
	}
	
	/**
	 * Has the same effect as calling setDirectory(pathname, true)
	 */
	public boolean setDirectory(String pathname) throws FileAlreadyExistsException, IOException {
		return setDirectory(pathname, true);
	}

	/**
	 * Changes the storage directory to the given pathname string after checking that the path is valid.
	 * If the given boolean argument is true, .taskey files in the current directory will be moved to the new one.
	 * This method is invoked by Logic, should the end user request to change it.
	 * 
	 * <p>Post-conditions:
	 * <br>- Creates the directory if it does not exist yet.
	 * <br>- If requested, moves the .taskey storage files from the existing directory to the new one, 
	 * 		 provided the new directory does not contain a full set of pre-existing tasklist files.
	 * <br>- Saves the new directory setting to a persistent config file in "user.dir".
	 * <br>- Storage's directory will not be updated if the specified exceptions are thrown.
	 * 
	 * @return <br>- False if the path was invalid due to illegal characters (e.g. *), 
	 * 		   reserved words (e.g. CON in Windows), or nonexistent root drive letters
	 *         <br>- True if the new directory was successfully set
	 *         
	 * @param pathname can be an absolute path, or relative to "user.dir"
	 * @param shouldMove boolean flag to specify whether the move operation should be performed
	 * 
	 * @throws FileAlreadyExistsException if the new directory already contains a full set of existing tasklists.
	 *			This is a signal for Logic to call loadAllTasklists(), then setDirectory(pathname, false).
	 *
	 * @throws IOException if an I/O error occurs when moving the files.
	 * 			This is not an atomic operation, so it is possible that some files have already been moved. 
	 *			Logic should save everything after this to ensure that the current directory still has all the savefiles.
	 */
	public boolean setDirectory(String pathname, boolean shouldMove) throws FileAlreadyExistsException, IOException {
		File dir = new File(pathname);
		if (createDirectory(dir) == false) {
			return false;
		}
		
		if (shouldMove) {
			// Check for existing task savefiles in dir; perform the move only if it does not
			if (!containsExistingTaskFilesIn(dir)) {
				moveFiles(directory, dir);
			} else {
				// Else if there are existing savefiles, signal Logic to load them
				System.out.println("{New directory contains existing tasklist files!} " + dir.getAbsolutePath());
				throw new FileAlreadyExistsException(null);
			}
		}

		directory = dir;
		if (shouldSave(dir)) {
			storageWriter.saveDirectoryConfigFile(dir, FILENAME_DIRCONFIG);
		}
		System.out.println("{Storage directory set} " + directory.getAbsolutePath());
		return true;
	}

	/**
	 * Creates the full directory path of the given abstract pathname.
	 * @param dir
	 * @return true if all the necessary folders were created or already exist and is valid; false otherwise
	 */
	private boolean createDirectory(File dir) {
		if (!dir.exists()) {
			if (dir.mkdirs() == false) { //mkdirs() failed
				return false;
			}
		}
		
		if (dir.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks if the given directory contains the full set of tasklist savefiles.
	 * @param dir directory to check for pre-existing tasklist files
	 * @return true if and only if dir contains the full set of tasklist files; false otherwise
	 */
	private boolean containsExistingTaskFilesIn(File dir) {
		// dir is empty
		if (dir.listFiles().length == 0) {
			return false;
		}
		
		Boolean[] tasklistFlags = new Boolean[TasklistEnum.size()];
		for (File file : dir.listFiles()) {
			String filename = file.getName();
			if (TasklistEnum.contains(filename)) {
				tasklistFlags[TasklistEnum.indexOf(filename)-1] = true;
			}
		}

		if (Arrays.asList(tasklistFlags).contains(false)) {
			return false; //at least one tasklist file is missing
		} else {
			return true; //all tasklist files are present in dir
		}
	}

	/**
	 * Moves the ".taskey" savefiles from the given source to destination directories.
	 * @param srcDir the source directory
	 * @param destDir the destination diectory
	 * @returns true if the files were moved successfully; false if no files were moved
	 * @throws IOException thrown by Files.move method
	 */
	private boolean moveFiles(File srcDir, File destDir) throws IOException {
		// Skip the move if the source and destination files are the same
		if (srcDir.getAbsolutePath().equalsIgnoreCase(destDir.getAbsolutePath())) {
			return false;
		}
		
		boolean wasMoved = false;
		for (File srcFile : srcDir.listFiles()) {
			if ( srcFile.getName().endsWith(FILENAME_EXTENSION) ) {
				Path srcPath = srcFile.toPath();
				Path destPath = destDir.toPath().resolve(srcFile.getName());

				try {
					Files.move(srcPath, destPath, StandardCopyOption.REPLACE_EXISTING);
					wasMoved = true;
				} catch (IOException e) {
					e.printStackTrace();
					throw e;
				}
			}
		}

		if (wasMoved) {
			System.out.println("{Storage files moved}");
		}
		return wasMoved;
	}
	
	/**
	 * Checks whether the abstract pathname given by dir should be saved to the directory config file.
	 * @param dir the candidate directory
	 * @return true if dir is different from the current directory;
	 * 		   false if dir is the default directory or is the same as the current
	 */
	private boolean shouldSave(File dir) {
		// If dir is equal to the default directory, we can delete the config file
		// since Storage does not need it to remember it
		if (dir.getAbsolutePath().equalsIgnoreCase(DEFAULT_DIRECTORY.getAbsolutePath())) {
			File configFile = new File(FILENAME_DIRCONFIG);
			try {
				Files.delete(configFile.toPath());
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return true; //since delete failed, return true to save it
			}
		}
		
		// Check that dir is different from the current directory
		if (! dir.getAbsolutePath().equalsIgnoreCase(directory.getAbsolutePath()) ) {
			return true;
		} else {
			return false;
		}
	}
}