package GLSLEditor.Options;


import GLSLEditor.Editor;
import GLSLEditor.Layouts.OptionsLayout.OptionsLayoutController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OptionsWindow {
    private static Editor editor;
    private static List<Label> tabs;
    private static Label selected;
    private static OptionsLayoutController controller;

    private static Parent generalOptionsRoot;


    public static void init(Editor editor){
        OptionsWindow.editor = editor;
    }

    public static void show(){
        tabs = new ArrayList<Label>();

        Stage optionsWindow = new Stage();
        optionsWindow.initModality(Modality.APPLICATION_MODAL);
        Scene optionsScene;

        FXMLLoader loader = new FXMLLoader(editor.getClass().getResource("Layouts/OptionsLayout/OptionsLayout.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        controller = loader.getController();


        optionsScene = new Scene(root, 500, 400);

        optionsScene.getStylesheets().add(editor.getClass().getResource("Layouts/OptionsLayout/OptionsLayoutStyle.css").toExternalForm());

        optionsWindow.setScene(optionsScene);

        loadOptionLayouts();





        tabs.add(controller.GeneralOptionsTab);
        tabs.add(controller.EditorOptionsTab);
        tabs.add(controller.HotkeyOptionsTab);


        for(Label l : tabs){

            l.setOnMouseEntered(e -> {if(selected != l)l.setId("OptionsTab_hover");});
            l.setOnMouseExited(e -> {if (selected != l) l.setId("OptionsTab_idle");});
            l.setOnMouseClicked(e -> {if(selected != l) select(l);});
            l.setId("OptionsTab_idle");
        }

        select(controller.GeneralOptionsTab);

        optionsWindow.show();



    }


    private static void select(Label l){
        if(selected != null) selected.setId("OptionsTab_idle");
        selected = l;
        selected.setId("OptionsTab_selected");


        if(selected.equals(controller.GeneralOptionsTab)){
                setOptionsLayout(generalOptionsRoot);
        }





    }


    private static void setOptionsLayout(Node root){
        controller.OptionsPane.getChildren().clear();
        controller.OptionsPane.getChildren().add(root);
    }

    private static void loadOptionLayouts(){


        FXMLLoader loader = new FXMLLoader(editor.getClass().getResource("Layouts/OptionsLayout/GeneralOptionsLayout.fxml"));
        generalOptionsRoot = null;
        try {
            generalOptionsRoot = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }







    }




}
