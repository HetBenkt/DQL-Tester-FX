package nl.bos.controllers;

import static nl.bos.Constants.QUERIES;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fxmisc.richtext.CodeArea;
import org.json.JSONArray;
import org.json.JSONObject;
import org.reactfx.Subscription;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfAttr;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import nl.bos.AttributeTableColumn;
import nl.bos.Repository;
import nl.bos.beans.HistoryItem;
import nl.bos.contextmenu.ContextMenuOnResultTable;
import nl.bos.utils.Calculations;
import nl.bos.utils.Controllers;
import nl.bos.utils.DQLSyntax;
import nl.bos.utils.HistoryListCell;
import nl.bos.utils.Resources;

public class QueryWithResult {
	private static final Logger LOGGER = Logger.getLogger(QueryWithResult.class.getName());

	private ContextMenuOnResultTable contextMenuOnResultTable;

	private JSONObject jsonObject;

	private FXMLLoader connectionWithStatusFxmlLoader;
	private String[] parsedDescription;

	@FXML
	private ComboBox<HistoryItem> historyStatements;
	@FXML
	private ComboBox<HistoryItem> favoriteStatements;

	private ObservableList<HistoryItem> historyItems;
	@FXML
	private VBox queryWithResultBox;
	@FXML
	private CodeArea statement;
	@FXML
	private TableView result;

	private Instant start;

	private Subscription subscribeToText;

	ComboBox<HistoryItem> getHistoryStatements() {
		return historyStatements;
	}

	CodeArea getStatement() {
		return statement;
	}

	JSONObject getJsonObject() {
		return jsonObject;
	}

	FXMLLoader getConnectionWithStatusFxmlLoader() {
		return connectionWithStatusFxmlLoader;
	}

	public Instant getStart() {
		return start;
	}

	public TableView getResult() {
		return result;
	}

	public void cleanStart() {
		this.start = null;
	}

	@FXML
	private void initialize() {
		Controllers.put(this.getClass().getSimpleName(), this);

		contextMenuOnResultTable = new ContextMenuOnResultTable(result);
		result.getSelectionModel().setCellSelectionEnabled(true);
		result.addEventHandler(MouseEvent.MOUSE_CLICKED, contextMenuOnResultTable::onRightMouseClick);
		result.getSortOrder().addListener((InvalidationListener) change -> {
			start = Instant.now();
			LOGGER.info("Start..." + Instant.now());
		});
		loadConnectionWithStatusFxml();

		subscribeToText = statement.multiPlainChanges().successionEnds(Duration.ofMillis(500))
				.subscribe(ignore -> statement.setStyleSpans(0, DQLSyntax.computeHighlighting(statement.getText())));
		Resources.initHistoryFile();

		loadHistory();
	}

	private void onStatementsSelection(HistoryItem newValue, ComboBox<HistoryItem> combo) {
		if (newValue != null && combo.isShowing()) {
			statement.replaceText(0, statement.getLength(), newValue.getQuery());
			LOGGER.log(Level.INFO, "Statement selected");
			//combo.hide();
		} else {
			// for some reasons, statement selection is called multiple times, the second
			// and fourth with empty values...
			// statement.replaceText(0, statement.getLength(),"");
			LOGGER.log(Level.INFO, "No statement selected");
		}
	}

	private void loadConnectionWithStatusFxml() {
		Resources resources = new Resources();
		BorderPane connectionWithStatus = (BorderPane) resources.loadFXML("/nl/bos/views/ConnectionWithStatus.fxml");
		connectionWithStatusFxmlLoader = resources.getFxmlLoader();
		queryWithResultBox.getChildren().add(connectionWithStatus);
	}

	private void loadHistory() {
		String history = convertFileToString();
		historyItems = FXCollections.observableList(makeListFrom(history));
		setHistoryItems(historyItems);
		setFavoriteItems(historyItems);
	}

