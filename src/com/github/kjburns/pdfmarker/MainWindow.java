package com.github.kjburns.pdfmarker;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

class MainWindow extends JFrame {
	private static final long serialVersionUID = -1258305688180711072L;
	private CanvasWidgetForPdfbox canvas;

	MainWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			System.err.println("Unable to load local system Look & Feel");
			e.printStackTrace();
		}
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
		new DocumentKeyboardNavigationHarnessForMainWindow(canvas, this, canvas);
		contentPane.add(canvas.getComponent(), BorderLayout.CENTER);
		
		JToolBar topToolbar = createTopToolbar();
		contentPane.add(topToolbar, BorderLayout.PAGE_START);
	}

	private JToolBar createTopToolbar() {
		JToolBar ret = new JToolBar();
		
		ret.add(new TestAction(getPdfContainer()));
		ret.add(new OpenFileAction(getPdfContainer()));
		
		PdfNavigationWidget navWidget = new PdfNavigationWidget(getPdfContainer());
		ret.add(navWidget.getComponent());
		// TODO Add more toolbar buttons
		return ret;
	}

	private HasActivePdfBoxDocument getPdfContainer() {
		return canvas;
	}
}
