package com.github.kjburns.pdfmarker;

import java.awt.Graphics2D;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.sun.istack.internal.NotNull;

/**
 * A PdfBox-specific pdf renderer.
 * @author Kevin J. Burns, P.E.
 *
 */
final class PdfRendererForPdfbox extends AbstractPdfRenderer {
	private final PDDocument document;
	private final PdfPageCoordinateManager coordManager;

	public PdfRendererForPdfbox(@NotNull PDDocument doc) {
		document = doc;
		final PdfPageCoordinateManagerForPdfbox.Builder builder = new PdfPageCoordinateManagerForPdfbox.Builder(document);
		coordManager = builder.build();
	}
	
	@Override
	protected void renderPage(final Graphics2D g2d, int pageNr) {
		PDFRenderer r = new PDFRenderer(document);
		try {
			r.renderPageToGraphics(pageNr, g2d);
		} catch (IOException e) {
			System.err.println("Unable to render page #" + pageNr + " due to an IOException");
			e.printStackTrace();
		}
	}

	@Override
	protected final int getPageCount() {
		return document.getNumberOfPages();
	}

	@Override
	public PdfPageCoordinateManager getCoordinateManager() {
		return coordManager;
	}
}
