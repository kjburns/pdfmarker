package com.github.kjburns.pdfmarker;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

/**
 * A pdf renderer to be used when no document is loaded.
 * @author Kevin J. Burns, P.E.
 *
 */
public class NullPdfRenderer implements PdfRenderer {
	private static class NullCoordinateManager implements PdfPageCoordinateManager {
		private static final PdfPageRectangle NULL_PAGE_RECTANGLE = createNullPageRectangle();
		
		@Override
		public PdfPageRectangle getPageOverallCoordinates(int pageNumber) {
			return NULL_PAGE_RECTANGLE;
		}

		private static PdfPageRectangle createNullPageRectangle() {
			return new PdfPageRectangleImpl(0, 1, 0, 1);
		}

		@Override
		public PdfPageRectangle getPageLocalCoordinates(int pageNumber) {
			return NULL_PAGE_RECTANGLE;
		}

		@Override
		public PdfPageRectangle getBoundsOfDocument() {
			return NULL_PAGE_RECTANGLE;
		}
	}
	
	private static final PdfPageCoordinateManager COORD_MGR = new NullCoordinateManager();

	@Override
	public void renderVisiblePortion(Graphics g, Rectangle2D bounds) {
	}

	@Override
	public PdfPageCoordinateManager getCoordinateManager() {
		return COORD_MGR;
	}
}
