package tdlm.gui;

import java.io.IOException;
import java.time.LocalDate;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import tdlm.controller.ToDoListController;
import tdlm.data.DataManager;
import saf.ui.AppYesNoCancelDialogSingleton;
import saf.ui.AppMessageDialogSingleton;
import properties_manager.PropertiesManager;
import saf.ui.AppGUI;
import saf.AppTemplate;
import saf.components.AppWorkspaceComponent;
import static saf.settings.AppStartupConstants.FILE_PROTOCOL;
import static saf.settings.AppStartupConstants.PATH_IMAGES;
import static tdlm.PropertyType.ADD_ICON;
import static tdlm.PropertyType.ADD_ITEM_TOOLTIP;
import static tdlm.PropertyType.CATEGORY_COLUMN_HEADING;
import static tdlm.PropertyType.COMPLETED_COLUMN_HEADING;
import static tdlm.PropertyType.DESCRIPTION_COLUMN_HEADING;
import static tdlm.PropertyType.DETAILS_HEADING_LABEL;
import static tdlm.PropertyType.END_DATE_COLUMN_HEADING;
import static tdlm.PropertyType.ITEMS_HEADING_LABEL;
import static tdlm.PropertyType.MOVE_DOWN_ICON;
import static tdlm.PropertyType.MOVE_DOWN_ITEM_TOOLTIP;
import static tdlm.PropertyType.MOVE_UP_ICON;
import static tdlm.PropertyType.MOVE_UP_ITEM_TOOLTIP;
import static tdlm.PropertyType.NAME_PROMPT;
import static tdlm.PropertyType.OWNER_PROMPT;
import static tdlm.PropertyType.REMOVE_ICON;
import static tdlm.PropertyType.REMOVE_ITEM_TOOLTIP;
import static tdlm.PropertyType.START_DATE_COLUMN_HEADING;
import static tdlm.PropertyType.WORKSPACE_HEADING_LABEL;
import tdlm.data.ToDoItem;
import static tdlm.gui.WorkspaceConstants.*;

