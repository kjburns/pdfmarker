package com.github.kjburns.pdfmarker;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import com.sun.istack.internal.NotNull;

/**
 * Action for opening a file from the main window
 * @author Kevin J. Burns, P.E.
 *
 */
final class OpenFileAction extends AbstractAction 
		implements FilenameFilters {
	private static final String MOST_RECENT_FOLDER_KEY = "last-folder";

	private static final long serialVersionUID = 7137760649854671754L;
	
	private final HasActivePdfBoxDocument pdfContainer;
	
	public OpenFileAction(@NotNull HasActivePdfBoxDocument pdfContainer) {
		this.pdfContainer = pdfContainer;
		
		putValue(SHORT_DESCRIPTION, "Open pdf");
		putValue(NAME, "open");
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		final JFileChooser chooser = createFileChooser();
		
		final int result = chooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			final File selectedFile = chooser.getSelectedFile();
			
			final String parentFolder = selectedFile.getParent();
			final Preferences prefs = Preferences.userNodeForPackage(getClass());
			prefs.put(MOST_RECENT_FOLDER_KEY, parentFolder);
			try {
				prefs.flush();
			} catch (BackingStoreException e) {
				System.err.println("Unable to save most recent folder to preferences.");
				e.printStackTrace();
			}
			
			try {
				PDDocument doc;
				doc = openPdf(selectedFile);
				pdfContainer.setActivePdfDocument(doc);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(chooser, "Could not open pdf: " + selectedFile.getName());
				e.printStackTrace();
			}
		}
	}

	private JFileChooser createFileChooser() {
		File startFolder = getLastFolder();
		if (startFolder == null) {
			startFolder = getDefaultFolder();
		}
		
		final JFileChooser chooser = new JFileChooser(startFolder);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.addChoosableFileFilter(PDF_FILTER);
		chooser.addChoosableFileFilter(MARKUP_FILE_FILTER);
		return chooser;
	}

	private PDDocument openPdf(File selectedFile) throws InvalidPasswordException, IOException {
		PDDocument ret = PDDocument.load(selectedFile);

		return ret;
	}

	private File getDefaultFolder() {
		final String homeFolder = System.getProperty("user.home");
		return new File(homeFolder);
	}

	private File getLastFolder() {
		final Preferences prefs = Preferences.userNodeForPackage(getClass());
		final String lastFolderStr = prefs.get(MOST_RECENT_FOLDER_KEY, null);
		if (lastFolderStr == null) {
			return null;
		}
		else {
			return new File(lastFolderStr);
		}
	}
}
