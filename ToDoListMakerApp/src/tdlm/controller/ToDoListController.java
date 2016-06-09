package tdlm.controller;

import java.time.LocalDate;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import properties_manager.PropertiesManager;
import tdlm.gui.Workspace;
import saf.AppTemplate;
import static saf.ui.AppYesNoCancelDialogSingleton.YES;
import tdlm.gui.InputItemDialogSingleton;
import static tdlm.PropertyType.INPUT_ITEM_MESSAGE;
import static tdlm.PropertyType.ADD_ITEM_TITLE;
import static tdlm.PropertyType.EDIT_ITEM_TITLE;
import static tdlm.PropertyType.OK;
import static tdlm.PropertyType.REMOVE_ITEM_MESSAGE;
import static tdlm.PropertyType.REMOVE_ITEM_TITLE;
import tdlm.data.DataManager;
import tdlm.data.ToDoItem;
import tdlm.gui.RemoveItemDialogSingleton;
/**
 * This class responds to interactions with todo list editing controls.
 * 
 * @author McKillaGorilla
 * @version 1.0
 */
public class ToDoListController {
    AppTemplate app;
    
    // WE WANT TO KEEP TRACK OF WHEN SOMETHING HAS NOT BEEN SAVED
    boolean saved;
    
    public ToDoListController(AppTemplate initApp) {
        // NOTHING YET
        saved = true;
        app = initApp;
    }
    
    public void processNameUpdate(){
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        DataManager dataManager = (DataManager)app.getDataComponent();
        
        // UPDATE NAME AND SAVE STATUS
        String newName = workspace.getNameTextField().getText();
        dataManager.setName(newName);
        saved = false;
        
        app.getGUI().updateToolbarControls(saved);
    }
    
    public void processOwnerUpdate(){
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        DataManager dataManager = (DataManager)app.getDataComponent();
        
        // UPDATE OWNER AND SAVE STATUS
        String newOwner = workspace.getOwnerTextField().getText();
        dataManager.setOwner(newOwner);
        saved = false;
        
        app.getGUI().updateToolbarControls(saved);
    }
    
    public void processAddItem() {
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // SHOW ADD ITEM PROMPT
        InputItemDialogSingleton dialog = workspace.getInputItemDialog();
        // DEFAULT DATE SELECTION
        dialog.getStartDatePicker().setValue(LocalDate.now());
        dialog.getEndDatePicker().setValue(LocalDate.now());
        // SHOW
        dialog.show(props.getProperty(ADD_ITEM_TITLE),props.getProperty(INPUT_ITEM_MESSAGE));
        
        // VERIFY ACTION
        String selection = dialog.getSelection();
        if (selection.equals(props.getProperty(OK))){
            // ADD NEW ITEM DATA WITH USER INPUT 
            ToDoItem newItem = dialog.getItem();
            DataManager dataManager = (DataManager)app.getDataComponent();
            dataManager.addItem(newItem);
            
            // UPDATE THE TABLE AND SAVE STATUS
            TableView<ToDoItem> itemsTable = workspace.getItemsTable();
            itemsTable.setItems(dataManager.getItems());
            saved = false;
            
            // RESET INPUT FIELDS
            dialog.getCategoryTextField().clear();
            dialog.getDescriptionTextField().clear();
            dialog.getStartDatePicker().setValue(LocalDate.now());
            dialog.getEndDatePicker().setValue(LocalDate.now());
            dialog.getCompletedCheckBox().setSelected(false);
            
            // ENABLE AND DISABLE APPROPRIATE CONTROLS
            workspace.updateTableControls();
            app.getGUI().updateToolbarControls(saved);
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
        
            // UPDATE THE TABLE AND SAVE STATUS
            itemsTable.setItems(dataManager.getItems());
            saved = false;
        
            // ENABLE AND DISABLE APPROPRIATE CONTROLS
            workspace.updateTableControls();
            app.getGUI().updateToolbarControls(saved);
        }
    }
    