/**
 * This class serves as the workspace component for this application, providing
 * the user interface controls for editing work.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class Workspace extends AppWorkspaceComponent {
    
    // HERE'S THE APP
    AppTemplate app;

    // IT KNOWS THE GUI IT IS PLACED INSIDE
    AppGUI gui;
    
    // THIS CONTROLLER PROVIDES THE RESPONSES TO INTERACTIONS
    ToDoListController toDoListController;
    
    // THIS IS OUR WORKSPACE HEADING
    Label headingLabel;
    
    // THIS HAS OUR TODO LIST DETAILS
    VBox detailsBox;
    Label detailsLabel;
    GridPane nameAndOwnerBox;
    HBox nameBox;
    Label nameLabel;
    TextField nameTextField;
    HBox ownerBox;
    Label ownerLabel;
    TextField ownerTextField;

     // THIS REGION IS FOR MANAGING TODO ITEMS
    VBox itemsBox;
    Label itemsLabel;
    HBox itemsToolbar;
    Button addItemButton;
    Button removeItemButton;
    Button moveUpItemButton;
    Button moveDownItemButton;
    TableView<ToDoItem> itemsTable;
    TableColumn itemCategoryColumn;
    TableColumn itemDescriptionColumn;
    TableColumn itemStartDateColumn;
    TableColumn itemEndDateColumn;
    TableColumn itemCompletedColumn;

    // HERE ARE OUR DIALOGS
    AppMessageDialogSingleton messageDialog;
    AppYesNoCancelDialogSingleton yesNoCancelDialog;
    InputItemDialogSingleton inputItemDialog;
    RemoveItemDialogSingleton removeItemDialog;

    // FOR DISPLAYING DEBUG STUFF
    Text debugText;

    /**
     * Constructor for initializing the workspace, note that this constructor
     * will fully setup the workspace user interface for use.
     *
     * @param initApp The application this workspace is part of.
     *
     * @throws IOException Thrown should there be an error loading application
     * data for setting up the user interface.
     */
    public Workspace(AppTemplate initApp) throws IOException {
	// KEEP THIS FOR LATER
	app = initApp;

	// KEEP THE GUI FOR LATER
	gui = app.getGUI();

        // INIT ALL WORKSPACE COMPONENTS
	layoutGUI();
        
        // AND SETUP EVENT HANDLING
	setupHandlers();
    }
    
    private void layoutGUI() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        DataManager dataManager = (DataManager)app.getDataComponent();
        
	// FIRST THE LABEL AT THE TOP
        headingLabel = new Label();
        headingLabel.setText(props.getProperty(WORKSPACE_HEADING_LABEL));        

        // THEN THE TODO LIST DETAILS
        detailsBox = new VBox();
        detailsLabel = new Label();
        detailsLabel.setText(props.getProperty(DETAILS_HEADING_LABEL));
        
        // THIS WILL CONTAIN BOTH
        nameAndOwnerBox = new GridPane();
        
        // THIS JUST THE NAME
        nameBox = new HBox();
        nameLabel = new Label();
        nameLabel.setText(props.getProperty(NAME_PROMPT));
        nameTextField = new TextField();
        nameTextField.setText(dataManager.getName());
        nameBox.getChildren().addAll(nameLabel, nameTextField);

        // THIS JUST THE OWNER
        ownerBox = new HBox();
        ownerLabel = new Label(props.getProperty(OWNER_PROMPT));
        ownerTextField = new TextField();
        ownerTextField.setText(dataManager.getOwner());
        ownerBox.getChildren().addAll(ownerLabel, ownerTextField);
        
        // ARRANGE THE CONTENTS
        nameAndOwnerBox.add(nameLabel,0,0);
        nameAndOwnerBox.add(nameTextField, 1, 0);
        nameAndOwnerBox.add(ownerLabel,0,1);
        nameAndOwnerBox.add(ownerTextField, 1,1);
        
        
        // NOW ORGANIZE THE CONTENTS OF detailsBox
        detailsBox.getChildren().add(detailsLabel);
        detailsBox.getChildren().add(nameAndOwnerBox);
 
        // NOW THE CONTROLS FOR ADDING LECTURES
        itemsBox = new VBox();
        itemsLabel = new Label(props.getProperty(ITEMS_HEADING_LABEL));
        itemsToolbar = new HBox();
        addItemButton = gui.initChildButton(itemsToolbar, ADD_ICON.toString(), ADD_ITEM_TOOLTIP.toString(), false);
        removeItemButton = gui.initChildButton(itemsToolbar, REMOVE_ICON.toString(), REMOVE_ITEM_TOOLTIP.toString(), true);
        moveUpItemButton = gui.initChildButton(itemsToolbar, MOVE_UP_ICON.toString(), MOVE_UP_ITEM_TOOLTIP.toString(), true);
        moveDownItemButton = gui.initChildButton(itemsToolbar, MOVE_DOWN_ICON.toString(), MOVE_DOWN_ITEM_TOOLTIP.toString(), true);
        itemsTable = new TableView();
        itemsBox.getChildren().add(itemsLabel);
        itemsBox.getChildren().add(itemsToolbar);
        itemsBox.getChildren().add(itemsTable);
        
        // NOW SETUP THE TABLE COLUMNS
        itemCategoryColumn = new TableColumn(props.getProperty(CATEGORY_COLUMN_HEADING));
        itemDescriptionColumn = new TableColumn(props.getProperty(DESCRIPTION_COLUMN_HEADING));
        itemStartDateColumn = new TableColumn(props.getProperty(START_DATE_COLUMN_HEADING));
        itemEndDateColumn = new TableColumn(props.getProperty(END_DATE_COLUMN_HEADING));
        itemCompletedColumn = new TableColumn(props.getProperty(COMPLETED_COLUMN_HEADING));
        
        // AND LINK THE COLUMNS TO THE DATA
        itemCategoryColumn.setCellValueFactory(new PropertyValueFactory<String, String>("category"));
        itemDescriptionColumn.setCellValueFactory(new PropertyValueFactory<String, String>("description"));
        itemStartDateColumn.setCellValueFactory(new PropertyValueFactory<LocalDate, String>("startDate"));
        itemEndDateColumn.setCellValueFactory(new PropertyValueFactory<LocalDate, String>("endDate"));
        itemCompletedColumn.setCellValueFactory(new PropertyValueFactory<Boolean, String>("completed"));
        
        // SCALE THE COLUMN SIZES
        itemCategoryColumn.prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.2));
        itemDescriptionColumn.prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.2));
        itemStartDateColumn.prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.2));
        itemEndDateColumn.prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.2));
        itemCompletedColumn.prefWidthProperty().bind(itemsTable.widthProperty().multiply(0.2));
        
        itemsTable.getColumns().add(itemCategoryColumn);
        itemsTable.getColumns().add(itemDescriptionColumn);
        itemsTable.getColumns().add(itemStartDateColumn);
        itemsTable.getColumns().add(itemEndDateColumn);
        itemsTable.getColumns().add(itemCompletedColumn);
        itemsTable.setItems(dataManager.getItems());

	// AND NOW SETUP THE WORKSPACE
	workspace = new VBox();
        workspace.getChildren().add(headingLabel);
        workspace.getChildren().add(detailsBox);
        workspace.getChildren().add(itemsBox);
        
        // SETUP DIALOGS
        inputItemDialog = InputItemDialogSingleton.getSingleton();
        inputItemDialog.init(app);
        removeItemDialog = RemoveItemDialogSingleton.getSingleton();
        removeItemDialog.init(gui.getWindow());
    }
    
    /**
     * An accessor method for getting the TextField object.
     * 
     * @return The TextField object of this type
     */
    public TextField getNameTextField(){
        return nameTextField;
    }
    
    /**
     * An accessor method for getting the TextField object.
     * 
     * @return The TextField object of this type
     */
    public TextField getOwnerTextField(){
        return ownerTextField;
    }
    
    /**
     * An accessor method for getting the singleton object.
     * 
     * @return The one singleton dialog of this object type.
     */
    public InputItemDialogSingleton getInputItemDialog(){
        return inputItemDialog;
    }
    
    /**
     * An accessor method for getting the singleton object.
     * 
     * @return The one singleton dialog of this object type.
     */
    public RemoveItemDialogSingleton getRemoveItemDialog(){
        return removeItemDialog;
    }
    
    /**
     * An accessor method for getting the table of items.
     * 
     * @return The table of items.
     */
    public TableView<ToDoItem> getItemsTable(){
        return itemsTable;
    }
    
    public void setDebugText(String text) {
	debugText.setText(text);
    }
    
    /**
     * This method is used to activate/deactivate table buttons when
     * they can and cannot be used so as to provide foolproof design.
     */
    public void updateTableControls() {
        // REMOVE BUTTONS ENABLED
        // ONCE FIRST ITEM ADDED AND SELECTED
        int size = itemsTable.getItems().size();
        int index = itemsTable.getSelectionModel().getSelectedIndex();
        if (index>=0 && size>0) removeItemButton.setDisable(false);
        else removeItemButton.setDisable(true);
        
        // MOVE BUTTONS ENABLED
        // ONCE SELECTED AND MORE THAN ONE ITEM ADDED
        if (index>=0 && size>1){
            if (index>0) moveUpItemButton.setDisable(false);
            else moveUpItemButton.setDisable(true);
            if (index<size-1) moveDownItemButton.setDisable(false);
            else moveDownItemButton.setDisable(true);
        }
        else{
            moveUpItemButton.setDisable(true);
            moveDownItemButton.setDisable(true);
        }
        // NOTE THAT ADD ITEM BUTTON
        // IS NEVER DISABLED SO WE NEVER HAVE TO TOUCH IT
    }
    
    private void setupHandlers() {
	// MAKE THE CONTROLLER
	toDoListController = new ToDoListController(app);
	
	// NOW CONNECT THE BUTTONS TO THEIR HANDLERS
        nameTextField.setOnKeyReleased(e->{
            toDoListController.processNameUpdate();
        });
        ownerTextField.setOnKeyReleased(e->{
            toDoListController.processOwnerUpdate(); 
        });
        addItemButton.setOnAction(e->{
            toDoListController.processAddItem();
        });
        removeItemButton.setOnAction(e->{
            toDoListController.processRemoveItem();
        });
        moveUpItemButton.setOnAction(e->{
            toDoListController.processMoveUpItem();
        });
        moveDownItemButton.setOnAction(e->{
            toDoListController.processMoveDownItem();
        });
        itemsTable.setOnMouseClicked(e->{
            updateTableControls();
        });
        itemsTable.setRowFactory(e->{
            return toDoListController.processEditItem();
        });
    }
    
    public void setImage(ButtonBase button, String fileName) {
	// LOAD THE ICON FROM THE PROVIDED FILE
        String imagePath = FILE_PROTOCOL + PATH_IMAGES + fileName;
        Image buttonImage = new Image(imagePath);
	
	// SET THE IMAGE IN THE BUTTON
        button.setGraphic(new ImageView(buttonImage));	
    }

    /**
     * This function specifies the CSS style classes for all the UI components
     * known at the time the workspace is initially constructed. Note that the
     * tag editor controls are added and removed dynamicaly as the application
     * runs so they will have their style setup separately.
     */
    @Override
    public void initStyle() {
	// NOTE THAT EACH CLASS SHOULD CORRESPOND TO
	// A STYLE CLASS SPECIFIED IN THIS APPLICATION'S
	// CSS FILE
        
        // FIRST THE WORKSPACE PANE
        workspace.getStyleClass().add(CLASS_BORDERED_PANE);
        
        // THEN THE HEADING
	headingLabel.getStyleClass().add(CLASS_HEADING_LABEL);
        
        // THEN THE DETAILS PANE AND ITS COMPONENTS
        detailsBox.getStyleClass().add(CLASS_BORDERED_PANE);
        detailsLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        nameAndOwnerBox.getStyleClass().add(CLASS_GRID);
        nameBox.getStyleClass().add(CLASS_HBOX);
        nameLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        ownerBox.getStyleClass().add(CLASS_HBOX);
        ownerLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        itemsBox.getStyleClass().add(CLASS_BORDERED_PANE);
        itemsLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        itemCategoryColumn.getStyleClass().add(CLASS_COLUMN);
        itemDescriptionColumn.getStyleClass().add(CLASS_COLUMN);
        itemStartDateColumn.getStyleClass().add(CLASS_COLUMN);
        itemEndDateColumn.getStyleClass().add(CLASS_COLUMN);
        itemCompletedColumn.getStyleClass().add(CLASS_COLUMN);
    }

    /**
     * This function reloads all the controls for editing tag attributes into
     * the workspace.
     */
    @Override
    public void reloadWorkspace() {
	DataManager dataManager = (DataManager)app.getDataComponent();
        nameTextField.setText(dataManager.getName());
        ownerTextField.setText(dataManager.getOwner());
        itemsTable.setItems(dataManager.getItems());
    }
}
