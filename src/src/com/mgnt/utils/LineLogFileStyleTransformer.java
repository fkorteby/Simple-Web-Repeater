/*
 * 
 */
package com.mgnt.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

// TODO: Auto-generated Javadoc
/**
 * The Class LineLogFileStyleTransformer.
 */
public class LineLogFileStyleTransformer implements LineStyleListener {
	
	/** The keyword. */
	String keyword = "";
	
	/** The current index line. */
	int currentIndexLine = 0;
	
	/** The current text line. */
	String currentTextLine = "";

	// ----------- KETWORDS STYLE -----------------------//	
	/** The Constant IMPORTANT_KEYWORDS. */
	final static String IMPORTANT_KEYWORDS = "DEBUG,INFO";	
	
	/** The Constant KEYWORDS_COLOR. */
	static final Color KEYWORDS_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	
	/** The Constant KEYWORDS_TEXT_STYLE. */
	static final int KEYWORDS_TEXT_STYLE = SWT.BOLD;	
	
	// ----------- KETWORDS STYLE -----------------------//
	/** The Constant SQUARE_BRAKETS_COLOR. */
	static final Color SQUARE_BRAKETS_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND);
	
	/** The Constant SQUARE_BRAKETS_TEXT_STYLE. */
	static final int SQUARE_BRAKETS_TEXT_STYLE = SWT.NORMAL;
	
	// ----------- KETWORDS STYLE -----------------------//
	/** The Constant DATE_TIME_COLOR. */
	static final Color DATE_TIME_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
	
	/** The Constant DATE_TIME_TEXT_STYLE. */
	static final int DATE_TIME_TEXT_STYLE = SWT.BOLD;
	
	// ----------- ERROR STYLE -----------------------//
	/** The Constant ERROR_COLOR. */
	static final Color ERROR_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_RED);
	
	/** The Constant ERROR_BACKGROUND_COLOR. */
	static final Color ERROR_BACKGROUND_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	
	/** The Constant ERROR_TEXT_STYLE. */
	static final int ERROR_TEXT_STYLE = SWT.BOLD;
	
	/** The Constant ERRORS_KEYWORDS. */
	final static String ERRORS_KEYWORDS = "ERROR";

	/* (non-Javadoc)
	 * @see org.eclipse.swt.custom.LineStyleListener#lineGetStyle(org.eclipse.swt.custom.LineStyleEvent)
	 */
	public void lineGetStyle(LineStyleEvent event) {

		event.styles = joinArrayGeneric(
							squareBraketsStyles(event), 
							wordStyle(IMPORTANT_KEYWORDS, KEYWORDS_COLOR, KEYWORDS_TEXT_STYLE, event), 
							dateTimeStyles(event), 
							dateTime2Styles(event), 
							dateTime3Styles(event),
							lineStyle(ERRORS_KEYWORDS, ERROR_COLOR, ERROR_BACKGROUND_COLOR, ERROR_TEXT_STYLE, event),
							findStyles(event),
							setCurrentLineBackground(event)
							);

	}
	
	/**
	 * Sets the current line background.
	 *
	 * @param event the event
	 * @return the style range[]
	 */
	private StyleRange[] setCurrentLineBackground(LineStyleEvent event) {	
		
		// Check if we are in the current line
		if(event.lineOffset <= currentIndexLine && (event.lineOffset + event.lineText.length())  >= currentIndexLine){
			// put the current line
			this.setCurrentTextLine(event.lineText);
			
			// Return the style
			return new  StyleRange[] {
					getHighlightStyle(
							Display.getDefault().getSystemColor(SWT.COLOR_BLACK),
							Display.getDefault().getSystemColor(SWT.COLOR_CYAN), 
							SWT.BOLD, 
							event.lineOffset,
							event.lineOffset + event.lineText.length())
					};
		}
		
		
		return new StyleRange[]{};
	}
	
	/**
	 * Find styles.
	 *
	 * @param event the event
	 * @return the style range[]
	 */
	private StyleRange[] findStyles(LineStyleEvent event) {
				
        if(keyword == null || keyword.length() == 0) {
	          return new StyleRange[0];
	        }
	        
	        String line = event.lineText;
	        int cursor = -1;
	        
	        LinkedList list = new LinkedList();
	        while( (cursor = line.toLowerCase().indexOf(keyword.toLowerCase(), cursor+1)) >= 0) {
	          list.add(getHighlightStyle(
	        		  Display.getDefault().getSystemColor(SWT.COLOR_WHITE), 
	        		  Display.getDefault().getSystemColor(SWT.COLOR_BLUE), 
	        		  SWT.BOLD, 
	        		  event.lineOffset + cursor, 
	        		  keyword.length()
	        		  ));
	        }
	        
	        return (StyleRange[]) list.toArray(new StyleRange[list.size()]);
	}

	/**
	 * Square brakets styles.
	 *
	 * @param event the event
	 * @return the style range[]
	 */
	private StyleRange[] squareBraketsStyles(LineStyleEvent event) {

		List styles = new ArrayList();
		String currentLine = event.lineText;
		String lastMatchRegex = "", currentMatchRegex = "", globalMatchRegex = "";

		Pattern pattern = Pattern.compile("\\[[^\\[]*(\\[[^\\]]*\\][^\\[]*)*\\]");
		Matcher matcher = pattern.matcher(currentLine);
		while (matcher.find()) {
			int offset = event.lineOffset;
			int start = offset + matcher.start();
			int end = offset + matcher.end();

			currentMatchRegex = currentLine.substring(matcher.start(), matcher.end());

			if (lastMatchRegex != currentMatchRegex) {
				styles.add(getHighlightStyle(SQUARE_BRAKETS_COLOR, SQUARE_BRAKETS_TEXT_STYLE, start, (end - start)));
				lastMatchRegex = currentMatchRegex;
			}
		}

		return (StyleRange[]) styles.toArray(new StyleRange[0]);
	}
	
	// 2018-10-21 11:28:46,821
	/**
	 * Date time styles.
	 *
	 * @param event the event
	 * @return the style range[]
	 */
	private StyleRange[] dateTimeStyles(LineStyleEvent event) {

		List styles = new ArrayList();
		String currentLine = event.lineText;
		String lastMatchRegex = "", currentMatchRegex = "", globalMatchRegex = "";

		Pattern pattern = Pattern.compile("\\d{4}[-]\\d{2}[-]\\d{2}\\s\\d{2}[:]\\d{2}[:]\\d{2}[,]\\d{3}");
		Matcher matcher = pattern.matcher(currentLine);
		while (matcher.find()) {
			int offset = event.lineOffset;
			int start = offset + matcher.start();
			int end = offset + matcher.end();

			currentMatchRegex = currentLine.substring(matcher.start(), matcher.end());

			if (lastMatchRegex != currentMatchRegex) {
				styles.add(getHighlightStyle(DATE_TIME_COLOR, DATE_TIME_TEXT_STYLE, start, (end - start)));
				lastMatchRegex = currentMatchRegex;
				// System.out.println("Success : "+currentMatchRegex+" "+(offset
				// + start)+" "+(offset + end));
			}
		}

		return (StyleRange[]) styles.toArray(new StyleRange[0]);
	}
	
	
	/**
	 * Date time2 styles.
	 *
	 * @param event the event
	 * @return the style range[]
	 */
	private StyleRange[] dateTime2Styles(LineStyleEvent event) {

		List styles = new ArrayList();
		String currentLine = event.lineText;
		String lastMatchRegex = "", currentMatchRegex = "", globalMatchRegex = "";
		
		// Format : Feb 25, 2018 11:38:24 PM
		Pattern pattern = Pattern.compile("\\w{3}\\s\\d{2}[,]\\s\\d{4}\\s(\\d{2}|\\d{1})[:](\\d{2}|\\d{1})[:](\\d{2}|\\d{1})\\s[AM|PM]{2}");
		Matcher matcher = pattern.matcher(currentLine);
		while (matcher.find()) {
			int offset = event.lineOffset;
			int start = offset + matcher.start();
			int end = offset + matcher.end();

			currentMatchRegex = currentLine.substring(matcher.start(), matcher.end());

			if (lastMatchRegex != currentMatchRegex) {
				styles.add(getHighlightStyle(DATE_TIME_COLOR, DATE_TIME_TEXT_STYLE, start, (end - start)));
				lastMatchRegex = currentMatchRegex;
				// System.out.println("Success : "+currentMatchRegex+" "+(offset
				// + start)+" "+(offset + end));
			}
		}

		return (StyleRange[]) styles.toArray(new StyleRange[0]);
	}
	
	// 21-Oct-2018 11:28:47.634
	/**
	 * Date time3 styles.
	 *
	 * @param event the event
	 * @return the style range[]
	 */
	private StyleRange[] dateTime3Styles(LineStyleEvent event) {

		List styles = new ArrayList();
		String currentLine = event.lineText;
		String lastMatchRegex = "", currentMatchRegex = "", globalMatchRegex = "";

		Pattern pattern = Pattern.compile("\\d{2}[-]\\w{3}[-]\\d{4}\\s\\d{2}[:]\\d{2}[:]\\d{2}[.]\\d{3}");
		Matcher matcher = pattern.matcher(currentLine);
		while (matcher.find()) {
			int offset = event.lineOffset;
			int start = offset + matcher.start();
			int end = offset + matcher.end();

			currentMatchRegex = currentLine.substring(matcher.start(), matcher.end());

			if (lastMatchRegex != currentMatchRegex) {
				styles.add(getHighlightStyle(DATE_TIME_COLOR, DATE_TIME_TEXT_STYLE, start, (end - start - 1)));
				lastMatchRegex = currentMatchRegex;
			}
		}

		return (StyleRange[]) styles.toArray(new StyleRange[0]);
	}

	/**
	 * Word style.
	 *
	 * @param keywordsList the keywords list
	 * @param color the color
	 * @param style the style
	 * @param event the event
	 * @return the style range[]
	 */
	private StyleRange[] wordStyle(String keywordsList, Color color, int style, LineStyleEvent event) {

		if (keywordsList == null || keywordsList.length() == 0) {
			event.styles = new StyleRange[0];
			return null;
		}

		String line = event.lineText;
		int cursor = -1;

		LinkedList list = new LinkedList();
		String[] keywordsArray = keywordsList.split(",");

		for (String keyword : keywordsArray) {
			while ((cursor = line.indexOf(keyword, cursor + 1)) >= 0) {
				list.add(getHighlightStyle(color, style, event.lineOffset + cursor, keyword.length()-1));
			}
			cursor = -1;
		}

		return (StyleRange[]) list.toArray(new StyleRange[list.size()]);
	}
	
	/**
	 * Line style.
	 *
	 * @param keywordsList the keywords list
	 * @param color the color
	 * @param backgroundColor the background color
	 * @param style the style
	 * @param event the event
	 * @return the style range[]
	 */
	private StyleRange[] lineStyle(String keywordsList, Color color, Color backgroundColor, int style, LineStyleEvent event) {

		if (keywordsList == null || keywordsList.length() == 0) {
			event.styles = new StyleRange[0];
			return null;
		}

		String line = event.lineText;
		int cursor = -1;

		LinkedList list = new LinkedList();
		String[] keywordsArray = keywordsList.split(",");

		for (String keyword : keywordsArray) {
			while ((cursor = line.indexOf(keyword, cursor + 1)) >= 0) {
				list.add(getHighlightStyle(backgroundColor, color, style, event.lineOffset, event.lineText.length()));
			}
			cursor = -1;
		}

		return (StyleRange[]) list.toArray(new StyleRange[list.size()]);
	}

	/**
	 * Gets the highlight style.
	 *
	 * @param color the color
	 * @param fontStyle the font style
	 * @param startOffset the start offset
	 * @param length the length
	 * @return the highlight style
	 */
	private StyleRange getHighlightStyle(Color color, int fontStyle, int startOffset, int length) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = startOffset;
		styleRange.length = length;
		styleRange.background = color;
		styleRange.fontStyle = fontStyle;
		return styleRange;
	}
	
	/**
	 * Gets the highlight style.
	 *
	 * @param color the color
	 * @param backgroundColor the background color
	 * @param fontStyle the font style
	 * @param startOffset the start offset
	 * @param length the length
	 * @return the highlight style
	 */
	private StyleRange getHighlightStyle(Color color, Color backgroundColor, int fontStyle, int startOffset, int length) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = startOffset;
		styleRange.length = length;
		styleRange.foreground = color;
		styleRange.background = backgroundColor;
		styleRange.fontStyle = fontStyle;
		return styleRange;
	}

	/**
	 * Join array generic.
	 *
	 * @param <T> the generic type
	 * @param arrays the arrays
	 * @return the t[]
	 */
	private <T> T[] joinArrayGeneric(T[]... arrays) {
		int length = 0;
		for (T[] array : arrays) {
			length += array.length;
		}

		// T[] result = new T[length];
		final T[] result = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), length);

		int offset = 0;
		for (T[] array : arrays) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}

		return result;
	}
	
	/**
	 * Gets the keyword.
	 *
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * Sets the keyword.
	 *
	 * @param keyword the new keyword
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * Gets the current index line.
	 *
	 * @return the current index line
	 */
	public int getCurrentIndexLine() {
		return currentIndexLine;
	}

	/**
	 * Sets the current index line.
	 *
	 * @param currentIndexLine the new current index line
	 */
	public void setCurrentIndexLine(int currentIndexLine) {
		this.currentIndexLine = currentIndexLine;
	}

	/**
	 * Gets the current text line.
	 *
	 * @return the current text line
	 */
	public String getCurrentTextLine() {
		return currentTextLine;
	}

	/**
	 * Sets the current text line.
	 *
	 * @param currentTextLine the new current text line
	 */
	public void setCurrentTextLine(String currentTextLine) {
		this.currentTextLine = currentTextLine;
	}
}