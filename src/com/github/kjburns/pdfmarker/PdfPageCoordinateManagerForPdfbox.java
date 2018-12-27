package com.github.kjburns.pdfmarker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

final class PdfPageCoordinateManagerForPdfbox 
		implements PdfPageCoordinateManager {
	static class Builder {
		private final PDDocument doc;
		private float pageSpacing = 18;
		
		public Builder(PDDocument document) {
			doc = document;
		}
		
		public void setPageSpacing(float spacing) {
			pageSpacing = spacing;
		}
		
		public PdfPageCoordinateManager build() {
			return new PdfPageCoordinateManagerForPdfbox(this);
		}
	}
	
	private final List<PdfPageRectangle> pageRectangles = new ArrayList<>();
	private final Builder builder;
	
	private PdfPageCoordinateManagerForPdfbox(Builder b) {
		builder = b;
		
		final Iterator<PDPage> it = builder.doc.getPages().iterator();
		float maxY = -builder.pageSpacing;
		
		while (it.hasNext()) {
			final PDPage page = it.next();
			final PDRectangle pageCoords = page.getMediaBox();
			final float minY = maxY + builder.pageSpacing;
			maxY = minY + pageCoords.getHeight();
			final float minX = -0.5f * pageCoords.getWidth();
			final float maxX = minX + pageCoords.getWidth();
			
			PdfPageRectangle rect = new PdfPageRectangleImpl(minX, maxX, minY, maxY);
			pageRectangles.add(rect);
		}
	}
	
	@Override
	public PdfPageRectangle getPageOverallCoordinates(int pageNumber) {
		checkPageNumber(pageNumber);
		
		return pageRectangles.get(pageNumber);
	}

	private void checkPageNumber(int pageNumber) {
		if (pageNumber < 0 || pageNumber >= builder.doc.getNumberOfPages()) {
			throw new IndexOutOfBoundsException("Requested reference to non-existent page #" + pageNumber + " of pdf.");
		}
	}

	@Override
	public PdfPageRectangle getPageLocalCoordinates(int pageNumber) {
		checkPageNumber(pageNumber);
		
		final PDRectangle mediaBox = builder.doc.getPage(pageNumber).getMediaBox();
		return new PdfPageRectangleImpl(
				mediaBox.getLowerLeftX(), mediaBox.getUpperRightX(), 
				mediaBox.getUpperRightY(), mediaBox.getLowerLeftY());
	}

	@Override
	public PdfPageRectangle getBoundsOfDocument() {
		final float minY = 0;
		final float maxY = pageRectangles.get(pageRectangles.size() - 1).getBottomY();
		final float minX = (float)pageRectangles.stream().mapToDouble((rect) -> {
			return rect.getLeftX();
		}).min().getAsDouble();
		final float maxX = (float)pageRectangles.stream().mapToDouble((rect) -> {
			return rect.getRightX();
		}).max().getAsDouble();
		
		return new PdfPageRectangleImpl(minX, maxX, minY, maxY);
	}
}
