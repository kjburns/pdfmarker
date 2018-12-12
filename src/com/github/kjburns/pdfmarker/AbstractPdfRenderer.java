package com.github.kjburns.pdfmarker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import com.sun.istack.internal.NotNull;

/**
 * An abstract pdf renderer that takes care of the generalities of rendering a portion
 * of a pdf, but does not lock in PdfBox as the pdf platform in use.
 * <p>
 * This implementation assumes that pdf pages are to be arranged in a line vertically--of course, this
 * is determined by whatever methodology was used to arrange the pages in the coordinate manager.
 * As such, the horizontal bounds of individual pages are not checked in determining whether pages
 * are visible. If that level of nuance is required, a more refined implementation will be required.
 * </p>
 * @author Kevin J. Burns, P.E.
 *
 */
public abstract class AbstractPdfRenderer implements PdfRenderer {
	/**
	 * Gets a coordinate manager that describes the location of each page.
	 * Implementors must not return {@code null}.
	 * @return
	 */
	@Override
	public abstract PdfPageCoordinateManager getCoordinateManager();
	/**
	 * Gets the number of pages in the document
	 * @return
	 */
	protected abstract int getPageCount();
	/**
	 * Renders a single page. The upper left corner of the page should be placed at (0, 0) in the provided graphics context.
	 * @param g2d the graphics context to draw on
	 * @param pageNr the zero-based page number of the pdf to render
	 */
	protected abstract void renderPage(final @NotNull Graphics2D g2d, int pageNr);

	@Override
	public void renderVisiblePortion(Graphics g, Rectangle2D visibleBounds) {
		final PdfPageCoordinateManager coordManager = getCoordinateManager();
		if (coordManager == null) {
			throw new RuntimeException("Concrete subclass of AbstractPdfRenderer returned null CoordinateManager.");
		}
		
		final Graphics2D g2d = (Graphics2D)g;
		final int firstVisiblePage = getFirstVisiblePage(visibleBounds);
		if (firstVisiblePage < 0) {
			return;
		}
		final int lastVisiblePage = getLastVisiblePage(visibleBounds, firstVisiblePage);
		if (lastVisiblePage < 0) {
			return;
		}
	
		for (int pageNr = firstVisiblePage; pageNr <= lastVisiblePage; pageNr++) {
			final AffineTransform oldTransform = g2d.getTransform();
			final PdfPageRectangle coords = coordManager.getPageOverallCoordinates(pageNr);
			g2d.translate(coords.getLeftX(), coords.getTopY());
			renderPage(g2d, pageNr);
	
			final PdfPageRectangle localCoords = coordManager.getPageLocalCoordinates(pageNr);
			g2d.setColor(Color.BLACK);
			g2d.drawRect((int)localCoords.getLeftX(), (int)localCoords.getBottomY(), 
					(int)(localCoords.getRightX() - localCoords.getLeftX()), 
					(int)(localCoords.getTopY() - localCoords.getBottomY()));
			g2d.setTransform(oldTransform);
		}
	}

	private int getLastVisiblePage(@NotNull Rectangle2D visibleBounds, int firstVisiblePage) {
		final int pageCount = getPageCount();
		
		for (int i = firstVisiblePage; i < pageCount; i++) {
			final PdfPageRectangle coords = getCoordinateManager().getPageOverallCoordinates(i);
			if (coords.getTopY() > visibleBounds.getMaxY()) {
				return i - 1;
			}
		}
	
		return pageCount - 1;
	}

	private int getFirstVisiblePage(@NotNull Rectangle2D visibleBounds) {
		/*
		 * for now, using a naive search. If very large pdfs become normal then
		 * using a binary search or some such may be better. 
		 * See Issue #1 on github.
		 */
		for (int i = 0; i < getPageCount(); i++) {
			final PdfPageRectangle coords = getCoordinateManager().getPageOverallCoordinates(i);
			if (coords.getBottomY() >= visibleBounds.getMinY()) {
				return i;
			}
		}
	
		return -1;
	}
}