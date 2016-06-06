package tdlm.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import saf.components.AppDataComponent;
import saf.AppTemplate;

/**
 * This class serves as the data management component for this application.
 *
 * @author Richard McKenna
 * @version 1.0
 */
public class DataManager implements AppDataComponent {
    // FIRST THE THINGS THAT HAVE TO BE SAVED TO FILES
    
    // NAME OF THE TODO LIST
    StringProperty name;
    
    // LIST OWNER
    StringProperty owner;
    
    // THESE ARE THE ITEMS IN THE TODO LIST
    ObservableList<ToDoItem> items;
    
    // THIS IS A SHARED REFERENCE TO THE APPLICATION
    AppTemplate app;
    
    /**
     * This constructor creates the data manager and sets up the
     *
     * @param initApp The application within which this data manager is serving.
     */
    public DataManager(AppTemplate initApp) throws Exception {
	// KEEP THE APP FOR LATER
	app = initApp;
        name = new SimpleStringProperty();
        owner = new SimpleStringProperty();
        items = FXCollections.observableArrayList();
    }
   
    public ObservableList<ToDoItem> getItems() {
	return items;
    }
    
    public String getName() {
        return name.get();
    }
    
    public void setName(String name){
        this.name.set(name);
    }
    
    public String getOwner() {
        return owner.get();
    }
    
    public void setOwner(String owner) {
        this.owner.set(owner);
    }

    public void addItem(ToDoItem item) {
        items.add(item);
    }
    
    public void removeItem(ToDoItem item){
        items.remove(item);
    }

    /**
     * 
     */
    @Override
    public void reset() {
    }
}
