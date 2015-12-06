package GLSLEditor;

import GLSLEditor.Layouts.MainLayout.MainLayoutController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main {

    /*PROJECTNOTES:
    * -standard of \n
    * -standard of /
    * -version 430
    * -how .glsl is structured
    * -how .shaders is structured
    * -why i'm doing this
    * -performance standpoint (not important for this project)
    * -standard of filename vs name
    * -#include extension
    * -only including .glh
    * -dependencies(richtextFX)
    * -tab to spaces
    * */


    public static String[] arg;

    //Starting point of this program. Launches instance of editor.
    public static void main(String[] args) {
        arg = args;

        Editor e = new Editor();
        e.launch(Editor.class);

    }
}
