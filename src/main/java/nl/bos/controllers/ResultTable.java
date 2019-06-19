package nl.bos.controllers;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.common.DfException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nl.bos.Repository;
import nl.bos.beans.RenditionObject;
import nl.bos.beans.VersionObject;
import nl.bos.contextmenu.menuitem.action.MenuItemDestroyObjectAction;
import nl.bos.contextmenu.menuitem.action.MenuItemExportContentAction;
import nl.bos.contextmenu.menuitem.action.MenuItemGetAttributesAction;
import nl.bos.contextmenu.menuitem.action.MenuItemOpenContentAction;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultTable {
    private static final Logger LOGGER = Logger.getLogger(ResultTable.class.getName());

    private final Repository repository = Repository.getInstance();

    private final ContextMenu contextMenu = new ContextMenu();
    private MenuItem getAttributes;
    private MenuItem destroyObject;

    private MenuItem openContent;
    private MenuItem exportContent;
    private MenuItem importContent;
    private MenuItem removeContent;

    @FXML
    private Button btnOk;
    @FXML
    private TableView tvResults;
    @FXML
    private TextField txtCount;


    @FXML
    private void initialize() {

        getAttributes = new MenuItem("Get Attributes");
        getAttributes.setDisable(true);
        new MenuItemGetAttributesAction(getAttributes, tvResults);

        destroyObject = new MenuItem("Destroy Object");
        destroyObject.setDisable(true);
        new MenuItemDestroyObjectAction(destroyObject, tvResults);

        openContent = new MenuItem("Open Content");
        openContent.setDisable(true);
        new MenuItemOpenContentAction(openContent, tvResults);

        exportContent = new MenuItem("Export Content");
        exportContent.setDisable(true);
        new MenuItemExportContentAction(exportContent, tvResults);

        importContent = new MenuItem("Import Content");
        importContent.setDisable(true);
        //new MenuItemImportContentAction(importContent, tvResults);

        removeContent = new MenuItem("Remove Content");
        removeContent.setDisable(true);
        //new MenuItemRemoveContentAction(removeContent, tvResults);

        contextMenu.getItems().addAll(openContent, destroyObject, importContent, exportContent, removeContent, new SeparatorMenuItem(), getAttributes);
    }

    public void loadResult(String id) {
        LOGGER.info(id);

        Stage stage = (Stage) btnOk.getScene().getWindow();
        if (stage.getTitle().contains("Versions")) {
            getVersions(id);
        } else {
            getRenditions(id);
        }
    }

    private void getVersions(String id) {
        TableColumn objectId = new TableColumn("r_object_id");
        objectId.setCellValueFactory(new PropertyValueFactory<>("objectId"));
        TableColumn objectName = new TableColumn("object_name");
        objectName.setCellValueFactory(new PropertyValueFactory<>("objectName"));
        TableColumn versionLabel = new TableColumn("r_version_label");
        versionLabel.setCellValueFactory(new PropertyValueFactory<>("versionLabel"));
        TableColumn chronicleId = new TableColumn("i_chronicle_id");
        chronicleId.setCellValueFactory(new PropertyValueFactory<>("chronicleId"));
        TableColumn contentsId = new TableColumn("i_contents_id");
        contentsId.setCellValueFactory(new PropertyValueFactory<>("contentsId"));
        TableColumn creationDate = new TableColumn("r_creation_date");
        creationDate.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        TableColumn modifyDate = new TableColumn("r_modify_date");
        modifyDate.setCellValueFactory(new PropertyValueFactory<>("modifyDate"));
        TableColumn contentType = new TableColumn("a_content_type");
        contentType.setCellValueFactory(new PropertyValueFactory<>("contentType"));

        tvResults.getColumns().addAll(objectId, objectName, versionLabel, chronicleId, contentsId, creationDate, modifyDate, contentType);

        tvResults.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onRightMouseClick);

        IDfPersistentObject object = repository.getObjectById(id);
        try {
            String chronicleObjectId = object.getString("i_chronicle_id");
            IDfCollection collection = repository.query(String.format("select r_object_id, object_name, r_version_label, i_chronicle_id, i_contents_id, r_creation_date, r_modify_date, a_content_type from dm_sysobject (ALL) where i_chronicle_id = '%s'", chronicleObjectId));
            int counter = 0;
            while (collection.next()) {
                counter++;
                VersionObject versionObject = new VersionObject();
                versionObject.setObjectId(collection.getString("r_object_id"));
                versionObject.setObjectName(collection.getString("object_name"));
                versionObject.setVersionLabel(collection.getString("r_version_label"));
                versionObject.setChronicleId(collection.getString("i_chronicle_id"));
                versionObject.setContentsId(collection.getString("i_contents_id"));
                versionObject.setCreationDate(collection.getString("r_creation_date"));
                versionObject.setModifyDate(collection.getString("r_modify_date"));
                versionObject.setContentType(collection.getString("a_content_type"));
                tvResults.getItems().add(versionObject);
            }
            txtCount.setText(String.valueOf(counter));
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void onRightMouseClick(MouseEvent t) {
        if (t.getButton() == MouseButton.PRIMARY) {
            contextMenu.hide();
        } else if (t.getButton() == MouseButton.SECONDARY) {
            validateMenuItems();
            contextMenu.show(tvResults, t.getScreenX(), t.getScreenY());
        }
    }

    private void validateMenuItems() {
        Stage stage = (Stage) btnOk.getScene().getWindow();

        String selectedCell = null;
        if (tvResults.getSelectionModel().getSelectedCells().size() > 0) {
            TablePosition focusedCell = (TablePosition) tvResults.getSelectionModel().getSelectedCells().get(0);
            Object cellData = focusedCell.getTableColumn().getCellData(focusedCell.getRow());
            selectedCell = String.valueOf(cellData);
        }

        openContent.setDisable(selectedCell == null || repository.isObjectId(selectedCell));
        destroyObject.setVisible(stage.getTitle().contains("Versions"));
        destroyObject.setDisable(selectedCell == null || repository.isObjectId(selectedCell));
        importContent.setVisible(stage.getTitle().contains("Renditions"));
        importContent.setDisable(true);
        exportContent.setDisable(selectedCell == null || repository.isObjectId(selectedCell));
        removeContent.setVisible(stage.getTitle().contains("Renditions"));
        removeContent.setDisable(true);
        getAttributes.setDisable(selectedCell == null || repository.isObjectId(selectedCell));
    }

    private void getRenditions(String id) {
        TableColumn objectId = new TableColumn("r_object_id");
        objectId.setCellValueFactory(new PropertyValueFactory<>("objectId"));
        TableColumn fullFormat = new TableColumn("full_format");
        fullFormat.setCellValueFactory(new PropertyValueFactory<>("fullFormat"));
        TableColumn contentSize = new TableColumn("content_size");
        contentSize.setCellValueFactory(new PropertyValueFactory<>("contentSize"));
        TableColumn fullContentSize = new TableColumn("full_content_size");
        fullContentSize.setCellValueFactory(new PropertyValueFactory<>("fullContentSize"));
        TableColumn rendition = new TableColumn("rendition");
        rendition.setCellValueFactory(new PropertyValueFactory<>("rendition"));
        TableColumn page = new TableColumn("page");
        page.setCellValueFactory(new PropertyValueFactory<>("page"));
        TableColumn storageId = new TableColumn("storage_id");
        storageId.setCellValueFactory(new PropertyValueFactory<>("storageId"));
        TableColumn setTime = new TableColumn("set_time");
        setTime.setCellValueFactory(new PropertyValueFactory<>("setTime"));
        TableColumn setFile = new TableColumn("set_file");
        setFile.setCellValueFactory(new PropertyValueFactory<>("setFile"));
        TableColumn setClient = new TableColumn("set_client");
        setClient.setCellValueFactory(new PropertyValueFactory<>("setClient"));
        TableColumn pageModifier = new TableColumn("page_modifier");
        pageModifier.setCellValueFactory(new PropertyValueFactory<>("pageModifier"));
        TableColumn dataTicket = new TableColumn("data_ticket");
        dataTicket.setCellValueFactory(new PropertyValueFactory<>("dataTicket"));

        tvResults.getColumns().addAll(objectId, fullFormat, contentSize, fullContentSize, rendition, page, storageId, setTime, setFile, setClient, pageModifier, dataTicket);

        tvResults.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onRightMouseClick);

        try {
            IDfCollection collection = repository.query(String.format("select r_object_id, full_format, content_size, full_content_size, rendition, page, storage_id, set_time, set_file, set_client, page_modifier, data_ticket from dmr_content where any parent_id = '%s'", id));
            int counter = 0;
            while (collection.next()) {
                counter++;
                RenditionObject renditionObject = new RenditionObject();
                renditionObject.setObjectId(collection.getString("r_object_id"));
                renditionObject.setFullFormat(collection.getString("full_format"));
                renditionObject.setContentSize(collection.getString("content_size"));
                renditionObject.setFullContentSize(collection.getString("full_content_size"));
                renditionObject.setRendition(collection.getString("rendition"));
                renditionObject.setPage(collection.getString("page"));
                renditionObject.setStorageId(collection.getString("storage_id"));
                renditionObject.setSetTime(collection.getString("set_time"));
                renditionObject.setSetFile(collection.getString("set_file"));
                renditionObject.setSetClient(collection.getString("set_client"));
                renditionObject.setPageModifier(collection.getString("page_modifier"));
                renditionObject.setDataTicket(collection.getString("data_ticket"));
                tvResults.getItems().add(renditionObject);
            }
            txtCount.setText(String.valueOf(counter));
        } catch (DfException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @FXML
    private void handleRefresh(ActionEvent actionEvent) {

    }

    @FXML
    private void handleOk(ActionEvent actionEvent) {
        Stage stage = (Stage) btnOk.getScene().getWindow();
        stage.close();
    }
}
