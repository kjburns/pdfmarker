package com.github.kjburns.pdfmarker;

import javax.swing.JComponent;

public interface CanvasWidgetForPdfbox extends HasActivePdfBoxDocument {
	JComponent getComponent();
}
