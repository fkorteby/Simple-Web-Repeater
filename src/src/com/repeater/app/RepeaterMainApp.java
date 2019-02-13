/*
 * 
 */
package com.repeater.app;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class RepeaterMainApp {
	private Text destURL;
	private Text srcURL;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			RepeaterMainApp window = new RepeaterMainApp();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		Shell shlSimpleWebRepeater = new Shell();
		shlSimpleWebRepeater.setSize(700, 220);
		shlSimpleWebRepeater.setText("Simple Web Repeater");
		
		Group group = new Group(shlSimpleWebRepeater, SWT.NONE);
		group.setText("Please fill the informations below :");
		group.setBounds(10, 46, 652, 89);
		
		destURL = new Text(group, SWT.BORDER);
		destURL.setText("www.google.com");
		destURL.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		destURL.setBounds(177, 29, 301, 21);
		
		srcURL = new Text(group, SWT.BORDER);
		srcURL.setText("localhost");
		srcURL.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		srcURL.setEnabled(false);
		srcURL.setBounds(177, 56, 301, 21);
		
		Label label = new Label(group, SWT.NONE);
		label.setText("Destination URL :");
		label.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label.setBounds(10, 32, 101, 15);
		
		Label label_1 = new Label(group, SWT.NONE);
		label_1.setText("Source URL :");
		label_1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label_1.setBounds(34, 59, 77, 15);
		
		Label label_2 = new Label(group, SWT.NONE);
		label_2.setText("Source Port :");
		label_2.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label_2.setBounds(509, 59, 72, 15);
		
		Spinner destPORT = new Spinner(group, SWT.BORDER);
		destPORT.setMaximum(65535);
		destPORT.setMinimum(1);
		destPORT.setSelection(443);
		destPORT.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		destPORT.setBounds(587, 29, 59, 22);
		
		Spinner srcPORT = new Spinner(group, SWT.BORDER);
		srcPORT.setEnabled(false);
		srcPORT.setMaximum(65535);
		srcPORT.setMinimum(1);
		srcPORT.setSelection(443);
		srcPORT.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		srcPORT.setBounds(587, 56, 59, 22);
		
		Label label_3 = new Label(group, SWT.NONE);
		label_3.setText("Destination Port :");
		label_3.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label_3.setBounds(484, 32, 97, 15);
		
		Combo srcProtocol = new Combo(group, SWT.READ_ONLY);
		srcProtocol.setEnabled(false);
		srcProtocol.setItems(new String[] {"https", "http"});
		srcProtocol.setBounds(117, 56, 54, 23);
		srcProtocol.setText("https");
		
		Combo destProtocol = new Combo(group, SWT.READ_ONLY);
		destProtocol.setItems(new String[] {"https", "http"});
		destProtocol.setBounds(117, 27, 54, 23);
		destProtocol.setText("https");
		
		Button btnStart = new Button(shlSimpleWebRepeater, SWT.NONE);
		Label title = new Label(shlSimpleWebRepeater, SWT.NONE);
		Button btnStop = new Button(shlSimpleWebRepeater, SWT.NONE);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(WebRepeater.start(srcPORT.getSelection(), srcProtocol.getText(), destURL.getText(), destPORT.getSelection(), destProtocol.getText())){
					title.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GREEN));
					btnStart.setEnabled(false);
					btnStop.setEnabled(true);
				}							
			}
		});
		btnStart.setText("START");
		btnStart.setImage(SWTResourceManager.getImage(RepeaterMainApp.class, "/icons/Play/Play_16x16.png"));
		btnStart.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		btnStart.setBounds(259, 141, 75, 26);
		
		
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(WebRepeater.stop()){
					title.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
					btnStart.setEnabled(true);
					btnStop.setEnabled(false);
				}
				
			}
		});
		btnStop.setText("STOP");
		btnStop.setImage(SWTResourceManager.getImage(RepeaterMainApp.class, "/icons/Stop/Stop_16x16.png"));
		btnStop.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		btnStop.setEnabled(false);
		btnStop.setBounds(340, 141, 73, 26);
		
		Button btnLogs = new Button(shlSimpleWebRepeater, SWT.NONE);
		btnLogs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LogViewerDialog logDialog = new LogViewerDialog(shlSimpleWebRepeater, SWT.ICON_INFORMATION | SWT.OK);
				logDialog.open(WebRepeater.GLOBAL_CONSOLE_FILE, "");
			}
		});
		btnLogs.setText("LOGS");
		btnLogs.setImage(SWTResourceManager.getImage(RepeaterMainApp.class, "/icons/Preview/Preview_16x16.png"));
		btnLogs.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		btnLogs.setBounds(419, 141, 73, 26);
		
		Button btnAbout = new Button(shlSimpleWebRepeater, SWT.NONE);
		btnAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				// create a dialog with ok and cancel buttons and a question icon
				MessageBox dialog = new MessageBox(shlSimpleWebRepeater, SWT.ID_ABOUT);
				dialog.setText("Simple Web Repeater");
				dialog.setMessage("This application is created by Farouk Korteby, for more information please check https://github.com/fkorteby/Simple-Web-Repeater");

				// open dialog and await user selection
				dialog.open();
			}
		});
		btnAbout.setText("ABOUT");
		btnAbout.setImage(SWTResourceManager.getImage(RepeaterMainApp.class, "/icons/Help/Help_16x16.png"));
		btnAbout.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		btnAbout.setBounds(498, 141, 85, 26);
		
		Button btnQuit = new Button(shlSimpleWebRepeater, SWT.NONE);
		btnQuit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.exit(0);
			}
		});
		btnQuit.setText("QUIT");
		btnQuit.setImage(SWTResourceManager.getImage(RepeaterMainApp.class, "/icons/Remove/Remove_16x16.png"));
		btnQuit.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		btnQuit.setBounds(589, 141, 73, 26);
		
		
		title.setText("Simple Web Repeater");
		title.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		title.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		title.setAlignment(SWT.CENTER);
		title.setBounds(10, 10, 652, 30);

		shlSimpleWebRepeater.open();
		shlSimpleWebRepeater.layout();
		while (!shlSimpleWebRepeater.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

}
