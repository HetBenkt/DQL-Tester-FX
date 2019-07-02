package nl.bos.controllers;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.reactfx.Subscription;

import javafx.fxml.FXML;
import nl.bos.utils.DQLSyntax;

public class ExecuteDQLScript {
	private static final Logger LOGGER = Logger.getLogger(ExecuteDQLScript.class.getName());
	@FXML
	private CodeArea dqlScriptView;
	private Subscription subscribeToText;
	

	@FXML
	public void initialize() {
		LOGGER.log(Level.INFO, "Enter the script view" + dqlScriptView);
		subscribeToText = dqlScriptView.multiPlainChanges().successionEnds(Duration.ofMillis(500))
				.subscribe(ignore -> dqlScriptView.setStyleSpans(0, DQLSyntax.computeHighlighting(dqlScriptView.getText())));
		 dqlScriptView.setParagraphGraphicFactory(LineNumberFactory.get(dqlScriptView));
	}


	
	@FXML
	private void exit() {
		subscribeToText.unsubscribe();
	}
}
