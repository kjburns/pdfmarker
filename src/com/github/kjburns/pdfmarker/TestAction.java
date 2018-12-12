package com.github.kjburns.pdfmarker;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.pdfbox.pdmodel.PDDocument;

class TestAction extends AbstractAction 
		implements HasActivePdfBoxDocument.PdfContainerListener {
	private static final long serialVersionUID = 8124058387031618479L;
	private final HasActivePdfBoxDocument pdfContainer;
	
	public TestAction(HasActivePdfBoxDocument container) {
		this.pdfContainer = container;
		pdfContainer.addPdfContainerListener(this);
		
		this.putValue(NAME, "test");
		this.putValue(SHORT_DESCRIPTION, "Used to test latest features. Will be removed in production.");
		this.setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		JOptionPane.showMessageDialog(null, "This test action shows every page of the pdf in order.");
		final int pageCount = pdfContainer.getActivePdfDocumentPageCount();
		for (int i = 0; i < pageCount; i++) {
			pdfContainer.setActivePdfDocumentCurrentPage(i);
		}
	}

	@Override
	public void loadingDocument(HasActivePdfBoxDocument container, PDDocument doc) {
		// nothing to do
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
		// nothing to do
	}
}
