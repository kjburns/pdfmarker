package com.github.kjburns.pdfmarker;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JToolBar;

class MainWindow extends JFrame {
	private static final long serialVersionUID = -1258305688180711072L;

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
		
		JToolBar topToolbar = createTopToolbar();
		contentPane.add(topToolbar, BorderLayout.PAGE_START);
		// TODO Auto-generated method stub
		
	}

	private JToolBar createTopToolbar() {
		JToolBar ret = new JToolBar();
		
		ret.add(new OpenFileAction(getPdfContainer()));
		// TODO Auto-generated method stub
		return ret;
	}

	private HasActivePdfDocument getPdfContainer() {
		// TODO Auto-generated method stub
		return null;
	}
}