	private String convertFileToString() {
		String history = new String(Resources.readHistoryJsonBytes(), StandardCharsets.UTF_8);
		jsonObject = new JSONObject(history);
		return history;
	}

	private List<HistoryItem> makeListFrom(String history) {
		JSONArray historyQueries = (JSONArray) new JSONObject(history).get(QUERIES);

		List<HistoryItem> histItems = new ArrayList<>();
		for (int i = 0; i < historyQueries.length(); i++) {
			histItems.add(historyItemFromJsonObject(historyQueries.getJSONObject(i)));
		}

		return histItems;
	}

	private void setHistoryItems(List<HistoryItem> statements) {
		addFilterToComboBox(historyItems, historyStatements);
	}

	@SuppressWarnings("unchecked")
	private void addFilterToComboBox(ObservableList<HistoryItem> allItems, ComboBox<HistoryItem> combo) {
		FilteredList<HistoryItem> filteredItems = allItems.filtered(p -> true);
		combo.setCellFactory((ListView<HistoryItem> listView) -> new HistoryListCell(null));
		combo.setButtonCell(new HistoryListCell(null));
		HistoryItem[] selectedValue = new HistoryItem[1];
		// filter the event that will select the current value (and close the combo) on
		// key SPACE
		ComboBoxListViewSkin<HistoryItem> comboBoxListViewSkin = new ComboBoxListViewSkin<HistoryItem>(combo);
		comboBoxListViewSkin.getPopupContent().addEventFilter(KeyEvent.ANY, (event) -> {
			if (event.getCode() == KeyCode.SPACE) {
				event.consume();
			}
		});
		// we don't hide the list on click to be able to handle click event on favorite/delete
		comboBoxListViewSkin.setHideOnClick(false);
		combo.setSkin(comboBoxListViewSkin);

		// only enable the editor when the drop down list is shown
		combo.showingProperty().addListener((obs, oldValue, newValue) -> {
			if (!oldValue && newValue) {
				// reset the editor value every time we show the drop down
				LOGGER.log(Level.INFO, "show listener");
				combo.setEditable(true);
				((ListView<HistoryItem>)comboBoxListViewSkin.getPopupContent()).scrollTo(combo.getValue());
				combo.getEditor().clear();
				selectedValue[0] = null;
			} else if (!newValue && oldValue) {
				LOGGER.log(Level.INFO, "hide listener");
				combo.setEditable(false);
				HistoryItem value = combo.getValue();
                if(value != null) {
                	selectedValue[0] = value;
                }

                combo.setEditable(false);
                if(value != null) {
                Platform.runLater(() ->
                {
                    combo.getSelectionModel().select(selectedValue[0]);
                    combo.setValue(selectedValue[0]);
                    //statement.replaceText(0, statement.getLength(), selectedValue[0].getQuery());
                });				
                }
			}
			
		});
		// on editor change, update the filtered list predicate
		combo.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
			if (!combo.isShowing()) {
				LOGGER.log(Level.INFO, "Text on not showing");
				return;
			}
			LOGGER.log(Level.INFO, "Text listener");
			Platform.runLater(() -> {
				filteredItems.setPredicate(item -> {
					// We return true for any items that contains the input.
					if (item == null) {
						return true;
					}
					return item.getQuery().toUpperCase().contains(newValue.toUpperCase());
				});
			});
		});
		
		combo.setItems(filteredItems);
		combo.valueProperty().addListener(
				(observableValue, oldValue, newValue) -> onStatementsSelection(newValue, combo));
	}

	private void setFavoriteItems(List<HistoryItem> statements) {
		ObservableList<HistoryItem> favoritesItems = FXCollections
				.observableArrayList(item -> new ObservableValue[] { item.favoriteProperty() });
		favoritesItems.addAll(historyItems);
		FilteredList<HistoryItem> favItems = favoritesItems.filtered(p -> p.isFavorite());

		addFilterToComboBox(favItems, favoriteStatements);
	}

	private HistoryItem historyItemFromJsonObject(JSONObject jsonObject) {
		String query = jsonObject.getString("query");
		String category = jsonObject.getString("category");
		boolean isFavorite = jsonObject.getBoolean("favorite");

		HistoryItem historyItem = new HistoryItem(query);
		historyItem.setCategory(category);
		historyItem.setFavorite(isFavorite);

		return historyItem;
	}

	public void handleDeleteHistoryItem(HistoryItem item) {
		int selectedIndex = historyItems.indexOf(item);
		historyItems.remove(item);

		JSONArray queries = (JSONArray) jsonObject.get(QUERIES);
		queries.remove(selectedIndex);
		Resources.writeJsonDataToJsonHistoryFile(jsonObject);
	}

	public void handleFavoriteHistoryItem(HistoryItem item, boolean isFavorite) {
		if (item != null) {
			item.setFavorite(isFavorite);

			updateJSONData(item);
			Resources.writeJsonDataToJsonHistoryFile(jsonObject);
			// reloadHistory();
		}
	}

	private void updateJSONData(HistoryItem selectedItem) {
		int selectedIndex = historyItems.indexOf(selectedItem);
		JSONArray queries = (JSONArray) jsonObject.get("queries");
		queries.put(selectedIndex, new JSONObject(selectedItem));
	}

	/**
	 * @noinspection unchecked
	 */
	int updateResultTable(IDfCollection collection) throws DfException {
		result.getItems().clear();
		result.getColumns().clear();

		List<AttributeTableColumn> columns = new ArrayList<>();
		ObservableList<ObservableList> rows = FXCollections.observableArrayList();

		int rowCount = 0;
		while (collection.next()) {
			rowCount++;
			ObservableList<String> row = FXCollections.observableArrayList();
			for (int i = 0; i < collection.getAttrCount(); i++) {
				IDfAttr attr = collection.getAttr(i);

				if (rowCount == 1) {
					final int j = i;
					AttributeTableColumn column = new AttributeTableColumn(attr.getName());
					column.setAttr(attr);
					column.setCellValueFactory(
							(Callback<AttributeTableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(
									param.getValue().get(j).toString()));
					columns.add(column);
				}

				switch (attr.getDataType()) {
				case IDfAttr.DM_BOOLEAN:
					row.add(String.valueOf(collection.getBoolean(attr.getName())));
					break;
				case IDfAttr.DM_DOUBLE:
					row.add(String.valueOf(collection.getDouble(attr.getName())));
					break;
				case IDfAttr.DM_ID:
					row.add(String.valueOf(collection.getId(attr.getName())));
					break;
				case IDfAttr.DM_INTEGER:
					row.add(String.valueOf(collection.getInt(attr.getName())));
					break;
				case IDfAttr.DM_STRING:
					row.add(String.valueOf(collection.getString(attr.getName())));
					break;
				case IDfAttr.DM_TIME:
					row.add(String.valueOf(collection.getTime(attr.getName())));
					break;
				default:
					LOGGER.finest("Error occurred while displaying the results.");
					row.add("N/A");
					break;
				}
			}
			rows.add(row);
		}
		result.getColumns().addAll(columns);
		result.setItems(rows);

		return rowCount;
	}

	/**
	 * @noinspection unchecked
	 */
	public int updateResultTableWithStringInput(String description, List<String> columnNames) {
		contextMenuOnResultTable.getMenuItemShowPropertiesAction().setDescription(description);

		result.getItems().clear();
		result.getColumns().clear();

		List<AttributeTableColumn> columns = new ArrayList<>();
		ObservableList<ObservableList> rows = FXCollections.observableArrayList();

		int rowCount = 0;

		while (rowCount < getRowSize(description)) {
			rowCount++;
			ObservableList<String> row = FXCollections.observableArrayList();
			for (int i = 0; i < columnNames.size(); i++) {

				if (rowCount == 1) {
					final int j = i;
					AttributeTableColumn column = new AttributeTableColumn(columnNames.get(j));
					column.setCellValueFactory(
							(Callback<AttributeTableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(
									param.getValue().get(j).toString()));
					columns.add(column);
				}

				row.add(getRowValue(rowCount - 1, i));
			}
			rows.add(row);
		}
		result.getColumns().addAll(columns);
		result.setItems(rows);

		return rowCount;
	}

	private int getRowSize(String description) {
		String fromColumns;
		String type = description.substring(0, description.indexOf('\t'));

		if (type.contains("Table")) {
			fromColumns = description.substring(description.indexOf("Columns:"));
		} else {
			fromColumns = description.substring(description.indexOf("Attributes:"));
		}

		String columnsInfo = fromColumns.substring(0, fromColumns.indexOf("\r\n"));
		String value = columnsInfo.substring(columnsInfo.indexOf(':') + 1).trim();

		parseDescription(description);

		return Integer.parseInt(value);
	}

	private String getRowValue(int rowIndex, int columnIndex) {
		String rowValue = "";

		try {
			rowValue = parsedDescription[(rowIndex * 3) + columnIndex];

		} catch (ArrayIndexOutOfBoundsException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		return rowValue;
	}

	private void parseDescription(String descriptionInput) {
		String[] split;
		String type = descriptionInput.substring(0, descriptionInput.indexOf('\t'));

		if (type.contains("Table")) {
			split = descriptionInput.substring(descriptionInput.indexOf("\r\n", descriptionInput.indexOf("Columns:")))
					.replace("KEYED\r\n", " KEYED ").replace("\r\n", " NOT_KEYED ").replace(" NOT_KEYED", " FALSE")
					.replace("KEYED", " TRUE").split(" ");
		} else {
			split = descriptionInput
					.substring(descriptionInput.indexOf("\r\n", descriptionInput.indexOf("Attributes:")))
					.replace("REPEATING\r\n", " REPEATING ").replace("\r\n", " NOT_REPEATING ")
					.replace(" NOT_REPEATING", " FALSE").replace("REPEATING", " TRUE").split(" ");
		}

		split[0] = "";
		split[1] = "";
		split[2] = "";
		split[3] = "";
		parsedDescription = Arrays.stream(split).filter(value -> !value.equals("")).toArray(String[]::new);
	}

	public void executeQuery(String query) {
		ConnectionWithStatus connectionWithStatusController = (ConnectionWithStatus) Controllers
				.get(ConnectionWithStatus.class.getSimpleName());

		Instant start = Instant.now();
		IDfCollection collection = Repository.getInstance().query(query);
		Instant end = Instant.now();
		connectionWithStatusController.getTimeQuery().setText(Calculations.getDurationInSeconds(start, end));

		if (collection == null) {
			return;
		}

		try {
			Instant startList = Instant.now();
			int rowCount = updateResultTable(collection);
			Instant endList = Instant.now();
			connectionWithStatusController.getTimeList().setText(Calculations.getDurationInSeconds(startList, endList));
			connectionWithStatusController.getResultCount().setText(String.valueOf(rowCount));

			collection.close();
		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, String.format("Error running query: [%s]", query), e);
		}
	}

	public void appendNewQueryToHistory(String statement, JSONObject jsonObject) {
		if (statementNotExists(historyItems, statement)) {
			HistoryItem historyItem = new HistoryItem(statement);
			historyItems.add(0, historyItem);
			historyStatements.setValue(historyItem);
			JSONArray queries = (JSONArray) jsonObject.get("queries");
			if (queries.length() > 0) {
				queries.put(queries.get(0));
			}
			queries.put(0, new JSONObject(historyItem));

			Resources.writeJsonDataToJsonHistoryFile(jsonObject);
		}
	}

	private boolean statementNotExists(ObservableList<HistoryItem> items, String statement) {
		for (HistoryItem item : items) {
			if (item.getQuery().equalsIgnoreCase(statement))
				return false;
		}
		return true;
	}
}
