package com.github.kjburns.pdfmarker;

class PdfPageRectangleImpl implements PdfPageRectangle {
	private final float left, right, top, bottom;
	
	public PdfPageRectangleImpl(float l, float r, float t, float b) {
		left = l;
		right = r;
		top = t;
		bottom = b;
	}
	
	@Override
	public float getLeftX() {
		return left;
	}

	@Override
	public float getRightX() {
		return right;
	}

	@Override
	public float getTopY() {
		return top;
	}

	@Override
	public float getBottomY() {
		return bottom;
	}
}
