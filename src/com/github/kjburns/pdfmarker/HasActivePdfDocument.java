package com.github.kjburns.pdfmarker;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.sun.istack.internal.NotNull;

/**
 * Interface defining interaction with a pdf document container.
 * @author Kevin J. Burns, P.E.
 *
 */
interface HasActivePdfDocument {
	/**
	 * A listener for monitoring a pdf container's document load/unload.
	 * @author Kevin J. Burns, P.E.
	 *
	 */
	static interface PdfContainerListener {
		/**
		 * Called when the specified container will soon load the specified document.
		 * @param container
		 * @param doc
		 */
		void loadingDocument(@NotNull HasActivePdfDocument container, @NotNull PDDocument doc);
		/**
		 * The specified container has loaded the specified document.
		 * @param container
		 * @param doc
		 */
		void loadedDocument(@NotNull HasActivePdfDocument container, @NotNull PDDocument doc);
		/**
		 * Called when the specified container will soon unload the specified document.
		 * @param container
		 * @param doc
		 */
		void unloadingDocument(@NotNull HasActivePdfDocument container, @NotNull PDDocument doc);
		/**
		 * The specified container has unloaded the specified document.
		 * @param container
		 * @param doc
		 */
		void unloadedDocument(@NotNull HasActivePdfDocument container, @NotNull PDDocument doc);
	}
	
	/**
	 * A listener for monitoring the current page of a pdf container.
	 * @author Kevin J. Burns, P.E.
	 *
	 */
	static interface PdfContainerCurrentPageListener {
		/**
		 * The current page of the specified pdf container will soon 
		 * change from {@code oldPage} to {@code newPage}.
		 * @param container
		 * @param oldPage
		 * @param newPage
		 */
		void currentPageChanging(@NotNull HasActivePdfDocument container, int oldPage, int newPage);
		/**
		 * The current page of the specified pdf container has changed
		 * from {@code oldPage} to {@code newPage}.
		 * @param container
		 * @param oldPage
		 * @param newPage
		 */
		void currentPageChanged(@NotNull HasActivePdfDocument container, int oldPage, int newPage);
	}
	
	/**
	 * Gets the active pdf document, if one exists. 
	 * @return The active pdf document, or {@code null} if none exists.
	 */
	PDDocument getActivePdfDocument_rNull();
	/**
	 * Sets the active pdf document.
	 * <p>
	 * If, prior to calling this method, the container already has a document loaded,
	 * the container will notify all registered {@code PdfContainerListener}s that
	 * the document 
	 * {@link PdfContainerListener#unloadingDocument(HasActivePdfDocument, PDDocument) is about to be unloaded} 
	 * and {@link PdfContainerListener#unloadedDocument(HasActivePdfDocument, PDDocument) after it's been unloaded}.
	 * Once the document has been unloaded, {@link #getActivePdfDocument_rNull()} will return {@code null}.
	 * Then, the container will notify listeners that the new document
	 * {@link PdfContainerListener#loadingDocument(HasActivePdfDocument, PDDocument) is about to be loaded}
	 * and {@link PdfContainerListener#loadedDocument(HasActivePdfDocument, PDDocument) has been loaded}.
	 * </p>
	 * @param doc The pdf document to give to this container. 
	 * This parameter must not be null, otherwise an IllegalArgumentException will be raised.
	 * @throws IllegalArgumentException if {@code doc == null}.
	 */
	void setActivePdfDocument(@NotNull PDDocument doc);
	
	/**
	 * Gets the number of pages in the active pdf document. 
	 * @return the number of pages in the active pdf document
	 * @throws IllegalStateException if {@link #getActivePdfDocument_rNull()} 
	 * returns {@code null} when this method is called
	 */
	int getActivePdfDocumentPageCount();
	/**
	 * Gets the current page in the active pdf document.
	 * <p>
	 * The current page will be zero-based. The implementation will decide
	 * on the precise definition of the current page, since some implementations
	 * (such as a scrolling display) may have a squishy definition for the current page.
	 * </p>
	 * @return the current page as defined above
	 * @throws IllegalStateException if {@link #getActivePdfDocument_rNull()} 
	 */
	int getActivePdfDocumentCurrentPage();
	/**
	 * Sets the current page in the active pdf document.
	 * <p>
	 * For the nuances associated with page numbers, see {@link #getActivePdfDocumentCurrentPage()}.
	 * </p>
	 * @param pageNumber the page number to be set as the active page. This
	 * parameter must be in the range [0, {@link #getActivePdfDocumentPageCount()} - 1].
	 * @throws IllegalStateException if {@link #getActivePdfDocument_rNull()} 
	 * @throws IllegalArgumentException if the page number is invalid. 
	 */
	void setActivePdfDocumentCurrentPage(int pageNumber);
	
	/**
	 * Registers a pdf container listener. 
	 * @param l Listener to be registered. May not be {@code null}. 
	 * If the listener has already been registered, it will be ignored.
	 * @throws IllegalArgumentException if {@code l == null}.
	 */
	void addPdfContainerListener(@NotNull PdfContainerListener l);
	/**
	 * Unregisters a pdf container listener.
	 * @param l Listener to be unregistered. If the supplied listener has not been
	 * registered, or if it is {@code null}, nothing happens.
	 */
	void removePdfContainerListener(PdfContainerListener l);
	
	/**
	 * Registers a pdf page number listener. 
	 * @param l Listener to be registered. May not be {@code null}. 
	 * If the listener has already been registered, it will be ignored.
	 * @throws IllegalArgumentException if {@code l == null}.
	 */
	void addPdfCurrentPageListener(@NotNull PdfContainerCurrentPageListener l);
	/**
	 * Unregisters a pdf page number listener.
	 * @param l Listener to be unregistered. If the supplied listener has not been
	 * registered, or if it is {@code null}, nothing happens.
	 */
	void removePdfCurrentPageListener(PdfContainerCurrentPageListener l);
}
