package com.github.kjburns.pdfmarker;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public interface FilenameFilters {
	final String MARKUP_FILE_EXTENSION = "pdfmarkup";
	
	/**
	 * The file filter for PDF files (and folders)
	 */
	final FileFilter PDF_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith(".pdf");
		}

		@Override
		public String getDescription() {
			return "PDF Document";
		}
	};
	/**
	 * The file filter for markup files (and folders)
	 */
	final FileFilter MARKUP_FILE_FILTER = new FileFilter() {
		@Override
		public boolean accept(File f) {
			return f.isDirectory() || f.getName().endsWith("." + MARKUP_FILE_EXTENSION);
		}

		@Override
		public String getDescription() {
			return "PDF Markup File";
		}
	};
}
