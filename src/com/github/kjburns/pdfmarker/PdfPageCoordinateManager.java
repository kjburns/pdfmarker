package com.github.kjburns.pdfmarker;

public interface PdfPageCoordinateManager {
	/**
	 * Gets the coordinates for a page, referenced to the overall 'scroll' of pages in the pdf.
	 * @param pageNumber Zero-based page number in the pdf.
	 * @return overall coordinates for the requested page
	 * @throws IndexOutOfBoundsException if there is no such page in the pdf
	 */
	PdfPageRectangle getPageOverallCoordinates(int pageNumber);
	/**
	 * Gets the coordinates for a page, referenced to the page itself.
	 * @param pageNumber Zero-based page number in the pdf.
	 * @return local coordinates for the requested page
	 * @throws IndexOutOfBoundsException if there is no such page in the pdf
	 */
	PdfPageRectangle getPageLocalCoordinates(int pageNumber);
	/**
	 * Gets the coordinates for the entire document, referenced to the overall 'scroll' of pages in the pdf.
	 * @return overall coordinates for the document
	 */
	PdfPageRectangle getBoundsOfDocument();
}
