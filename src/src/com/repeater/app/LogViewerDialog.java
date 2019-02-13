/*
 * 
 */
package com.repeater.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Scanner;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import com.mgnt.utils.LineLogFileStyleTransformer;
import com.mgnt.utils.ReverseLineReader;

public class LogViewerDialog extends Dialog {

	protected Object result;
	protected Shell shlLogViewer;
	
	private Text logFilePath;
	private WatchStatus watchStatus = WatchStatus.INACTIVE;
	private Thread watchThread;
	
	private Button btnPause;
	private Button btnPlay;
	private Button btnAlwaysScrollBarTop;

	private static String logfileName;
	private static String logfileDirectory;
	
	private final int LINES_COUNT = 1000;	
	private WatchService watchService;
	private WatchKey watchKey;
	public Text keyword;
	private Composite composite_center;	
	private StyledText logDisplay;
	private final LineLogFileStyleTransformer lineStyleListener = new LineLogFileStyleTransformer();
	
	public enum WatchStatus {
	    ACTIVE,
	    INACTIVE
	}

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public LogViewerDialog(Shell parent, int style) {
		super(parent, style);
		setText("Log Viewer");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open(String logfileName, String logfileDirectory) {
		this.logfileName = logfileName;
		this.logfileDirectory = logfileDirectory;
		createContents();
		shlLogViewer.open();
		shlLogViewer.layout();
		Display display = getParent().getDisplay();
		while (!shlLogViewer.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}
	
	private static String readFromFile(int linesCount) {

		try {
			
			String line = "";
			String result = "";
			int linesIndex = 0;
			
			File file = new File(logfileDirectory + logfileName);
			ReverseLineReader reader = new ReverseLineReader(file, "UTF-8");
			
			while ((linesIndex < linesCount) && ((line = reader.readLine()) != null)) {
				result += line + "\n";
				linesIndex ++;
			}
			
			return result;
			
		} catch (IOException e) {
			return e.getMessage();
		}

	}
	
	private static int getNumberOfLettersOfCurrentFile() {
		
		int result = 0;
		File file = new File(logfileDirectory + logfileName);
		
		try {			
			
			Scanner scanner = new Scanner(file,"utf-8");
			
			while (scanner.hasNext()) {
				result += scanner.nextLine().toCharArray().length;	            
	        }
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;		
	}
	
	private void startWatchService() {

		Path path = Paths.get(logfileDirectory);

		try {

			watchService = FileSystems.getDefault().newWatchService();
			watchKey = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
	                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.OVERFLOW);
			
			while (watchStatus == WatchStatus.ACTIVE) {
				final WatchKey wk = watchService.take();
				for (WatchEvent<?> event : wk.pollEvents()) {

					// we only register "ENTRY_MODIFY" so the context is always
					// a Path.
					final Path changed = (Path) event.context();

					// Check the watched file
					if (changed.endsWith(logfileName)) {

						if(!shlLogViewer.isDisposed()){
							// Refresh the console
							shlLogViewer.getDisplay().asyncExec(new Runnable() {
								
								public void run() {	
									
									// Read the text
									String newTextToDisplay = readFromFile(LINES_COUNT);
									
									int index = newTextToDisplay.indexOf(lineStyleListener.getCurrentTextLine());
									
									// Take the last current line before updating
									logDisplay.setText(newTextToDisplay);
									
									// First reading
									if(btnAlwaysScrollBarTop.getSelection() || index == 0){
										
										// Move to the top
										logDisplay.setTopIndex(0);
										
									} else {
										// Move to the old text
										logDisplay.setSelection(index, index);		
										
										// Move the scroll to the middle
										logDisplay.setTopIndex(logDisplay.getTopIndex() + 5);
									}
									
								}
							});							
						}						
					}
				}
				// reset the key
				boolean valid = wk.reset();
			}

		} catch (InterruptedException e) {
			System.out.println("The watching service is interrupted for directory [" + logfileDirectory + "] file : [" + logfileName +"]");
			
		} catch (IOException e) {
			System.out.println("The following file is not found : [" + logfileDirectory + "][" + logfileName +"]");
		}

	}
	
	private void stopWatchService() {
		
		// Interrupt the Thread
		watchThread.interrupt();
		
		try {
			// close the service
			watchService.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private StyleRange getHighlightStyle(int startOffset, int length) {
	    StyleRange styleRange = new StyleRange();
	    styleRange.start = startOffset;
	    styleRange.length = length;
	    styleRange.background = shlLogViewer.getDisplay().getSystemColor(SWT.COLOR_BLUE);
	    styleRange.foreground = shlLogViewer.getDisplay().getSystemColor(SWT.COLOR_WHITE);
	    styleRange.fontStyle = SWT.BOLD;
	    return styleRange;
	  }

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlLogViewer = new Shell(getParent(), SWT.SHELL_TRIM | SWT.BORDER);
		shlLogViewer.setSize(824, 566);
		shlLogViewer.setText("Log Viewer");
		
		shlLogViewer.addShellListener(new ShellListener() {
			
			@Override
			public void shellClosed(ShellEvent arg0) {
				// Pause the thread
				watchStatus = WatchStatus.INACTIVE;		
				
				// Stop the service and destroy the Thread
				//stopWatchService();
			}

			@Override
			public void shellActivated(ShellEvent arg0) {}
			@Override
			public void shellDeactivated(ShellEvent arg0) {}
			@Override
			public void shellDeiconified(ShellEvent arg0) {}
			@Override
			public void shellIconified(ShellEvent arg0) {}			
	    });
		
		shlLogViewer.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite composite_3 = new Composite(shlLogViewer, SWT.NONE);
		composite_3.setLayout(new GridLayout(1, false));
		
		Composite composite_bottom = new Composite(composite_3, SWT.NONE);
		composite_bottom.setLayout(new GridLayout(4, false));
		GridData gd_composite_bottom = new GridData(SWT.FILL, SWT.BOTTOM, false, false, 1, 1);
		gd_composite_bottom.heightHint = 35;
		composite_bottom.setLayoutData(gd_composite_bottom);
		
		Label lblSearchTextHere = new Label(composite_bottom, SWT.NONE);
		lblSearchTextHere.setText("Search text here :");
		
		keyword = new Text(composite_bottom, SWT.BORDER | SWT.READ_ONLY);
		keyword.setEditable(true);
		keyword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));		
		
		
		Button btnFind = new Button(composite_bottom, SWT.NONE);
		btnFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Apply the style for requested text
				lineStyleListener.setKeyword(keyword.getText());
				
				// Redraw the logDisplay
				logDisplay.redraw();				
				
				// Create the variables
				int index = 0; 
				int selection_index = 0;
				
				// Get current index
				if(logDisplay.getSelection().y > 0){
					index = logDisplay.getSelection().y;
				}
				
				// Calculate the index of the requested string
				selection_index = index + logDisplay.getText().toLowerCase().substring(index, logDisplay.getText().length()).indexOf(keyword.getText().toLowerCase());
				
				// Check if its found or not
				if(selection_index >= index){
					logDisplay.setSelection(selection_index, selection_index + keyword.getText().length());
					keyword.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
				} else {
					keyword.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
				}
			}
		});
		btnFind.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnFind.setText("Find");
		btnFind.setImage(SWTResourceManager.getImage(LogViewerDialog.class, "/icons/Find/Find_16x16.png"));
		btnFind.setEnabled(true);
		
		keyword.addListener(SWT.Traverse, new Listener()
	    {
	        @Override
	        public void handleEvent(Event event)
	        {
	            if(event.detail == SWT.TRAVERSE_RETURN)
	            {
	               btnFind.notifyListeners(SWT.Selection, new Event());
	            }
	        }
	    });
		
		Button btnCancel = new Button(composite_bottom, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Reset keyword Text field
				keyword.setBackground(null);
				keyword.setText("");
				keyword.setFocus();
				
				// Reset the logViewer
				//logDisplay.setSelection(0);
				lineStyleListener.setKeyword("");
				logDisplay.redraw();
			}
		});
		btnCancel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnCancel.setText("Cancel");
		btnCancel.setImage(SWTResourceManager.getImage(LogViewerDialog.class, "/icons/Cancel/Cancel_16x16.png"));
		
		composite_center = new Composite(composite_3, SWT.NONE);
		composite_center.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite_center.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		logDisplay = new StyledText(composite_center, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP | SWT.FULL_SELECTION | SWT.READ_ONLY);
		logDisplay.setText("(The system cannot find the file specified)");
		logDisplay.setEditable(false);
		logDisplay.setAlwaysShowScrollBars(true);
		
		// Custom actions
		logDisplay.addLineStyleListener(lineStyleListener);
		logDisplay.setText(readFromFile(LINES_COUNT));
		
		logDisplay.addCaretListener(new CaretListener() {

			@Override
			public void caretMoved(CaretEvent event) {
				
				lineStyleListener.setCurrentIndexLine(event.caretOffset);
				logDisplay.redraw();
			}
		});
		
		btnAlwaysScrollBarTop = new Button(composite_3, SWT.CHECK);
		btnAlwaysScrollBarTop.setSelection(true);
		GridData gd_btnAlwaysScrollBarTop = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnAlwaysScrollBarTop.widthHint = 175;
		btnAlwaysScrollBarTop.setLayoutData(gd_btnAlwaysScrollBarTop);
		btnAlwaysScrollBarTop.setText("Always Scroll Bar on the Top");
		
		Composite composite_top = new Composite(composite_3, SWT.NONE);
		composite_top.setLayout(new GridLayout(5, false));
		GridData gd_composite_top = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_composite_top.minimumWidth = -1;
		gd_composite_top.minimumHeight = 40;
		composite_top.setLayoutData(gd_composite_top);
		
		Label lblFileOpened = new Label(composite_top, SWT.NONE);
		lblFileOpened.setText("File opened :");
		
		logFilePath = new Text(composite_top, SWT.BORDER | SWT.READ_ONLY);
		logFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		logFilePath.setText(this.logfileDirectory + this.logfileName);
		
		btnPlay = new Button(composite_top, SWT.NONE);
		btnPlay.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnPlay.setImage(SWTResourceManager.getImage(LogViewerDialog.class, "/icons/Play/Play_16x16.png"));
		btnPlay.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Start the thread
				btnPlay.setEnabled(false);
				btnPause.setEnabled(true);
				watchStatus = WatchStatus.ACTIVE;
				
				// Launch the thread update the log file
				watchThread = new Thread() {
					public void run() {
						System.out.println("Watching service starts for the path :["+logfileDirectory+"] file name : "+logfileName);				
						startWatchService();
					}
				};
			}
		});
		btnPlay.setText("Play");
		
		// Start the thread
		btnPlay.setEnabled(false);
		
		btnPause = new Button(composite_top, SWT.NONE);
		btnPause.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnPause.setImage(SWTResourceManager.getImage(LogViewerDialog.class, "/icons/Pause/Pause_16x16.png"));
		btnPause.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Pause the thread
				btnPlay.setEnabled(true);
				btnPause.setEnabled(false);
				watchStatus = WatchStatus.INACTIVE;		
				
				// Stop the service and destroy the Thread
				stopWatchService();
				watchThread = null;
			}
		});
		btnPause.setText("Pause");
		btnPause.setEnabled(true);
		
		Button btnClose = new Button(composite_top, SWT.NONE);
		btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnClose.setImage(SWTResourceManager.getImage(LogViewerDialog.class, "/icons/Delete/Delete_16x16.png"));
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shlLogViewer.close();
			}
		});
		btnClose.setText("Close");

		// Launch the thread update the log file
		this.watchThread = new Thread() {
			public void run() {
				System.out.println("Watching service starts for the path :["+logfileDirectory+"] file name : "+logfileName);				
				startWatchService();
			}
		};
		this.watchStatus = WatchStatus.ACTIVE;		
		this.watchThread.start();
	}
}
