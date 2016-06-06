package tdlm.controller;

import javafx.scene.control.TableView;
import properties_manager.PropertiesManager;
import tdlm.gui.Workspace;
import saf.AppTemplate;
import tdlm.gui.AddItemDialogSingleton;
import static tdlm.PropertyType.ADD_ITEM_MESSAGE;
import static tdlm.PropertyType.ADD_ITEM_TITLE;
import tdlm.data.DataManager;
import tdlm.data.ToDoItem;
/**
 * This class responds to interactions with todo list editing controls.
 * 
 * @author McKillaGorilla
 * @version 1.0
 */
public class ToDoListController {
    AppTemplate app;
    
    public ToDoListController(AppTemplate initApp) {
	app = initApp;
    }
    //4
    public void processAddItem() {
        // ENABLE/DISABLE THE PROPER BUTTONS
	Workspace workspace = (Workspace)app.getWorkspaceComponent();
	workspace.reloadWorkspace();
        
        AddItemDialogSingleton dialog = workspace.getAddItemDialog();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        
        // SHOW ADD ITEM PROMPT
        dialog.show(props.getProperty(ADD_ITEM_TITLE),props.getProperty(ADD_ITEM_MESSAGE));
        
        // ADD NEW ITEM DATA WITH USER INPUT 
        ToDoItem newItem = dialog.getItem();
        DataManager dataManager = (DataManager)app.getDataComponent();
        if (newItem != null) dataManager.addItem(newItem);
        
        // UPDATE TABLE
        TableView<ToDoItem> itemsTable = workspace.getItemsTable();
        itemsTable.setItems(dataManager.getItems());
    }
    //5
    public void processRemoveItem() {
        
    }
    //6
    public void processMoveUpItem() {
        
    }
    //7
    public void processMoveDownItem() {
        
    }
    //8
    public void processEditItem() {
        
    }
}
