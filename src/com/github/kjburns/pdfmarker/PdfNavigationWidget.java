package com.github.kjburns.pdfmarker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.github.kjburns.pdfmarker.HasActivePdfBoxDocument.PdfContainerCurrentPageListener;
import com.github.kjburns.pdfmarker.HasActivePdfBoxDocument.PdfContainerListener;

class PdfNavigationWidget {
	private abstract class NavigationAction extends AbstractAction
			implements PdfContainerCurrentPageListener, PdfContainerListener {
		private static final long serialVersionUID = -2155209219540473996L;
		private boolean anyDocumentOpen = false;

		protected abstract boolean canActionBePerformedLegally();

		public NavigationAction() {
			pdfContainer.addPdfCurrentPageListener(this);
			pdfContainer.addPdfContainerListener(this);
		}

		protected final boolean isAnyDocumentOpen() {
			return anyDocumentOpen;
		}

		@Override
		public final void setEnabled(boolean proposedState) {
			/*
			 * need to check that this action can legally be performed before 
			 * allowing outside callers to enable the action
			 */
			final boolean canPerform = canActionBePerformedLegally();
			/*
			 * Truth table:
			 *            proposedState
			 *               T   F
			 * --------+---+---------
			 * Can     | T | T   F
			 * Perform | F | F   F
			 * 
			 * ...so it's an 'and'
			 */
			super.setEnabled(proposedState && canPerform);
		}

		@Override
		public final void loadingDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			// nothing to do
		}

