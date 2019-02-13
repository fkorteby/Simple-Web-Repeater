/*
 * 
 */
package com.repeater.app;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.wb.swt.SWTResourceManager;

import com.repeater.server.HTTPClient;
import com.repeater.server.SimpleHttpServer;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Combo;

public class RepeaterApp extends Dialog {

	protected Object result;
	protected Shell shlSimpleWebRepeater;
	private Text destURL;
	private Text srcURL;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public RepeaterApp(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		// Get Display
		Display display = Display.getDefault();
		createContents();
		shlSimpleWebRepeater.open();
		shlSimpleWebRepeater.layout();
		display = getParent().getDisplay();
		while (!shlSimpleWebRepeater.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		
		
		shlSimpleWebRepeater = new Shell(getParent(), SWT.SHELL_TRIM);
		shlSimpleWebRepeater.setImage(SWTResourceManager.getImage(RepeaterApp.class, "/icons/Refresh/Refresh.ico"));
		shlSimpleWebRepeater.setSize(688, 228);
		shlSimpleWebRepeater.setText("Simple Web Repeater");
		shlSimpleWebRepeater.setLayout(new FormLayout());
		
		Group grpPleaseFillThe = new Group(shlSimpleWebRepeater, SWT.NONE);
		grpPleaseFillThe.setText("Please fill the informations below :");
		FormData fd_grpPleaseFillThe = new FormData();
		fd_grpPleaseFillThe.top = new FormAttachment(0, 46);
		fd_grpPleaseFillThe.left = new FormAttachment(0, 10);
		fd_grpPleaseFillThe.right = new FormAttachment(100, -10);
		grpPleaseFillThe.setLayoutData(fd_grpPleaseFillThe);
		
		destURL = new Text(grpPleaseFillThe, SWT.BORDER);
		destURL.setText("www.google.com");
		destURL.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		destURL.setBounds(177, 29, 301, 21);
		
		srcURL = new Text(grpPleaseFillThe, SWT.BORDER);
		srcURL.setEnabled(false);
		srcURL.setText("localhost");
		srcURL.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		srcURL.setBounds(177, 56, 301, 21);
		
		Label label = new Label(grpPleaseFillThe, SWT.NONE);
		label.setText("Destination URL :");
		label.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label.setBounds(10, 32, 101, 15);
		
		Label label_1 = new Label(grpPleaseFillThe, SWT.NONE);
		label_1.setText("Source URL :");
		label_1.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label_1.setBounds(34, 59, 77, 15);
		
		Label label_2 = new Label(grpPleaseFillThe, SWT.NONE);
		label_2.setText("Source Port :");
		label_2.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label_2.setBounds(509, 59, 72, 15);
		
		Spinner destPORT = new Spinner(grpPleaseFillThe, SWT.BORDER);
		destPORT.setMaximum(65535);
		destPORT.setMinimum(1);
		destPORT.setSelection(443);
		destPORT.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		destPORT.setBounds(587, 29, 59, 22);
		
		Spinner srcPORT = new Spinner(grpPleaseFillThe, SWT.BORDER);
		srcPORT.setEnabled(false);
		srcPORT.setMaximum(65535);
		srcPORT.setMinimum(1);
		srcPORT.setSelection(80);
		srcPORT.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		srcPORT.setBounds(587, 56, 59, 22);
		
		Label label_3 = new Label(grpPleaseFillThe, SWT.NONE);
		label_3.setText("Destination Port :");
		label_3.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.BOLD));
		label_3.setBounds(484, 32, 97, 15);
		grpPleaseFillThe.setTabList(new Control[]{destURL, srcURL, destPORT, srcPORT});
		
		Label title = new Label(shlSimpleWebRepeater, SWT.NONE);
		title.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
		title.setAlignment(SWT.CENTER);
		FormData fd_title = new FormData();
		title.setLayoutData(fd_title);
		title.setFont(SWTResourceManager.getFont("Segoe UI", 16, SWT.BOLD));
		title.setText("Simple Web Repeater");
		
