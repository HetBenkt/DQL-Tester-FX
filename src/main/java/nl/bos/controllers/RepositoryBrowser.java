package nl.bos.controllers;

import static nl.bos.Constants.ATTR_ACL_NAME;
import static nl.bos.Constants.ATTR_A_CONTENT_TYPE;
import static nl.bos.Constants.ATTR_IS_PRIVATE;
import static nl.bos.Constants.ATTR_OWNER_PERMIT;
import static nl.bos.Constants.ATTR_R_CONTENT_SIZE;
import static nl.bos.Constants.ATTR_R_CREATION_DATE;
import static nl.bos.Constants.ATTR_R_LOCK_DATE;
import static nl.bos.Constants.ATTR_R_LOCK_MACHINE;
import static nl.bos.Constants.ATTR_R_LOCK_OWNER;
import static nl.bos.Constants.ATTR_R_MODIFY_DATE;
import static nl.bos.Constants.ATTR_R_VERSION_LABEL;
import static nl.bos.Constants.TYPE_CABINET;
import static nl.bos.Constants.TYPE_DOCUMENT;
import static nl.bos.Constants.TYPE_REPOSITORY;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.documentum.fc.client.DfACL;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.bos.BrowserTreeItem;
import nl.bos.Constants;
import nl.bos.Repository;
import nl.bos.utils.AppAlert;
import nl.bos.utils.Resources;

public class RepositoryBrowser implements ChangeListener<TreeItem<BrowserTreeItem>> {
	private static final Logger LOGGER = Logger.getLogger(RepositoryBrowser.class.getName());

	private final Repository repository = Repository.getInstance();

	@FXML
	private TreeView<BrowserTreeItem> treeView;
	@FXML
	private TextField txtObjectId;
	@FXML
	private TextField txtObjectType;
	@FXML
	private TextField txtContentType;
	@FXML
	private TextField txtContentSize;
	@FXML
	private TextField txtCreationDate;
	@FXML
	private TextField txtModifyDate;
	@FXML
	private TextField txtLockOwner;
	@FXML
	private TextField txtLockMachine;
	@FXML
	private TextField txtLockDate;
	@FXML
	private TextField txtAclName;
	@FXML
	private TextField txtPermission;
	@FXML
	private TextField txtVersion;
	@FXML
	private Button btnExit;
	@FXML
	private Label lblNrOfItems;
	@FXML
	private CheckBox ckbShowAllCabinets;
	@FXML
	private CheckBox ckbShowAllVersions;
	@FXML
	private VBox vbox;

	private BrowserTreeItem rootItem;
	private MyTreeNode selected;
	private final ContextMenu rootContextMenu = new ContextMenu();
	private Resources resources = new Resources();

	private MenuItem miVersions;
	private MenuItem miRenditions;

	private MenuItem miOpenContent;
	private MenuItem miExportContent;
	private MenuItem miCheckout;
	private MenuItem miCheckin;
	private MenuItem miCancelCheckout;

	private Image imgLockedDocument = new Image(
			resources.getResourceStream("nl/bos/icons/type/t_dm_document_lock_16.gif"));

