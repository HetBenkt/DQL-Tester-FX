package nl.bos.utils;

import javafx.beans.binding.Bindings;
import javafx.css.Style;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import nl.bos.beans.HistoryItem;
import nl.bos.controllers.QueryWithResult;

/**
 * TODO refactor QueryWithResult to extact this class (how to handle actions?)
 * @author loherve
 *
 */
public class HistoryListCell extends ListCell<HistoryItem> {
	private HBox graphic;

	@SuppressWarnings("static-access")
	public HistoryListCell(QueryWithResult parent, final Style style) {

		Label label = new Label();
		// Bind the label text to the item property using a converter.
		label.textProperty().bind(Bindings.convert(itemProperty()));
		// Set max width to infinity so the cross is all the way to the right.

		label.setMaxWidth(Double.POSITIVE_INFINITY);
		// We have to modify the hiding behavior of the ComboBox to allow clicking on
		// the hyperlink,
		// so we need to hide the ComboBox when the label is clicked (item selected).

		Hyperlink star = new Hyperlink("☆");
//		star.textProperty().bind(Bindings.createStringBinding(() -> {
//			if (getItem().isFavorite()) {
//				return "⭐";
//			} else {
//				return "☆";
//			}
//		}, getItem().favoriteProperty()));
		star.setVisited(true); // So it is black, and not blue.
		star.setOnAction(event -> {
			// Remove the item from history
			//handleFavoriteHistoryItem(getItem(), !getItem().isFavorite());
		});

		Hyperlink cross = new Hyperlink("X");
		cross.setVisited(true); // So it is black, and not blue.
		cross.setOnAction(event -> {
			// Since the ListView reuses cells, we need to get the item first, before making
			// changes.
			// Remove the item from history
			parent.handleDeleteHistoryItem(getItem());
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
			setGraphic(graphic);
			setTooltip(new Tooltip(item.getQuery()));
		} else {
			setGraphic(null);
			setTooltip(null);
		}
	}
}