		Button btnStart = new Button(shlSimpleWebRepeater, SWT.NONE);
		Button btnStop = new Button(shlSimpleWebRepeater, SWT.NONE);
		Combo srcProtocol = new Combo(grpPleaseFillThe, SWT.READ_ONLY);
		Combo destProtocol = new Combo(grpPleaseFillThe, SWT.READ_ONLY);
		srcProtocol.setEnabled(false);
		
		FormData fd_btnStart = new FormData();
		fd_btnStart.left = new FormAttachment(0, 259);
		fd_btnStart.bottom = new FormAttachment(100, -10);
		btnStart.setLayoutData(fd_btnStart);
		btnStart.setImage(SWTResourceManager.getImage(RepeaterApp.class, "/icons/Play/Play_16x16.png"));
		btnStart.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
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
		fd_btnStart.right = new FormAttachment(btnStop, -6);
		
		fd_title.bottom = new FormAttachment(grpPleaseFillThe, -6);
		fd_title.left = new FormAttachment(grpPleaseFillThe, 0, SWT.LEFT);
		fd_grpPleaseFillThe.bottom = new FormAttachment(btnStart, -19);
		
		
		destProtocol.setItems(new String[] {"https", "http"});
		destProtocol.setBounds(117, 27, 54, 23);
		destProtocol.setText("https");
		srcProtocol.setItems(new String[] {"https", "http"});
		srcProtocol.setBounds(117, 56, 54, 23);
		srcProtocol.setText("http");
		
		btnStop.setEnabled(false);
		FormData fd_btnStop = new FormData();
		fd_btnStop.bottom = new FormAttachment(100, -10);
		btnStop.setLayoutData(fd_btnStop);
		btnStop.setImage(SWTResourceManager.getImage(RepeaterApp.class, "/icons/Stop/Stop_16x16.png"));
		btnStop.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		btnStop.setText("STOP");
		
		Button btnAbout = new Button(shlSimpleWebRepeater, SWT.NONE);
		FormData fd_btnAbout = new FormData();
		fd_btnAbout.bottom = new FormAttachment(100, -10);
		btnAbout.setLayoutData(fd_btnAbout);
		btnAbout.setImage(SWTResourceManager.getImage(RepeaterApp.class, "/icons/Help/Help_16x16.png"));
		btnAbout.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		btnAbout.setText("ABOUT");
		
		Button btnQuit = new Button(shlSimpleWebRepeater, SWT.NONE);
		btnQuit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		fd_title.right = new FormAttachment(btnQuit, 0, SWT.RIGHT);
		fd_btnAbout.left = new FormAttachment(btnQuit, -91, SWT.LEFT);
		fd_btnAbout.right = new FormAttachment(btnQuit, -6);
		FormData fd_btnQuit = new FormData();
		fd_btnQuit.left = new FormAttachment(100, -83);
		fd_btnQuit.bottom = new FormAttachment(100, -10);
		fd_btnQuit.right = new FormAttachment(100, -10);
		btnQuit.setLayoutData(fd_btnQuit);
		btnQuit.setImage(SWTResourceManager.getImage(RepeaterApp.class, "/icons/Remove/Remove_16x16.png"));
		btnQuit.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		btnQuit.setText("QUIT");
		
		
		
		Button btnLogs = new Button(shlSimpleWebRepeater, SWT.NONE);
		btnLogs.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		fd_btnStop.left = new FormAttachment(btnLogs, -79, SWT.LEFT);
		fd_btnStop.right = new FormAttachment(btnLogs, -6);
		btnLogs.setText("LOGS");
		btnLogs.setImage(SWTResourceManager.getImage(RepeaterApp.class, "/icons/Preview/Preview_16x16.png"));
		btnLogs.setFont(SWTResourceManager.getFont("Segoe UI", 9, SWT.NORMAL));
		FormData fd_btnLogs = new FormData();
		fd_btnLogs.left = new FormAttachment(btnAbout, -79, SWT.LEFT);
		fd_btnLogs.bottom = new FormAttachment(100, -10);
		fd_btnLogs.right = new FormAttachment(btnAbout, -6);
		btnLogs.setLayoutData(fd_btnLogs);
		shlSimpleWebRepeater.setTabList(new Control[]{grpPleaseFillThe, btnStart, btnStop, btnLogs, btnAbout, btnQuit});

	}
}
