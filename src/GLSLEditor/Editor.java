package GLSLEditor;


import GLSLEditor.FileUI.FileBar;
import GLSLEditor.FileUI.FileTab;
import GLSLEditor.Highlighting.Highlighter;
import GLSLEditor.Hotkey.Hotkey;
import GLSLEditor.Hotkey.Hotkeys;
import GLSLEditor.Layouts.MainLayout.MainLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;

public class Editor extends Application{
    private Stage window;
    private Scene scene;
    private String windowTitle;
    private CodeArea codeArea;
    private FileBar fileBar;
    private final String initialDirectory = "C:/Users/Heikki/IdeaProjects/GLSLEditor/TestFiles";


    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle(windowTitle);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("Layouts/MainLayout/MainLayout.fxml"));
        Parent root = loader.load();
        MainLayoutController c = loader.getController();


        scene = new Scene(root, 800, 600);
        window.setScene(scene);

        codeArea = new CodeArea(c.mainCodeArea, this);
        fileBar = new FileBar(c.activeFileBar, this);


        c.newMenuItem.setOnAction( e-> menuNew());
        Hotkeys.setHotkey("MenuNew", new Hotkey(this, new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> menuNew()));



        c.openMenuItem.setOnAction(e -> menuOpen());
        Hotkeys.setHotkey("MenuOpen", new Hotkey(this, new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), () -> menuOpen()));


        c.saveMenuItem.setOnAction(e -> menuSave());
        Hotkeys.setHotkey("MenuSave", new Hotkey(this, new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), () -> menuSave()));


        c.saveAsMenuItem.setOnAction(e -> menuSaveAs());
        Hotkeys.setHotkey("MenuSaveAs", new Hotkey(this, new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), () -> menuSaveAs()));


        c.closeMenuItem.setOnAction(e -> menuClose());


        Highlighter.init(this);



        scene.getStylesheets().add(getClass().getResource("MainLayout/MainLayoutStyle.css").toExternalForm());

        window.show();


        c.activeFileBar.requestFocus();

    }



    public void menuNew(){
        FileTab newTab = new FileTab(new Document(), this);
        fileBar.addTab(newTab);
        select(newTab);


    }

    public void menuOpen(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to open");
        fileChooser.setInitialDirectory(new File(initialDirectory));

        File file =  fileChooser.showOpenDialog(null);


        if(file != null) {

            Document doc = new Document(file.getAbsolutePath().replace('\\', '/'));



            if(fileBar.hasTab(doc)) {
                select(fileBar.getTab(doc));
                return;
            }


            FileTab tab = new FileTab(doc, this);
            fileBar.addTab(tab);
            select(tab);


        }
    }

    public void menuSave(){
        if(getActiveDocument() == null) return;

        if(getActiveDocument().isFile()){
            getActiveDocument().save();
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(initialDirectory));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Plain text file", "*.txt"), new FileChooser.ExtensionFilter("Any file", "*.*"));

        File file = fc.showSaveDialog(window);

        if(file == null) return;


        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        getActiveDocument().setFilename(file.getAbsolutePath().replace('\\', '/'));

        getActiveDocument().save();

    }

    public void menuSaveAs(){
        if(getActiveDocument() == null) return;

        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(initialDirectory));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Plain text file", "*.txt"), new FileChooser.ExtensionFilter("Any file", "*.*"));

        File file = fc.showSaveDialog(window);

        if(file == null) return;


        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        getActiveDocument().setFilename(file.getAbsolutePath().replace('\\', '/'));

        getActiveDocument().save();

    }

    public void menuClose(){
        if(getActiveDocument() == null) return;
        fileBar.removeTab(fileBar.getSelectedTab());
    }



    public void addStyle(String file){
        scene.getStylesheets().add(getClass().getResource(file).toExternalForm());
    }

    public void select(FileTab tab){
        fileBar.selectTab(tab);
        codeArea.changeActiveDocument();
    }

    public Document getActiveDocument(){

        if(fileBar.getSelectedTab() != null)return fileBar.getSelectedTab().getDocument();
        return null;
    }

    public FileBar getFileBar(){return fileBar;}


    public CodeArea getCodeArea(){
        return codeArea;
    }



    public Editor(){
        windowTitle = "GLSLEditor";
    }


}
