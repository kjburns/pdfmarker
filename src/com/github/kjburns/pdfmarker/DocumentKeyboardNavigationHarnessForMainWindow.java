package com.github.kjburns.pdfmarker;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

final class DocumentKeyboardNavigationHarnessForMainWindow {
	private class PageUpAction extends AbstractAction {
		private static final long serialVersionUID = -3554068480009163505L;
		private static final String ACTION_COMMAND_NAME = "page-up-action";

		public PageUpAction() {
			putValue(ACTION_COMMAND_KEY, ACTION_COMMAND_NAME);
		}
		
		@Override
		public void actionPerformed(ActionEvent ev) {
			documentContainer.pageUp();
		}
	}
	
	private class PageDownAction extends AbstractAction {
		private static final long serialVersionUID = -4956406181811813L;
		private static final String ACTION_COMMAND_NAME = "page-down-action";
		
		public PageDownAction() {
			putValue(ACTION_COMMAND_KEY, ACTION_COMMAND_NAME);
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			documentContainer.pageDown();
		}
	}
	
	private class CtrlHomeAction extends AbstractAction {
		private static final long serialVersionUID = -743927927706955236L;
		private static final String ACTION_COMMAND_NAME = "ctrl-home-action";

		public CtrlHomeAction() {
			putValue(ACTION_COMMAND_KEY, ACTION_COMMAND_NAME);
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			documentContainer.ctrlHome();
		}
	}
	
	private class CtrlEndAction extends AbstractAction {
		private static final long serialVersionUID = 3957145319238987397L;
		private static final String ACTION_COMMAND_NAME = "ctrl-end-action";

		public CtrlEndAction() {
			putValue(ACTION_COMMAND_KEY, ACTION_COMMAND_NAME);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			documentContainer.ctrlEnd();
		}
	}
	
	private class DownArrowAction extends AbstractAction {
		private static final long serialVersionUID = 5987464201944629273L;
		private static final String ACTION_COMMAND_NAME = "down-arrow-action";
		
		public DownArrowAction() {
			putValue(ACTION_COMMAND_KEY, ACTION_COMMAND_NAME);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			documentContainer.downArrow();
		}
	}
	
	private class UpArrowAction extends AbstractAction {
		private static final long serialVersionUID = 6728873790084284423L;
		private static final String ACTION_COMMAND_NAME = "up-arrow-action";
		
		public UpArrowAction() {
			putValue(ACTION_COMMAND_KEY, ACTION_COMMAND_NAME);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			documentContainer.upArrow();
		}
	}
	
	private final HasDocumentKeyboardNavigation documentContainer;
	private final JFrame mainWindow;
	private final HasActionMapping scrollingCanvas;

	public DocumentKeyboardNavigationHarnessForMainWindow(HasDocumentKeyboardNavigation documentContainer,
			JFrame mainWindow, HasActionMapping actionMapper) {
		this.documentContainer = documentContainer;
		this.mainWindow = mainWindow;
		scrollingCanvas = actionMapper;
		
		createKeyboardShortcutsForMainWindow();
		createKeyboardShortcutsForCanvas();
	}

	private void createKeyboardShortcutsForCanvas() {
		final InputMap inputMap = scrollingCanvas.getInputMap();
		final ActionMap actionMap = scrollingCanvas.getActionMap();
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), DownArrowAction.ACTION_COMMAND_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_DOWN, 0), DownArrowAction.ACTION_COMMAND_NAME);
		actionMap.put(DownArrowAction.ACTION_COMMAND_NAME, this.new DownArrowAction());
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), UpArrowAction.ACTION_COMMAND_NAME);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_KP_UP, 0), UpArrowAction.ACTION_COMMAND_NAME);
		actionMap.put(UpArrowAction.ACTION_COMMAND_NAME, this.new UpArrowAction());
	}

	private void createKeyboardShortcutsForMainWindow() {
		final InputMap inputMap = mainWindow.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		final ActionMap actionMap = mainWindow.getRootPane().getActionMap();
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0), PageUpAction.ACTION_COMMAND_NAME);
		actionMap.put(PageUpAction.ACTION_COMMAND_NAME, this.new PageUpAction());
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0), PageDownAction.ACTION_COMMAND_NAME);
		actionMap.put(PageDownAction.ACTION_COMMAND_NAME, this.new PageDownAction());
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, KeyEvent.CTRL_DOWN_MASK), CtrlHomeAction.ACTION_COMMAND_NAME);
		actionMap.put(CtrlHomeAction.ACTION_COMMAND_NAME, this.new CtrlHomeAction());
		
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, KeyEvent.CTRL_DOWN_MASK), CtrlEndAction.ACTION_COMMAND_NAME);
		actionMap.put(CtrlEndAction.ACTION_COMMAND_NAME, this.new CtrlEndAction());
	}
}
