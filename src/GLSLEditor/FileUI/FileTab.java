package GLSLEditor.FileUI;


import GLSLEditor.Document;
import GLSLEditor.Editor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

//A class that represents a singe tab on the filebar. Handles closing files from UI, and contains the document. Otherwise, mostly UI stuff
public class FileTab {

    //The document of this tab
    private Document document;


    private Editor editor;

    //The UI element of the tab. Contains the name and the close button
    private HBox hbox;
    //The label that has the document's name
    private Label label;
    //The button that closes the tab
    private Button closeButton;
    //Is this tab selected?
    private boolean selected;


    public FileTab(Document doc, final Editor editor){
        label = new Label(doc.getName());
        hbox = new HBox();
        closeButton = new Button();
        document = doc;
        this.editor = editor;
        selected = false;
        //Because of lambdas
        FileTab own = this;

        //Set up the UI
        hbox.setMinWidth(100);
        hbox.setMaxWidth(100);
        hbox.setMinHeight(editor.getFileBar().getBar().getHeight());
        hbox.getChildren().add(label);
        hbox.getChildren().add(closeButton);
        label.setMinWidth(90);
        //TODO: Actual icon
        closeButton.setBackground(new Background(new BackgroundFill(Paint.valueOf("red"), null, null)));

        //Lambda to close the tab(requests it from FileBar)
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editor.removeTab(own);
            }
        });


        //Lambda to select the tab
        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editor.selectTab(FileTab.this);
            }
        });

        //Style stuff
        label.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!selected) hbox.setId("FileTab_hover");
            }
        });

        //Style stuff
        label.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if(!selected) hbox.setId("FileTab_idle");
            }
        });

        //Style stuff
        hbox.setId("FileTab_idle");


        //Style stuff
        if(document.isSaved() == true){
            label.setId("FileTabText_saved");
        }
        if(document.isSaved()  == false){
            label.setId("FileTabText_unsaved");
        }

        //Style stuff
        document.getSavedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {


                if(newValue.booleanValue() == true){
                    label.setId("FileTabText_saved");
                }
                if(newValue.booleanValue() == false){
                    label.setId("FileTabText_unsaved");
                }

            }
        });

        //If the document's name is changed, change the label's text
        document.getFilenameProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                label.setText(document.getName());
            }
        });


    }


    //Sets the style of the tab and sets the boolean flag, called from FileBar
    protected void select(){

        selected = true;
        hbox.setId("FileTab_selected");

    }

    //Sets the style of the tab and sets the boolean flag, called from FileBar
    protected void unselect(){

        selected = false;
        hbox.setId("FileTab_idle");
    }

    //Is the tab selected?
    public boolean isSelected(){
        return selected;
    }


    public Document getDocument(){return document;}

    public String getName(){return label.getText();}

    public Node toNode(){
        return hbox;

    }


}
