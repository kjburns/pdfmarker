package com.github.kjburns.pdfmarker;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JToolBar;

class MainWindow extends JFrame {
	private static final long serialVersionUID = -1258305688180711072L;
	private CanvasWidgetForPdfbox canvas;

	MainWindow() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Pdf Marker");

		addTemporarySetupItems();
		
		buildUi();
		
		pack();
	}

	private void addTemporarySetupItems() {
		setMinimumSize(new Dimension(300, 300));
	}

	private void buildUi() {
		final Container contentPane = getContentPane();

		final BorderLayout layout = new BorderLayout();
		contentPane.setLayout(layout);
		
		canvas = new CanvasWidgetForPdfboxImpl();
		contentPane.add(canvas.getComponent(), BorderLayout.CENTER);
		
		JToolBar topToolbar = createTopToolbar();
		contentPane.add(topToolbar, BorderLayout.PAGE_START);
	}

	private JToolBar createTopToolbar() {
		JToolBar ret = new JToolBar();
		
		ret.add(new TestAction(getPdfContainer()));
		ret.add(new OpenFileAction(getPdfContainer()));
		// TODO Add more toolbar buttons
		return ret;
	}

	private HasActivePdfBoxDocument getPdfContainer() {
		return canvas;
	}
}
