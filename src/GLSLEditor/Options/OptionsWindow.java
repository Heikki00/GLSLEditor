package GLSLEditor.Options;


import GLSLEditor.Editor;
import GLSLEditor.Layouts.OptionsLayout.OptionsLayoutController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class OptionsWindow {
    private static Editor editor;

    public static void init(Editor editor){
        OptionsWindow.editor = editor;
    }

    public static void show(){


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

        OptionsLayoutController c = loader.getController();


        optionsScene = new Scene(root, 500, 400);

        optionsScene.getStylesheets().add(editor.getClass().getResource("Layouts/MainLayout/MainLayoutStyle.css").toExternalForm());

        optionsWindow.setScene(optionsScene);













        optionsWindow.show();











    }


}
