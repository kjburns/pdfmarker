package com.github.kjburns.pdfmarker;

import javax.swing.ActionMap;
import javax.swing.InputMap;

interface HasActionMapping {
	InputMap getInputMap();
	ActionMap getActionMap();
}