		@Override
		public final void loadedDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			if (doc != null) {
				anyDocumentOpen = true;
			}
			setEnabled(true);
		}

		@Override
		public final void unloadingDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			if (doc != null) {
				anyDocumentOpen = false;
			}
			setEnabled(false);
		}

		@Override
		public final void unloadedDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			// nothing to do
		}

		@Override
		public abstract void currentPageChanging(HasActivePdfBoxDocument container, int oldPage, int newPage);

		@Override
		public abstract void currentPageChanged(HasActivePdfBoxDocument container, int oldPage, int newPage);
	}
	
	private class PageUpAction extends NavigationAction {
		private static final long serialVersionUID = -2357545205747665604L;
		public PageUpAction() {
			super();
			
			putValue(NAME, "up");
			putValue(ACTION_COMMAND_KEY, "page-up");
			putValue(SHORT_DESCRIPTION, "Navigates to previous page in the pdf");
		}

		@Override
		protected final boolean canActionBePerformedLegally() {
			return isAnyDocumentOpen() && (pdfContainer.getActivePdfDocumentCurrentPage() > 0);
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			final int currPage = pdfContainer.getActivePdfDocumentCurrentPage();
			
			if (currPage > 0) {
				pdfContainer.setActivePdfDocumentCurrentPage(currPage - 1);
			}
		}

		@Override
		public void currentPageChanging(HasActivePdfBoxDocument container, int oldPage, int newPage) {
			if (newPage == 0) {
				setEnabled(false);
			}
		}

		@Override
		public void currentPageChanged(HasActivePdfBoxDocument container, int oldPage, int newPage) {
			if (newPage != 0) {
				setEnabled(true);
			}
		}
	}
	
	private class PageDownAction extends NavigationAction {
		private static final long serialVersionUID = -2357545205747665604L;
		public PageDownAction() {
			super();
			
			putValue(NAME, "down");
			putValue(ACTION_COMMAND_KEY, "page-down");
			putValue(SHORT_DESCRIPTION, "Navigates to next page in the pdf");
		}

		@Override
		protected final boolean canActionBePerformedLegally() {
			final int pageCount = pdfContainer.getActivePdfDocumentPageCount();
			return isAnyDocumentOpen() && (pdfContainer.getActivePdfDocumentCurrentPage() < pageCount - 1);
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			final int currPage = pdfContainer.getActivePdfDocumentCurrentPage();
			final int pageCount = pdfContainer.getActivePdfDocumentPageCount();
			
			if (currPage < pageCount - 1) {
				pdfContainer.setActivePdfDocumentCurrentPage(currPage + 1);
			}
		}

		@Override
		public void currentPageChanging(HasActivePdfBoxDocument container, int oldPage, int newPage) {
			final int pageCount = container.getActivePdfDocumentPageCount();
			if (newPage == pageCount - 1) {
				setEnabled(false);
			}
		}

		@Override
		public void currentPageChanged(HasActivePdfBoxDocument container, int oldPage, int newPage) {
			final int pageCount = container.getActivePdfDocumentPageCount();
			if (newPage != pageCount - 1) {
				setEnabled(true);
			}
		}
	}
	
	private class PageNumberInput extends JTextField
			implements PdfContainerListener, PdfContainerCurrentPageListener, ActionListener, FocusListener {
		private class InputVerifierImpl extends InputVerifier {
			@Override
			public boolean verify(JComponent input) {
				JTextField tf = (JTextField)input;
				final String txt = tf.getText();
				if (txt.trim().equals("")) {
					return false;
				}
				
				try {
					final int pageNumber = Integer.parseInt(txt);
					return pageNumberIsOk(pageNumber);
				} catch (NumberFormatException e) {
					return false;
				}
			}

			private boolean pageNumberIsOk(int pageNumber) {
				final int pageCount = pdfContainer.getActivePdfDocumentPageCount();
				
				return (pageNumber >= 1) && (pageNumber <= pageCount);
			}
		}
		
		private class RestoreTextAction extends AbstractAction {
			/**
			 * 
			 */
			private static final long serialVersionUID = -7237621688439164675L;
			static final String ACTION_COMMAND_NAME = "restore-text";
			
			public RestoreTextAction() {
				putValue(ACTION_COMMAND_KEY, ACTION_COMMAND_NAME);
			}
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setText(lastKnownText);
				widget.requestFocusInWindow();
			}
		}
		
		private static final long serialVersionUID = 5054483252247101900L;
		private boolean updatingDisplay = false;
		private final InputVerifier inputVerifierInstance = new InputVerifierImpl();
		private String lastKnownText = "";
		
		public PageNumberInput() {
			super(3);

			pdfContainer.addPdfContainerListener(this);
			pdfContainer.addPdfCurrentPageListener(this);
			setHorizontalAlignment(JTextField.CENTER);
			setInputVerifier(inputVerifierInstance);
			addActionListener(this);
			addFocusListener(this);
			
			setupEscapeAction();
		}
		
		private void setupEscapeAction() {
			final InputMap inputMap = getInputMap();
			final ActionMap actionMap = getActionMap();
			
			inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), RestoreTextAction.ACTION_COMMAND_NAME);
			actionMap.put(RestoreTextAction.ACTION_COMMAND_NAME, new RestoreTextAction());
		}

		private void setPageNumber(int pageNumber) {
			if (!updatingDisplay) {
				pdfContainer.setActivePdfDocumentCurrentPage(pageNumber);
			}
		}

		@Override
		public void loadingDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			setText("1");
		}

		@Override
		public void loadedDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			setEnabled(true);
		}

		@Override
		public void unloadingDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			setEnabled(false);
		}

		@Override
		public void unloadedDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			setText("");
		}

		@Override
		public void currentPageChanging(HasActivePdfBoxDocument container, int oldPage, int newPage) {
			// nothing to do
		}

		@Override
		public void currentPageChanged(HasActivePdfBoxDocument container, int oldPage, int newPage) {
			updatingDisplay = true;
			final int displayedPageNumber = newPage + 1;
			setText("" + displayedPageNumber);
			updatingDisplay = false;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				final String text = getText();
				final int pageNumber = Integer.parseInt(text);
				setPageNumber(pageNumber - 1); // because page number is zero-based internally
				widget.requestFocusInWindow();
			} catch (NumberFormatException e) {
				// nothing to do here, but focus won't be lost
			}
		}

		@Override
		public void focusGained(FocusEvent arg0) {
			selectAll();
			lastKnownText = getText();
		}

		@Override
		public void focusLost(FocusEvent arg0) {
			final String text = getText();
			final int pageNumber = Integer.parseInt(text);
			setPageNumber(pageNumber - 1); // because page number is zero-based internally
		}
	}
	
	private class PageCountLabel extends JLabel implements PdfContainerListener {
		private static final long serialVersionUID = -2045878632184443902L;

		public PageCountLabel() {
			super("");
			pdfContainer.addPdfContainerListener(this);
		}

		@Override
		public void loadingDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			// nothing to do here
		}

		@Override
		public void loadedDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			setText("/" + doc.getNumberOfPages());
		}

		@Override
		public void unloadingDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			setText("");
		}

		@Override
		public void unloadedDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			// nothing to do here
		}
	}
	
	private final HasActivePdfBoxDocument pdfContainer;
	private final JComponent widget;
	private final Action pageUpAction;
	private final Action pageDownAction;
	private final JComponent pageNumberInput;
	
	public PdfNavigationWidget(HasActivePdfBoxDocument container) {
		pdfContainer = container;
		// actions and widgets must be initialized after container because they depend on it
		pageUpAction = new PageUpAction();
		pageDownAction = new PageDownAction();
		pageNumberInput = new PageNumberInput();
		
		widget = buildComponent();
		
		setEnabled(false);
	}
	
	private JComponent buildComponent() {
		JToolBar ret = new JToolBar();
		
		ret.add(pageUpAction);
		ret.add(pageNumberInput);
		ret.add(this.new PageCountLabel());
		ret.add(pageDownAction);

		return ret;
	}

	public JComponent getComponent() {
		return widget;
	}

	private void setEnabled(boolean state) {
		pageUpAction.setEnabled(state);
		pageDownAction.setEnabled(state);
		pageNumberInput.setEnabled(state);
	}
}
