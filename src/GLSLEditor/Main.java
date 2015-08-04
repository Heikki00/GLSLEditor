package GLSLEditor;

import GLSLEditor.Layouts.MainLayout.MainLayoutController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main {



    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Layouts/MainLayout/MainLayout.fxml"));

        Parent root = loader.load();

        MainLayoutController c = loader.getController();

       // codeArea = c.mainCodeArea;

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 600, 500));
        root.getScene();

       // height = new SimpleDoubleProperty();
      //  height.bind(primaryStage.heightProperty());

       // width = new SimpleDoubleProperty();
      //  width.bind(primaryStage.widthProperty());




        primaryStage.show();

     //   Document d = new Document("C:\\Users\\Heikki\\Documents\\Visual Studio 2013\\Projects\\DareEngine2\\DareEngine2\\res\\shaders\\test.glsl");

     //     codeArea.replaceText(d.getRawText());




    }



    void open(String filename){





    }





    public static void main(String[] args) {
        Editor e = new Editor();
        e.launch(Editor.class);

    }
}
