/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tdlm.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import static saf.settings.AppStartupConstants.CANCEL;
import static tdlm.PropertyType.CATEGORY_LABEL;
import static tdlm.PropertyType.COMPLETED_LABEL;
import static tdlm.PropertyType.DESCRIPTION_LABEL;
import static tdlm.PropertyType.END_LABEL;
import static tdlm.PropertyType.OK;
import static tdlm.PropertyType.START_LABEL;
import tdlm.data.ToDoItem;

/**
 * This class serves to present an Add Item input dialog.
 * 
 * @author Jon Reyes
 */
public class InputItemDialogSingleton extends Stage {
    // HERE'S THE SINGLETON OBJECT
    static InputItemDialogSingleton singleton = null;
    
    // HERE ARE THE DIALOG COMPONENTS 
    VBox messagePane;
    Scene messageScene;
    Label messageLabel;
    
    GridPane inputGrid;
    
    Label categoryLabel;
    TextField categoryTextField;
    
    Label descriptionLabel;
    TextField descriptionTextField;
    
    Label startLabel;
    DatePicker startDatePicker;
    
    Label endLabel;
    DatePicker endDatePicker;
    
    HBox completedBox;
    Label completedLabel;
    CheckBox completedCheckBox;
    
    HBox controlBox;
    Button okButton;
    Button cancelButton;
    
    String selection;
    ToDoItem newItem;
    
     /**
     * Note that the constructor is private since it follows
     * the singleton design pattern.
     * 
     * @param primaryStage The owner of this modal dialog.
     */
    private InputItemDialogSingleton(){}
    
    /**
     * A static accessor method for getting the singleton object.
     * 
     * @return The one singleton dialog of this object type.
     */
    public static InputItemDialogSingleton getSingleton() {
	if (singleton == null)
	    singleton = new InputItemDialogSingleton();
	return singleton;
    }
    
   /**
     * An accessor method for getting the TextField object
     * 
     * @return The TextField object of this type.
     */
    public TextField getCategoryTextField(){
        return categoryTextField;
    }
    
    /**
     * An accessor method for getting the TextField object
     * 
     * @return The TextField object of this type.
     */
    public TextField getDescriptionTextField(){
        return descriptionTextField;
    }
    
    /**
     * An accessor method for getting the DatePicker object
     * 
     * @return The DatePicker object of this type.
     */
    public DatePicker getStartDatePicker(){
        return startDatePicker;
    }
    
    /**
     * An accessor method for getting the DatePicker object
     * 
     * @return The DatePicker object of this type.
     */
    public DatePicker getEndDatePicker(){
        return endDatePicker;
    }
    
    /**
     * An accessor method for getting the CheckBox object
     * 
     * @return The CheckBox object of this type.
     */
    public CheckBox getCompletedCheckBox(){
        return completedCheckBox;
    }
    
    /**
     * An accessor method for getting the selection
     * 
     * @return The user selection when this dialog was presented.
     */
    public String getSelection(){
        return selection;
    }
    
    /**
     * An accessor method for getting the new item created.
     * 
     * @return The new item to be added.
     */
    public ToDoItem getItem(){
        return newItem;
    }
    
    /**
     * This method initializes the singleton for use.
     * 
     * @param owner The window above which this dialog will be centered.
     */
    public void init(Stage owner) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        // MAKE THIS DIALOG MODAL, MEANING OTHERS WILL WAIT
        // FOR IT WHEN IT IS DISPLAYED
        initModality(Modality.WINDOW_MODAL);
        initOwner(owner);
        
        // LABEL TO DISPLAY THE CUSTOM MESSAGE
        messageLabel = new Label();
        
        // CATEGORY INPUT
        categoryLabel = new Label(props.getProperty(CATEGORY_LABEL));
        categoryTextField = new TextField();
        
        // DESCRIPTION INPUT
        descriptionLabel = new Label(props.getProperty(DESCRIPTION_LABEL));
        descriptionTextField = new TextField();
        
        // START DATE INPUT
        startLabel = new Label(props.getProperty(START_LABEL));
        startDatePicker = new DatePicker();
        
        // END DATE INPUT
        endLabel = new Label(props.getProperty(END_LABEL));
        endDatePicker = new DatePicker();
        
        // COMPLETED INPUT
        completedCheckBox = new CheckBox(props.getProperty(COMPLETED_LABEL));
        
        // OK AND CANCEL CONTROLS
        controlBox = new HBox();
        controlBox.setAlignment(Pos.CENTER);
        okButton = new Button(props.getProperty(OK));
        cancelButton = new Button(CANCEL);
        controlBox.getChildren().addAll(okButton,cancelButton);
        
        // MAKE THE EVENT HANDLER FOR THESE BUTTONS
        EventHandler okCancelHandler = (EventHandler<ActionEvent>) (ActionEvent ae) -> {
            Button sourceButton = (Button)ae.getSource();
            InputItemDialogSingleton.this.selection = sourceButton.getText();
            if (InputItemDialogSingleton.this.selection.equals(props.getProperty(OK))){
                // CREATE NEW ITEM FROM INPUT
                newItem = new ToDoItem(
                    categoryTextField.getText(),
                    descriptionTextField.getText(),
                    startDatePicker.getValue(),
                    endDatePicker.getValue(),
                    completedCheckBox.isSelected()
                );
            }            
            InputItemDialogSingleton.this.hide();
        };
        
        // AND THEN REGISTER THEM TO RESPOND TO INTERACTIONS
        okButton.setOnAction(okCancelHandler);
        cancelButton.setOnAction(okCancelHandler);
        
        // INPUT GRID
        inputGrid = new GridPane();
        inputGrid.addRow(0,categoryLabel,categoryTextField);
        inputGrid.addRow(1,descriptionLabel,descriptionTextField);
        inputGrid.addRow(2,startLabel,startDatePicker);
        inputGrid.addRow(3,endLabel,endDatePicker);
        
        // WE'LL PUT EVERYTHING HERE
        messagePane = new VBox();
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(messageLabel);
        messagePane.getChildren().add(inputGrid);
        messagePane.getChildren().add(completedCheckBox);
        messagePane.getChildren().add(controlBox);
        
        // MAKE IT LOOK NICE
        messagePane.setPadding(new Insets(40, 30, 40, 30));
        messagePane.setSpacing(20);
        
        // AND PUT IT IN THE WINDOW
        messageScene = new Scene(messagePane);
        this.setScene(messageScene);
    }
    
    /**
     * This method loads a custom message into the label and
     * then pops open the dialog.
     * 
     * @param title The title to appear in the dialog window.
     * 
     * @param message Message to appear inside the dialog.
     */
    public void show(String title, String message) {
	// SET THE DIALOG TITLE BAR TITLE
	setTitle(title);
	
	// SET THE MESSAGE TO DISPLAY TO THE USER
        messageLabel.setText(message);
	
	// AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
	// WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
	// DO MORE WORK.
        showAndWait();
    }
}