    public void processMoveUpItem() {
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        DataManager dataManager = (DataManager)app.getDataComponent();

        // GET SELECTED ITEM DATA
        TableView<ToDoItem> itemsTable = workspace.getItemsTable();
        int index = itemsTable.getSelectionModel().getSelectedIndex();
        int previous = index-1;
        ToDoItem selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        ToDoItem previousItem = dataManager.getItems().get(previous);
        
        // MOVE ITEM UP
        dataManager.getItems().set(previous,selectedItem);
        dataManager.getItems().set(index, previousItem);
           
        // UPDATE THE TABLE AND SAVE STATUS
        itemsTable.setItems(dataManager.getItems());
        saved = false;
        
        // ENABLE AND DISABLE APPROPRIATE CONTROLS
        workspace.updateTableControls();
        app.getGUI().updateToolbarControls(saved);
    }

    public void processMoveDownItem() {
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
        DataManager dataManager = (DataManager)app.getDataComponent();

        // GET SELECTED ITEM DATA
        TableView<ToDoItem> itemsTable = workspace.getItemsTable();
        int index = itemsTable.getSelectionModel().getSelectedIndex();
        int next = index+1;
        ToDoItem selectedItem = itemsTable.getSelectionModel().getSelectedItem();
        ToDoItem nextItem = dataManager.getItems().get(next);
        
        // MOVE ITEM DOWN
        dataManager.getItems().set(next,selectedItem);
        dataManager.getItems().set(index, nextItem);
        
        // UPDATE THE TABLE AND SAVE STATUS
        itemsTable.setItems(dataManager.getItems());
        saved = false;
        
        // ENABLE AND DISABLE APPROPRIATE CONTROLS
        workspace.updateTableControls();
        app.getGUI().updateToolbarControls(saved);
    }

     public TableRow<ToDoItem> processEditItem(){
        TableRow<ToDoItem> itemRow = new TableRow<>();
        itemRow.setOnMouseClicked(e->{
            if (e.getClickCount() == 2 && (! itemRow.isEmpty())){
                Workspace workspace = (Workspace)app.getWorkspaceComponent();
                PropertiesManager props = PropertiesManager.getPropertiesManager();
                DataManager dataManager = (DataManager)app.getDataComponent();
                
                // GET SELECTED ITEM DATA
                ToDoItem selectedItem = itemRow.getItem();

                // EDIT ITEM PROMPT
                InputItemDialogSingleton dialog = workspace.getInputItemDialog();
                // SET INPUT FIELDS
                dialog.getCategoryTextField().setText(selectedItem.getCategory());
                dialog.getDescriptionTextField().setText(selectedItem.getDescription());
                dialog.getStartDatePicker().setValue(selectedItem.getStartDate());
                dialog.getEndDatePicker().setValue(selectedItem.getEndDate());
                dialog.getCompletedCheckBox().setSelected(selectedItem.getCompleted());
                // SHOW
                dialog.show(props.getProperty(EDIT_ITEM_TITLE),props.getProperty(INPUT_ITEM_MESSAGE));
                
                // VERIFY ACTION
                String selection = dialog.getSelection();
                if (selection.equals(props.getProperty(OK))){
                    // MODIFY SELECTED ITEM IN LIST
                    int selectedItemIndex = dataManager.getItems().indexOf(selectedItem);
                    ToDoItem newItem = dialog.getItem();
                    dataManager.getItems().set(selectedItemIndex,newItem);

                    // UPDATE THE TABLE AND SAVE STATUS
                    TableView<ToDoItem> itemsTable = workspace.getItemsTable();
                    itemsTable.setItems(dataManager.getItems());
                    saved = false;
                    
                    // ENABLE AND DISABLE APPROPRIATE CONTROLS
                    app.getGUI().updateToolbarControls(saved);
                }
            }
        });
        return itemRow;
     }
}