	@FXML
	private void initialize() {
		vbox.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressEvent);
		ckbShowAllCabinets.setSelected(Resources.isBrowserAllCabinet());
		initContextMenu();
		initBrowserTree();
	}

	private void handleKeyPressEvent(KeyEvent keyEvent) {
		LOGGER.finest(String.format("Keycode = %s", keyEvent.getCode()));

		if (keyEvent.getCode() == KeyCode.F3) {
			triggerFindItem(null);
		}
	}

	private void initContextMenu() {
		MenuItem miDump = new MenuItem("Get Attributes");
		miDump.setOnAction(this::triggerGetAttributes);

		miOpenContent = new MenuItem("Open Content");
		miOpenContent.setDisable(true);
		miOpenContent.setOnAction(this::triggerOpenContent);

		miExportContent = new MenuItem("Export Content");
		miExportContent.setDisable(true);
		miExportContent.setOnAction(this::triggerExportContent);

		miCheckout = new MenuItem("Check Out Document");
		miCheckout.setDisable(true);
		miCheckout.setOnAction(this::triggerCheckout);

		miCheckin = new MenuItem("Check In Document");
		miCheckin.setDisable(true);
		miCheckin.setOnAction(this::triggerCheckin);

		miCancelCheckout = new MenuItem("Cancel Checkout");
		miCancelCheckout.setDisable(true);
		miCancelCheckout.setOnAction(this::triggerCancelCheckout);

		miVersions = new MenuItem("Versions");
		miVersions.setDisable(true);
		miVersions.setOnAction(this::triggerVersions);

		miRenditions = new MenuItem("Renditions");
		miRenditions.setDisable(true);
		miRenditions.setOnAction(this::triggerRenditions);

		MenuItem miFindItem = new MenuItem("Find item <F3>");
		miFindItem.setOnAction(this::triggerFindItem);

		rootContextMenu.getItems().addAll(miOpenContent, new SeparatorMenuItem(), miCheckout, miCheckin,
				miCancelCheckout, new SeparatorMenuItem(), miDump, new SeparatorMenuItem(), miExportContent,
				new SeparatorMenuItem(), miVersions, miRenditions, new SeparatorMenuItem(), miFindItem);
	}

	private void triggerGetAttributes(ActionEvent actionEvent) {
		String selectedId = repository.getIdFromObject(selected.getValue().getObject());
		LOGGER.info(selectedId);

		Stage dumpAttributes = new Stage();
		dumpAttributes.setTitle(String.format("Attributes List - %s (%s)", selectedId, repository.getRepositoryName()));

		VBox loginPane = (VBox) resources.loadFXML("/nl/bos/views/GetAttributes.fxml");
		Scene scene = new Scene(loginPane);
		dumpAttributes.setScene(scene);

		GetAttributes controller = resources.getFxmlLoader().getController();
		controller.dumpObject(selectedId);
		dumpAttributes.showAndWait();
	}

	private void triggerOpenContent(ActionEvent actionEvent) {
		try {
			String path = repository.downloadContent((IDfSysObject) selected.getValue().getObject());
			if (Desktop.isDesktopSupported()) {
				Resources.openFile(new File(path));
			}
		} catch (DfException e) {
			AppAlert.error("Error during opening content", e.getMessage());
		}
	}

	private void triggerExportContent(ActionEvent actionEvent) {
		try {
			repository.downloadContent((IDfSysObject) selected.getValue().getObject());
		} catch (DfException e) {
			AppAlert.error("Error during export content", e.getMessage());
		}
	}

	private void triggerCheckout(ActionEvent actionEvent) {
		try {
			repository.checkoutContent((IDfSysObject) selected.getValue().getObject());
			selected.setGraphic(new ImageView(imgLockedDocument));
		} catch (DfException e) {
			AppAlert.error("Error during checkout", e.getMessage());
		}

	}

	private void triggerCancelCheckout(ActionEvent actionEvent) {
		try {
			repository.cancelCheckout((IDfSysObject) selected.getValue().getObject());
			((MyTreeNode) selected.getParent()).refresh();
		} catch (DfException e) {
			AppAlert.error("Error during cancel checkout", e.getMessage());

		}
	}

	private void triggerCheckin(ActionEvent actionEvent) {
		String selectedId = repository.getIdFromObject(selected.getValue().getObject());
		LOGGER.info("Checkin " + selectedId);
		Stage checkinStage = new Stage();
		checkinStage.setTitle("Checkin");
		Resources resources = new Resources();
		VBox checkinDialog = (VBox) resources.loadFXML("/nl/bos/views/dialogs/CheckinDialog.fxml");
		Scene scene = new Scene(checkinDialog);
		checkinStage.setScene(scene);
		CheckinDialog controller = resources.getFxmlLoader().getController();
		controller.setStage(checkinStage);
		controller.initialize();
		controller.checkinDialog(selectedId);
		checkinStage.showAndWait();
		((MyTreeNode) selected.getParent()).refresh();
	}

	private void triggerRenditions(ActionEvent actionEvent) {
		showResultTable("Renditions");
	}

	private void triggerVersions(ActionEvent actionEvent) {
		showResultTable("Versions");
	}

	private void showResultTable(String label) {
		String id = repository.getIdFromObject(selected.getValue().getObject());
		LOGGER.info(id);

		Stage resultStage = new Stage();
		resultStage.setTitle(
				String.format("%s - %s (%s)", label, repository.getObjectName(id), repository.getRepositoryName()));

		VBox resultPane = (VBox) resources.loadFXML("/nl/bos/views/ResultTable.fxml");
		Scene scene = new Scene(resultPane);
		resultStage.setScene(scene);

		ResultTable controller = resources.getFxmlLoader().getController();
		controller.loadResult(id);
		resultStage.showAndWait();
	}

	private void initBrowserTree() {
		rootItem = new BrowserTreeItem(null, repository.getRepositoryName(), TYPE_REPOSITORY, "");
		TreeItem<BrowserTreeItem> treeItemBrowser = buildTreeItemBrowser(rootItem);
		treeItemBrowser.setExpanded(true);
		treeView.setRoot(treeItemBrowser);
		treeView.getSelectionModel().selectedItemProperty().addListener(this);

		treeView.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleContextMenu);
	}

	private void handleContextMenu(MouseEvent mouseEvent) {
		LOGGER.finest(String.format("Click-count: %s", String.valueOf(mouseEvent.getClickCount())));

		selected = (MyTreeNode) treeView.getSelectionModel().getSelectedItem();

		if (selected != null && !selected.isExpanded()) {
			selected.isFirstTimeChildren = true;
		}

		if (mouseEvent.getButton() != MouseButton.SECONDARY) {
			rootContextMenu.hide();
			return;
		}

		selected = (MyTreeNode) treeView.getSelectionModel().getSelectedItem();
		// item is selected - this prevents fail when clicking on empty space
		if (selected != null && !selected.getValue().getType().equals(TYPE_REPOSITORY)) {
			// open context contextmenu on current screen position
			IDfPersistentObject selectedObject = selected.getValue().getObject();
			boolean isDocumentType = repository.isDocumentType(selectedObject);
			miOpenContent.setDisable(!isDocumentType);
			miExportContent.setDisable(!isDocumentType);
			miCheckout.setDisable(!repository.canCheckOut(selectedObject));
			miCancelCheckout.setDisable(!repository.isCheckedOut(selectedObject));
			miCheckin.setDisable(!repository.isCheckedOut(selectedObject));
			miVersions.setDisable(!isDocumentType);
			miRenditions.setDisable(!isDocumentType);
			rootContextMenu.show(treeView, mouseEvent.getScreenX(), mouseEvent.getScreenY());
		}
	}

	private String showSearchPopup() {
		Optional<String> findTreeItem = AppAlert.confirmationWithPanelAndResponse("Find Tree Item", "Object ID:");
		return findTreeItem.orElse("");
	}

	private void searchForTreeItem(String searchId) {
		if (repository.isObjectId(searchId)) {
			AppAlert.error("No object ID", "The given input is not a valid object ID");
			return;
		}

		try {
			IDfSysObject objectToBeFound = (IDfSysObject) repository.getObjectById(searchId);

			if (objectToBeFound == null) {
				AppAlert.error("No object found", "No object found for the given object ID");
				return;
			}

			if (!objectToBeFound.getHasFolder()) {
				return;
			}

			List<IDfId> ancestorIds = getAncestorList(objectToBeFound);

			TreeItem<BrowserTreeItem> root = treeView.getRoot();

			while (!ancestorIds.isEmpty()) {
				int ancestorCount = ancestorIds.size();
				ObservableList<TreeItem<BrowserTreeItem>> children = root.getChildren();

				for (TreeItem<BrowserTreeItem> child : children) {
					IDfId childId = child.getValue().getObject().getObjectId();

					if (ancestorIds.contains(childId)) {
						treeView.getSelectionModel().select(child);
						treeView.scrollTo(treeView.getSelectionModel().getSelectedIndex());
						root = child;
						ancestorIds.remove(childId);
					}
				}

				if (ancestorCount == ancestorIds.size()) {
					LOGGER.warning("Could not find full path in browser tree!");
					break;
				}
			}

		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private List<IDfId> getAncestorList(IDfSysObject objectToBeFound) throws DfException {
		List<IDfId> ancestorIds = new ArrayList<>();
		ancestorIds.add(objectToBeFound.getObjectId());

		IDfFolder folderToBeFound = (IDfFolder) repository.getObjectById(objectToBeFound.getFolderId(0).getId());
		for (int i = 0; i < folderToBeFound.getValueCount("i_ancestor_id"); i++) {
			ancestorIds.add(folderToBeFound.getRepeatingId("i_ancestor_id", i));
		}

		return ancestorIds;
	}

	private TreeItem<BrowserTreeItem> buildTreeItemBrowser(BrowserTreeItem treeItem) {
		return createNode(treeItem);
	}

	// This method creates a TreeItem to represent the given File. It does this
	// by overriding the TreeItem.getChildren() and TreeItem.isLeaf() methods
	// anonymously, but this could be better abstracted by creating a
	// 'FileTreeItem' subclass of TreeItem. However, this is left as an exercise
	// for the reader.
	private TreeItem<BrowserTreeItem> createNode(final BrowserTreeItem treeItem) {
		Image image = new Image(
				resources.getResourceStream(String.format("nl/bos/icons/type/t_%s_16.gif", treeItem.getType())));
		try {
			if (treeItem.getType().equals(TYPE_CABINET)) {
				boolean isPrivate = treeItem.getObject().getBoolean(ATTR_IS_PRIVATE);
				if (isPrivate)
					image = new Image(resources.getResourceStream("nl/bos/icons/type/t_mycabinet_16.gif"));
			} else if (treeItem.getType().equals(TYPE_DOCUMENT)) {
				String lockOwner = treeItem.getObject().getString(Constants.ATTR_R_LOCK_OWNER);
				if (!lockOwner.equals(""))
					image = imgLockedDocument;
			}

		} catch (DfException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		ImageView imageView = new ImageView(image);
		return new MyTreeNode(treeItem, imageView);
	}

	@Override
	public void changed(ObservableValue<? extends TreeItem<BrowserTreeItem>> observable,
			TreeItem<BrowserTreeItem> oldValue, TreeItem<BrowserTreeItem> newValue) {
		BrowserTreeItem selectedItem = newValue.getValue();
		LOGGER.info(String.format("Selected item: %s", selectedItem.getName()));
		IDfPersistentObject selectedObject = selectedItem.getObject();
		if (selectedObject != null) {
			try {
				txtObjectId.setText(selectedObject.getObjectId().getId());
				txtObjectType.setText(selectedObject.getType().getName());
				txtContentType.setText(selectedObject.getString(ATTR_A_CONTENT_TYPE));
				txtContentSize.setText(selectedObject.getString(ATTR_R_CONTENT_SIZE));
				txtCreationDate.setText(selectedObject.getTime(ATTR_R_CREATION_DATE).asString(""));
				txtModifyDate.setText(selectedObject.getTime(ATTR_R_MODIFY_DATE).asString(""));
				txtLockOwner.setText(selectedObject.getString(ATTR_R_LOCK_OWNER));
				txtLockMachine.setText(selectedObject.getString(ATTR_R_LOCK_MACHINE));
				txtLockDate.setText(selectedObject.getTime(ATTR_R_LOCK_DATE).asString(""));
				txtAclName.setText(selectedObject.getString(ATTR_ACL_NAME));
				txtPermission.setText(convertPermitToLabel(selectedObject.getInt(ATTR_OWNER_PERMIT)));
				txtVersion.setText(getRepeatingValue(selectedObject));
			} catch (DfException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		} else {
			txtObjectId.setText("");
			txtObjectType.setText("");
			txtContentType.setText("");
			txtContentSize.setText("");
			txtCreationDate.setText("");
			txtModifyDate.setText("");
			txtLockOwner.setText("");
			txtLockMachine.setText("");
			txtLockDate.setText("");
			txtAclName.setText("");
			txtPermission.setText("");
			txtVersion.setText("");
		}
	}

	private String getRepeatingValue(IDfPersistentObject object) throws DfException {
		int count = object.getValueCount(ATTR_R_VERSION_LABEL);
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < count; i++) {
			result.append(object.getRepeatingString(ATTR_R_VERSION_LABEL, i));
			if (i != count - 1)
				result.append(", ");
		}
		return String.valueOf(result);
	}

	private String convertPermitToLabel(int permit) {
		if (permit == DfACL.DF_PERMIT_NONE)
			return DfACL.DF_PERMIT_NONE_STR;
		if (permit == DfACL.DF_PERMIT_BROWSE)
			return DfACL.DF_PERMIT_BROWSE_STR;
		if (permit == DfACL.DF_PERMIT_READ)
			return DfACL.DF_PERMIT_READ_STR;
		if (permit == DfACL.DF_PERMIT_RELATE)
			return DfACL.DF_PERMIT_RELATE_STR;
		if (permit == DfACL.DF_PERMIT_VERSION)
			return DfACL.DF_PERMIT_VERSION_STR;
		if (permit == DfACL.DF_PERMIT_WRITE)
			return DfACL.DF_PERMIT_WRITE_STR;
		if (permit == DfACL.DF_PERMIT_DELETE)
			return DfACL.DF_PERMIT_DELETE_STR;
		return "";
	}

	@FXML
	private void handleExit(ActionEvent actionEvent) {
		LOGGER.info(String.valueOf(actionEvent.getSource()));
		Stage stage = (Stage) btnExit.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void handleShowAllCabinets(ActionEvent actionEvent) {
		TreeItem<BrowserTreeItem> treeItemBrowser = buildTreeItemBrowser(rootItem);
		treeItemBrowser.setExpanded(true);
		treeView.setRoot(treeItemBrowser);
		Resources.setBrowserAllCabinet(ckbShowAllCabinets.isSelected());

	}

	private void triggerFindItem(ActionEvent actionEvent) {
		String searchId = showSearchPopup();
		if (!searchId.isEmpty())
			searchForTreeItem(searchId);
	}

	private class MyTreeNode extends TreeItem<BrowserTreeItem> {
		// We cache whether the File is a leaf or not. A File is a leaf if
		// it is not a directory and does not have any files contained within
		// it. We cache this as isLeaf() is called often, and doing the
		// actual check on File is expensive.
		private boolean isLeaf;

		// We do the children and leaf testing only once, and then set these
		// booleans to false so that we do not check again during this
		// run. A more complete implementation may need to handle more
		// dynamic file system situations (such as where a folder has files
		// added after the TreeView is shown). Again, this is left as an
		// exercise for the reader.
		private boolean isFirstTimeChildren;
		private boolean isFirstTimeLeaf;

		private MyTreeNode(BrowserTreeItem treeItem, ImageView imageView) {
			super(treeItem, imageView);
			isFirstTimeChildren = true;
			isFirstTimeLeaf = true;
		}

		@Override
		public ObservableList<TreeItem<BrowserTreeItem>> getChildren() {
			if (isFirstTimeChildren) {
				isFirstTimeChildren = false;

				// First getChildren() call, so we actually go off and
				// determine the children of the File contained in this TreeItem.
				super.getChildren().setAll(buildChildren(this));
			}
			return super.getChildren();
		}

		public void refresh() {
			this.getChildren().setAll(buildChildren(this));
		}

		@Override
		public boolean isLeaf() {
			if (isFirstTimeLeaf) {
				isFirstTimeLeaf = false;
				BrowserTreeItem treeItem = getValue();
				isLeaf = !treeItem.isDirectory();
			}
			return isLeaf;
		}

		private ObservableList<TreeItem<BrowserTreeItem>> buildChildren(MyTreeNode parent) {
			BrowserTreeItem parentItem = parent.getValue();
			if (parentItem != null && parentItem.isDirectory()) {
				List<BrowserTreeItem> treeItems = parentItem.listObjects(parentItem, ckbShowAllCabinets.isSelected(),
						ckbShowAllVersions.isSelected());
				lblNrOfItems.setText(String.format("%s items found", treeItems.size()));
				ObservableList<TreeItem<BrowserTreeItem>> children = FXCollections.observableArrayList();
				for (BrowserTreeItem treeItem : treeItems) {
					children.add(createNode(treeItem));
				}
				return children;
			}
			return FXCollections.emptyObservableList();
		}
	}
}
