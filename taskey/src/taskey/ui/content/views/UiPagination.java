package taskey.ui.content.views;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 * @@author A0125419H
 * This class is used to provide pagination support with extensions.
 * Such as selecting of elements
 * It is used in almost all of the tab contents of the UiFormatters
 * 
 * @author junwei
 * 
 */

public class UiPagination {
	private Pagination myPages;
	private ArrayList<GridPane> myGrids;
	private ArrayList<ArrayList<StackPane>> totalEntries; // track for key events
	private int currentPage;
	private int selectionInPage;
	private int currentSelection; // against total number of entries
	private String selectionStyle;
	private StackPane selectedPane = null;
	private boolean isSettingUp; // this is for preventing reset of uiPagination to page 0
	private ScrollPane scrollPane = null; // such that scrollbar will move with selection
	
	public UiPagination(String _selectionStyle) {
		selectionStyle = _selectionStyle;
		myPages = new Pagination(); 
		myGrids = new ArrayList<GridPane>();	
		totalEntries = new ArrayList<ArrayList<StackPane>>();
		currentSelection = 0;
		selectionInPage = 0;
		isSettingUp = true;
		
		disableKeyInputs();
	}
	
	public Pagination getPagination() {
		return myPages;
	}
	
	private void disableKeyInputs() {
		// so that the default pagination arrows wont affect the manual key input calls
		myPages.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				event.consume();
			}
		});
	}

	/**
	 * Initialize and sets up the display for Pagination with totalPages.
	 * Then binds the method for create pages
	 * @param totalPages - the total pages
	 */
	public void initializeDisplay( int totalPages ) {	
		if ( totalPages == 0 ) {// for formatting
			myPages.setPageCount(1);
			myPages.setMaxPageIndicatorCount(1);
		} else {
			myPages.setPageCount(totalPages);
			myPages.setMaxPageIndicatorCount(totalPages);
		}
		myPages.getStyleClass().add(Pagination.STYLE_CLASS_BULLET);
		
		myPages.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer pageIndex) {
                return createPage(pageIndex);
            }
        });
		isSettingUp = false;
	}
	
	public int getSelection() { 
		if ( totalEntries.size() == 0 ) {
			return -1;
		}
		return currentSelection;
	}
	
	private void setCurrentToSelectionInPage() {  // map current selection to the selection in a page
		int currentIndex = 0; 
		for ( int i = 0; i < totalEntries.size(); i++ ) {
			if ( i != currentPage ) {
				currentIndex += totalEntries.get(i).size();
			} else {
				selectionInPage = Math.max(selectionInPage,0);
				selectionInPage = Math.min(selectionInPage,totalEntries.get(i).size()-1);
				currentIndex += selectionInPage;
				break;
			}
		}
		currentSelection = currentIndex;
	}
	
	public void selectInPage(int pageTo, int selection) { // select an entry in a page
		if ( pageTo < 0 || pageTo >= totalEntries.size() ) {
			return;
		}
		selectionInPage = selection;
		currentPage = pageTo;
		myPages.setCurrentPageIndex(currentPage); // go to the target page where item resides
		setCurrentToSelectionInPage();
		
		ArrayList<StackPane> pageContent = totalEntries.get(currentPage);
		if ( pageContent.size() == 0 ) { // no elements in page
			return;
		}
		// remove previous selection's style
		if ( selectedPane != null ) {
			selectedPane.getStyleClass().remove(selectionStyle);
		}	
		selectedPane = (StackPane) pageContent.get(selectionInPage);
		selectedPane.getStyleClass().add(selectionStyle);		
		
		adjustScrollPaneToSelection(); 
	}
	
	/**
	 * Process key, for the selection to be handled
	 * Needs to be called by the UiFormatters in order to interact with the page
	 *
	 * @param - event the event
	 */
	public void processKey(KeyEvent event) {
		if ( totalEntries.size() == 0 ) {
			return;
		}
		if ( event.getCode() == KeyCode.PAGE_DOWN) {
			if ( selectionInPage + 1 > totalEntries.get(currentPage).size()-1) { // do page switch
				selectInPage(currentPage+1,0);
			} else {
				selectInPage(currentPage,selectionInPage+1);
			}
		} else if ( event.getCode() == KeyCode.PAGE_UP) {
			if ( selectionInPage - 1 < 0 && currentPage >= 1 ) { // there exists a previous page
				int prevPageSize = totalEntries.get(currentPage-1).size(); // go previous
				selectInPage(currentPage-1, prevPageSize-1);
			} else {
				selectInPage(currentPage,selectionInPage-1);
			}
		} else if ( event.getCode() == KeyCode.RIGHT) {
			selectInPage(currentPage+1,selectionInPage);
		} else if ( event.getCode() == KeyCode.LEFT) {
			selectInPage(currentPage-1,selectionInPage);
		}
		
	}	

	/**
	 * Adds the grid to pagination, and provides an arraylist of stackpanes
	 * Which will be handled by UiPagination to do selection
	 *
	 * @param theGrid - the grid
	 * @param pageEntries - the page entries
	 */
	public void addGridToPagination(GridPane theGrid, ArrayList<StackPane> pageEntries) {
		totalEntries.add(pageEntries);
		myGrids.add(theGrid);	
	}
	
	/**
	 * Creates the page.
	 * Note this method creates the whole page by default, therefore we keep an arraylist of grids instead
	 * @param pageIndex the page index
	 * @return the grid pane
	 */
	private GridPane createPage(int pageIndex) {
		if ( pageIndex >= myGrids.size() ) {
			return null; // grid has not been initialized
		} 
		if ( isSettingUp == true ) { // workaround for pagination index resetting to 0 on setPageFactory
			// leave current Page active
		} else {
			currentPage = pageIndex;
		}
		if ( currentPage > myGrids.size()-1) { // on deletion of task
			int lastPage = myGrids.size()-1;
			currentPage = lastPage;
			selectionInPage = totalEntries.get(lastPage).size()-1;
		}
		selectInPage(currentPage,selectionInPage);
		return myGrids.get(currentPage);
    }	
	
	public void clear() {
		isSettingUp = true;
		myGrids.clear();
		totalEntries.clear();
		selectedPane = null;
	}

	public void setScrollPane(ScrollPane mainPane) {
		scrollPane = mainPane;
	}
	
	private void adjustScrollPaneToSelection() {
		if ( scrollPane == null ) {
			return;
		}

		// crude shifting of layout
		// Since layout bounds are updated internally, and not at this point in time
		scrollPane.vvalueProperty().bind(
				selectedPane.layoutYProperty().
				subtract(selectedPane.getLayoutY() + 
						 scrollPane.getViewportBounds().getMaxY() > 
						 selectedPane.layoutBoundsProperty().getValue().getMaxY() ? 
						 scrollPane.getViewportBounds().getMaxY() - 
						 selectedPane.layoutBoundsProperty().getValue().getMaxY() : 0));
	}
}
