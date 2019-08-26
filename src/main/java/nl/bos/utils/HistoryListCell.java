package nl.bos.utils;

import javafx.beans.binding.Bindings;
import javafx.css.Style;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import nl.bos.beans.HistoryItem;
import nl.bos.controllers.QueryWithResult;

import java.util.logging.Logger;

/**
 * TODO refactor QueryWithResult to extact this class
 * 
 * @author loherve
 *
 */
public class HistoryListCell extends ListCell<HistoryItem> {
	private static final Logger LOGGER = Logger.getLogger(HistoryListCell.class.getName());
	private HBox graphic;
	private Hyperlink star;

	@SuppressWarnings("static-access")
	public HistoryListCell(final Style style) {
		QueryWithResult queryWithResultController = (QueryWithResult) Controllers
				.get(QueryWithResult.class.getSimpleName());
		Label label = new Label();
		// Bind the label text to the item property using a converter.
		label.textProperty().bind(Bindings.convert(itemProperty()));
		// Set max width to infinity so the cross is all the way to the right.

		label.setMaxWidth(Double.POSITIVE_INFINITY);

		star = new Hyperlink("☆");
		star.setTextFill(Color.web("#9B870C"));
		star.setStyle("-fx-underline: false");
		star.setVisited(true); // So it is black, and not blue.
		star.setOnAction(event -> {
			// Switch the item favorite flag
			LOGGER.info("Clicked star on " + getItem());
			queryWithResultController.handleFavoriteHistoryItem(getItem(), !getItem().isFavorite());
		});

		Hyperlink cross = new Hyperlink("✗");
		cross.setTextFill(Color.RED);
		cross.setStyle("-fx-underline: false");
		cross.setVisited(true); // So it is black, and not blue.
		cross.setOnAction(event -> {
			// Delete the item from history
			queryWithResultController.handleDeleteHistoryItem(getItem());
		});
		// Arrange controls in a HBox, and set display to graphic only (the text is
		// included in the graphic in this implementation).
		graphic = new HBox(star, label, cross);
		graphic.setHgrow(label, Priority.ALWAYS);
		setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
	}

	protected void updateItem(HistoryItem item, boolean empty) {
		super.updateItem(item, empty);
		if (!empty && item != null) {
			star.textProperty().bind(Bindings.createStringBinding(() -> {
				if (item.isFavorite()) {
					return "★";
				} else {
					return "☆";
				}
			}, item.favoriteProperty()));
			setGraphic(graphic);
			setTooltip(new Tooltip(item.getQuery()));
		} else {
			setGraphic(null);
			setTooltip(null);
		}
	}
}
