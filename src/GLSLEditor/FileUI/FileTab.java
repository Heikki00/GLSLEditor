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

public class FileTab {

    private Document document;

    private Editor editor;
    private HBox hbox;
    private Label label;
    private Button closeButton;
    private boolean selected;

    public FileTab(Document doc, final Editor editor){
        label = new Label(doc.getName());
        hbox = new HBox();
        closeButton = new Button();

        hbox.setPrefWidth(100);
        hbox.setMinHeight(editor.getFileBar().getBar().getHeight());
        hbox.getChildren().add(label);
        hbox.getChildren().add(closeButton);

        closeButton.setBackground(new Background(new BackgroundFill(Paint.valueOf("red"), null, null)));
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editor.menuClose();
            }
        });

        document = doc;
        this.editor = editor;
        selected = false;

        label.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                editor.select(FileTab.this);
            }
        });

        label.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(!selected) hbox.setId("FileTab_hover");
            }
        });

        label.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if(!selected) hbox.setId("FileTab_idle");
            }
        });

        hbox.setId("FileTab_idle");



        if(document.isSaved() == true){
            label.setId("FileTabText_saved");
        }
        if(document.isSaved()  == false){
            label.setId("FileTabText_unsaved");
        }
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


        document.getFilenameProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                label.setText(document.getName());
            }
        });


    }


    protected void select(){

        selected = true;
        hbox.setId("FileTab_selected");

    }

    protected void unselect(){

        selected = false;
        hbox.setId("FileTab_idle");
    }

    public boolean isSelected(){
        return selected;
    }


    public Document getDocument(){return document;}

    public String getName(){return label.getText();}

    public Node toNode(){
        return hbox;

    }


}
