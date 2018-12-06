package com.github.kjburns.pdfmarker;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import com.sun.istack.internal.NotNull;

/**
 * Interface representing an object that renders the visible portion of a pdf to a graphics context.
 * @author Kevin J. Burns, P.E.
 *
 */
public interface PdfRenderer {
	/**
	 * Renders the visible portion of a pdf to the supplied graphics context according
	 * to the supplied bounds object's limits.
	 * @param g The graphics context to draw on
	 * @param bounds The visible portion in which the pdf should be drawn.
	 */
	void renderVisiblePortion(@NotNull Graphics g, @NotNull Rectangle2D bounds);
}
