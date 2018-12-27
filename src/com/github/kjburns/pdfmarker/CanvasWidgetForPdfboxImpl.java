package com.github.kjburns.pdfmarker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.pdfbox.pdmodel.PDDocument;

class CanvasWidgetForPdfboxImpl 
		implements CanvasWidgetForPdfbox {
	private class PdfRendererPanel extends JPanel {
		private static final long serialVersionUID = 898302421086892730L;
		private final PdfRenderer renderer;
		
		public PdfRenderer getRenderer() {
			return renderer;
		}

		public PdfRendererPanel(PDDocument doc) {
			if (doc == null) {
				renderer = new NullPdfRenderer();
			}
			else {
				renderer = new PdfRendererForPdfbox(doc);
			}
			
			createLayout();
			setDoubleBuffered(true);
		}
		
		private void createLayout() {
			setLayout(null);
			
			final PdfPageRectangle bounds = renderer.getCoordinateManager().getBoundsOfDocument();
			int width = (int)(bounds.getRightX() - bounds.getLeftX());
			int height = (int)(bounds.getBottomY() - bounds.getTopY());
			setPreferredSize(new Dimension(width, height));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			final Graphics2D g2d = (Graphics2D)g;
			final AffineTransform oldTransform = g2d.getTransform();
			final int tx = (int)(-renderer.getCoordinateManager().getBoundsOfDocument().getLeftX());
			g2d.translate(tx, 0);
			
			final Rectangle rect = scrollPane.getViewport().getViewRect();
			renderer.renderVisiblePortion(g, rect);
			g2d.setTransform(oldTransform);
		}
	}
	
	private class CurrentPageTracker
			implements ChangeListener, PdfContainerListener {
		private int currentPage = -1;
		
		@Override
		public void loadingDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			// nothing to do here
		}
		@Override
		public void loadedDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			currentPage = 0;
		}

		@Override
		public void unloadingDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			currentPage = -1;
		}

		@Override
		public void unloadedDocument(HasActivePdfBoxDocument container, PDDocument doc) {
			// nothing to do here
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if (currentPage == -1) {
				return;
			}
			
			final int newPage = getActivePdfDocumentCurrentPage();
			if (newPage != currentPage) {
				final Iterator<PdfContainerCurrentPageListener> it = pageListeners.iterator();
				while (it.hasNext()) {
					it.next().currentPageChanged(CanvasWidgetForPdfboxImpl.this, currentPage, newPage);
				}
				
				currentPage = newPage;
			}
		}
	}
	
	private List<PdfContainerListener> containerListeners = new ArrayList<>();
	private List<PdfContainerCurrentPageListener> pageListeners = new ArrayList<>();
	private PDDocument activeDocument = null;
	
	private final CurrentPageTracker pageTracker = this.new CurrentPageTracker();
	private final JScrollPane scrollPane = createScrollPane();
	private PdfRendererPanel renderingPanel = null;
	
	private static final float FULL_PAGE_JUMP_FRACTION = 0.9f;
	private static final float SINGLE_LINE_JUMP_FRACTION = 0.1f;
	
	public CanvasWidgetForPdfboxImpl() {
		addPdfContainerListener(pageTracker);
	}
	
	@Override
	public PDDocument getActivePdfDocument_rNull() {
		return activeDocument;
	}

	private JScrollPane createScrollPane() {
		JScrollPane ret = new JScrollPane();
		
		ret.getVerticalScrollBar().setUnitIncrement(40);
		ret.getVerticalScrollBar().setBlockIncrement(200);

		ret.getViewport().addChangeListener(this.pageTracker);
		return ret;
	}

	@Override
	public void setActivePdfDocument(PDDocument newDocument) {
		if (newDocument == null) {
			throw new IllegalArgumentException("Passed null document to setActivePdfDocument on canvas widget");
		}
		final PDDocument oldDocument = getActivePdfDocument_rNull();
		if (oldDocument != null) {
			containerListeners.stream().forEach((l) -> {
				l.unloadingDocument(this, oldDocument);
			});
			setDocumentInternal(null);
			containerListeners.stream().forEach((l) -> {
				l.unloadedDocument(this, oldDocument);
			});
			try {
				oldDocument.close();
			} catch (IOException e) {
				System.err.println("Unable to close pdf.");
				e.printStackTrace();
				return;
			}
		}
		
		containerListeners.stream().forEach((l) -> {
			l.loadingDocument(this, newDocument);
		});
		setDocumentInternal(newDocument);
		containerListeners.stream().forEach((l) -> {
			l.loadedDocument(this, newDocument);
		});
	}

	private void setDocumentInternal(PDDocument newDocument) {
		activeDocument = newDocument;
		renderingPanel = new PdfRendererPanel(activeDocument);
		scrollPane.setViewportView(renderingPanel);
		renderingPanel.revalidate();
		renderingPanel.requestFocusInWindow();
	}

	@Override
	public int getActivePdfDocumentPageCount() {
		if (activeDocument == null) {
			return 0;
		}
		return activeDocument.getNumberOfPages();
	}

	@Override
	public int getActivePdfDocumentCurrentPage() {
		if (renderingPanel == null) {
			return -1;
		}
		
		final PdfPageCoordinateManager coordMgr = renderingPanel.getRenderer().getCoordinateManager();
		final Rectangle viewRect = scrollPane.getViewport().getViewRect();
		/*
		 * Implementing for now as brute force. If large pdfs become normal then
		 * a less naive search, like binary search, will be more appropriate.
		 * See Issue #1 on github.
		 * 
		 * The current page will be defined as the page that intersects the top of the view.
		 */
		final float viewTop = (float)viewRect.getMinY();
		for (int pageNr = 0; pageNr < activeDocument.getNumberOfPages(); pageNr++) {
			final PdfPageRectangle pageCoords = coordMgr.getPageOverallCoordinates(pageNr);
			final float top = pageCoords.getTopY();
			final float bottom = pageCoords.getBottomY();
			
			if ((top - viewTop) * (bottom - viewTop) <= 0) {
				return pageNr;
			}
		}

		return -1;
	}

	@Override
	public void setActivePdfDocumentCurrentPage(int newPageNumber) {
		if (activeDocument == null) {
			throw new IllegalStateException("Attempted to change active page when no document was open.");
		}
		checkPageNumber(newPageNumber);
		
		final int oldPageNumber = getActivePdfDocumentCurrentPage();
		
		final Iterator<PdfContainerCurrentPageListener> it = pageListeners.iterator();
		while (it.hasNext()) {
			final PdfContainerCurrentPageListener listener = it.next();
			listener.currentPageChanging(this, oldPageNumber, newPageNumber);
		}

		final PdfPageCoordinateManager coordMgr = renderingPanel.getRenderer().getCoordinateManager();
		final PdfPageRectangle coords = coordMgr.getPageOverallCoordinates(newPageNumber);
		final float topY = coords.getTopY();
		
		final double currX = scrollPane.getViewport().getViewPosition().getX();
		Point newPosition = new Point();
		newPosition.setLocation(currX, topY);
		scrollPane.getViewport().setViewPosition(newPosition);
	}

	private void checkPageNumber(int pageNumber) {
		if (pageNumber < 0 || pageNumber >= activeDocument.getNumberOfPages()) {
			throw new IllegalArgumentException("Requested setting pdf page to out-of-range value");
		}
	}

	@Override
	public void addPdfContainerListener(PdfContainerListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		if (!containerListeners.contains(l)) {
			containerListeners.add(l);
		}
	}

	@Override
	public void removePdfContainerListener(PdfContainerListener l) {
		containerListeners.remove(l);
	}

	@Override
	public void addPdfCurrentPageListener(PdfContainerCurrentPageListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		
		if (!pageListeners.contains(l)) {
			pageListeners.add(l);
		}
	}

	@Override
	public void removePdfCurrentPageListener(PdfContainerCurrentPageListener l) {
		pageListeners.remove(l);
	}

	@Override
	public JComponent getComponent() {
		return scrollPane;
	}

	@Override
	public void pageUp() {
		final JViewport viewport = scrollPane.getViewport();
		final double visibleHeight = viewport.getVisibleRect().getHeight();
		final double jumpDistance = visibleHeight * FULL_PAGE_JUMP_FRACTION;
		advanceUp(jumpDistance);
	}

	@Override
	public void pageDown() {
		final JViewport viewport = scrollPane.getViewport();
		final double visibleHeight = viewport.getVisibleRect().getHeight();
		final double jumpDistance = visibleHeight * FULL_PAGE_JUMP_FRACTION;
		advanceDown(jumpDistance);
	}

	@Override
	public void upArrow() {
		JOptionPane.showMessageDialog(null, "up arrow");
		final JViewport viewport = scrollPane.getViewport();
		final double visibleHeight = viewport.getVisibleRect().getHeight();
		final double jumpDistance = visibleHeight * SINGLE_LINE_JUMP_FRACTION;
		advanceUp(jumpDistance);
	}
	
	private void advanceUp(double pixels) {
		final JViewport viewport = scrollPane.getViewport();
		final PdfPageCoordinateManager coordMgr = renderingPanel.getRenderer().getCoordinateManager();
		final Point currentPosition = viewport.getViewPosition();
		final Point newPosition = new Point();
		final double newTop = Math.max(currentPosition.getY() - pixels, coordMgr.getBoundsOfDocument().getTopY());
		newPosition.setLocation(currentPosition.getX(), newTop);
		viewport.setViewPosition(newPosition);
	}
	
	private void advanceDown(double pixels) {
		final JViewport viewport = scrollPane.getViewport();
		final PdfPageCoordinateManager coordMgr = renderingPanel.getRenderer().getCoordinateManager();
		final Point currentPosition = viewport.getViewPosition();
		final Point newPosition = new Point();
		final double viewportHeight = viewport.getVisibleRect().getHeight();
		final PdfPageRectangle docBounds = coordMgr.getBoundsOfDocument();
		final double newBottom = Math.min(currentPosition.getY() + pixels + viewportHeight,	docBounds.getBottomY());
		final double newTop = Math.max(newBottom, docBounds.getTopY());
		newPosition.setLocation(currentPosition.getX(), newTop);
		viewport.setViewPosition(newPosition);
	}

	@Override
	public void downArrow() {
		final JViewport viewport = scrollPane.getViewport();
		final double visibleHeight = viewport.getVisibleRect().getHeight();
		final double jumpDistance = visibleHeight * SINGLE_LINE_JUMP_FRACTION;
		advanceDown(jumpDistance);
	}

	@Override
	public void ctrlHome() {
		// nothing to do--apparently JScrollPane already takes care of this
	}

	@Override
	public void ctrlEnd() {
		// nothing to do--apparently JScrollPane already takes care of this
	}
}
