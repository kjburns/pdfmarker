package com.github.kjburns.pdfmarker;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;

public interface CanvasWidgetForPdfbox extends HasActivePdfBoxDocument, HasDocumentKeyboardNavigation, HasActionMapping {
	JComponent getComponent();

	@Override
	default InputMap getInputMap() {
		return getComponent().getInputMap();
	}

	@Override
	default ActionMap getActionMap() {
		return getComponent().getActionMap();
	}
}
