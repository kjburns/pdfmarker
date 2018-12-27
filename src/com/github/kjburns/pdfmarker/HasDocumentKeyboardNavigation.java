package com.github.kjburns.pdfmarker;

/**
 * Interface for providing abstraction for handling keyboard navigation of a document.
 * @author Kevin J. Burns, P.E.
 *
 */
interface HasDocumentKeyboardNavigation {
	void pageUp();
	void pageDown();
	void upArrow();
	void downArrow();
	void ctrlHome();
	void ctrlEnd();
}
