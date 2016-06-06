package tdlm.controller;

import javafx.scene.control.TableView;
import properties_manager.PropertiesManager;
import tdlm.gui.Workspace;
import saf.AppTemplate;
import static saf.ui.AppYesNoCancelDialogSingleton.YES;
import tdlm.gui.InputItemDialogSingleton;
import static tdlm.PropertyType.INPUT_ITEM_MESSAGE;
import static tdlm.PropertyType.ADD_ITEM_TITLE;
import static tdlm.PropertyType.EDIT_ITEM_TITLE;
import static tdlm.PropertyType.REMOVE_ITEM_MESSAGE;
import static tdlm.PropertyType.REMOVE_ITEM_TITLE;
import tdlm.data.DataManager;
import tdlm.data.ToDoItem;
import static tdlm.gui.InputItemDialogSingleton.OK;
import tdlm.gui.RemoveItemDialogSingleton;
/**
 * This class responds to interactions with todo list editing controls.
 * 
 * @author McKillaGorilla
 * @version 1.0
 */
public class ToDoListController {
    AppTemplate app;
    
    // WE WANT TO KEEP TRACK OF ITEMS IN TABLE
    int size;
    
    public ToDoListController(AppTemplate initApp) {
        // NOTHING YET
        size = 0;
        app = initApp;
    }
    
    public void processNameUpdate(){
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        DataManager dataManager = (DataManager)app.getDataComponent();
        
        // UPDATE NAME
        String newName = workspace.getNameTextField().getText();
        dataManager.setName(newName);
    }
    
    public void processOwnerUpdate(){
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        DataManager dataManager = (DataManager)app.getDataComponent();
        
        // UPDATE OWNER
        String newOwner = workspace.getOwnerTextField().getText();
        dataManager.setOwner(newOwner);
    }
    
    public void processAddItem() {
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // SHOW ADD ITEM PROMPT
        InputItemDialogSingleton dialog = workspace.getInputItemDialog();
        dialog.show(props.getProperty(ADD_ITEM_TITLE),props.getProperty(INPUT_ITEM_MESSAGE));
        
        // VERIFY ACTION
        String selection = dialog.getSelection();
        if (selection.equals(OK)){
            // ADD NEW ITEM DATA WITH USER INPUT 
            ToDoItem newItem = dialog.getItem();
            DataManager dataManager = (DataManager)app.getDataComponent();
            dataManager.addItem(newItem);
        
            // UPDATE THE TABLE AND SIZE
            TableView<ToDoItem> itemsTable = workspace.getItemsTable();
            itemsTable.setItems(dataManager.getItems());
            size = dataManager.getItems().size();
        
            // ENABLE AND DISABLE APPROPRIATE CONTROLS
            workspace.updateTableControls(size);
        }
    }

    public void processRemoveItem() {
        Workspace workspace = (Workspace)app.getWorkspaceComponent();           
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // PROMPT USER TO CONFIRM REMOVE ITEM 
        RemoveItemDialogSingleton dialog = workspace.getRemoveItemDialog();
        dialog.show(props.getProperty(REMOVE_ITEM_TITLE),props.getProperty(REMOVE_ITEM_MESSAGE));
        
        // AND NOW GET THE USER'S SELECTION
        String selection = dialog.getSelection();
        // IF THE USER SAID YES, THEN SAVE REMOVE ITEM
        if (selection.equals(YES)){
            // GET SELECTED ITEM TO REMOVE
            TableView<ToDoItem> itemsTable = workspace.getItemsTable();
            ToDoItem selectedItem = itemsTable.getSelectionModel().getSelectedItem();
            DataManager dataManager = (DataManager)app.getDataComponent();
            dataManager.removeItem(selectedItem);
        
            // UPDATE THE TABLE AND SIZE
            itemsTable.setItems(dataManager.getItems());
            size = dataManager.getItems().size();
        
            // ENABLE AND DISABLE APPROPRIATE CONTROLS
            workspace.updateTableControls(size);
        }
    }
    
    public void processMoveUpItem() {
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        DataManager dataManager = (DataManager)app.getDataComponent();

        // GET SELECTED ITEM DATA
        TableView<ToDoItem> itemsTable = workspace.getItemsTable();
        ToDoItem selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        int index = itemsTable.getSelectionModel().getSelectedIndex();
        int previous = index-1;
        
        // VERIFY VALID ACTION WITHIN BOUNDS
        if (previous>=0){
            ToDoItem previousItem = dataManager.getItems().get(previous);
            // MOVE ITEM UP
            dataManager.getItems().set(previous,selectedItem);
            dataManager.getItems().set(index, previousItem);
        }
        
        // UPDATE THE TABLE
        itemsTable.setItems(dataManager.getItems());
    }

    public void processMoveDownItem() {
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        DataManager dataManager = (DataManager)app.getDataComponent();

        // GET SELECTED ITEM DATA
        TableView<ToDoItem> itemsTable = workspace.getItemsTable();
        ToDoItem selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        int index = itemsTable.getSelectionModel().getSelectedIndex();
        int next = index+1;
        
        // VERIFY VALID ACTION WITHIN BOUNDS
        if (next<size){
            ToDoItem nextItem = dataManager.getItems().get(next);
            // MOVE ITEM DOWN
            dataManager.getItems().set(next,selectedItem);
            dataManager.getItems().set(index, nextItem);
        }
        
        // UPDATE THE TABLE
        itemsTable.setItems(dataManager.getItems());
    }

    public void processEditItem() {
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // SHOW EDIT ITEM PROMPT
        InputItemDialogSingleton dialog = workspace.getInputItemDialog();
        dialog.show(props.getProperty(EDIT_ITEM_TITLE),props.getProperty(INPUT_ITEM_MESSAGE));

        // VERIFY ACTION
        String selection = dialog.getSelection();
        if (selection.equals(OK)){
            // GET SELECTED ITEM DATA
            DataManager dataManager = (DataManager)app.getDataComponent();
            TableView<ToDoItem> itemsTable = workspace.getItemsTable();
            ToDoItem selectedItem = itemsTable.getSelectionModel().getSelectedItem();
            int selectedItemIndex = dataManager.getItems().indexOf(selectedItem);
            
            // MODIFY SELECTED ITEM IN LIST
            ToDoItem newItem = dialog.getItem();
            dataManager.getItems().set(selectedItemIndex,newItem);
            
            // UPDATE THE TABLE
            itemsTable.setItems(dataManager.getItems());
        }
    }
}
