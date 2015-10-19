package GLSLEditor;


import GLSLEditor.AutoComplete.AutoComplete;
import GLSLEditor.FileUI.FileBar;
import GLSLEditor.FileUI.FileTab;
import GLSLEditor.FileUI.ShaderBar;
import GLSLEditor.Highlighting.Highlighter;
import GLSLEditor.Hotkey.Hotkey;
import GLSLEditor.Hotkey.Hotkeys;
import GLSLEditor.Layouts.MainLayout.MainLayoutController;
import GLSLEditor.Options.OptionsWindow;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.TwoDimensional;


import java.io.File;
import java.io.IOException;

//Main class that manages everything. Is the JavaFX Application class, inits the subsystems, 
public class Editor extends Application{
    private Stage window;
    private Scene scene;
    private String windowTitle;
    private CodeArea codeArea;
    private FileBar fileBar;
    private final String initialDirectory = "C:/";
    private Project project;
    private MainLayoutController c;
    private ShaderBar shaderBar;



    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle(windowTitle);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("Layouts/MainLayout/MainLayout.fxml"));
        Parent root = loader.load();
        c = loader.getController();



        scene = new Scene(root, 800, 600);
        window.setScene(scene);

        scene.getStylesheets().add(getClass().getResource("Layouts/MainLayout/MainLayoutStyle.css").toExternalForm());

        codeArea = new CodeArea(c.mainCodeArea, this);
        fileBar = new FileBar(c.activeFileBar, this);
        c.mainCodeArea.requestFocus();



        Hotkeys.setHotkey("Test", new Hotkey(this, new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN), () -> {
            System.out.println("Foo");

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    codeArea.getArea().positionCaret(0);
                }
            });


        }));






        c.newMenuItem.setOnAction(e -> menuNew());
        Hotkeys.setHotkey("MenuNew", new Hotkey(this, new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> menuNew()));


        c.openMenuItem.setOnAction(e -> menuOpen());
        Hotkeys.setHotkey("MenuOpen", new Hotkey(this, new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), () -> menuOpen()));


        c.saveMenuItem.setOnAction(e -> menuSave());
        Hotkeys.setHotkey("MenuSave", new Hotkey(this, new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), () -> menuSave()));


        c.saveAsMenuItem.setOnAction(e -> menuSaveAs());
        Hotkeys.setHotkey("MenuSaveAs", new Hotkey(this, new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), () -> menuSaveAs()));


        c.closeMenuItem.setOnAction(e -> menuClose());

        c.optionsMenuItem.setOnAction(e -> OptionsWindow.show());

        c.newProjectMenuItem.setOnAction(e -> menuNewProject());
        Hotkeys.setHotkey("MenuNewProject", new Hotkey(this, new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), () -> menuNewProject()));

        c.openProjectMenuItem.setOnAction(e -> menuOpenProject());
        Hotkeys.setHotkey("MenuOpenProject", new Hotkey(this, new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), () -> menuOpenProject()));

        c.closeProjectMenuItem.setOnAction(e -> menuCloseProject());

        c.compileMenuItem.setOnAction(e -> menuCompile());
        Hotkeys.setHotkey("MenuCompile", new Hotkey(this, new KeyCodeCombination(KeyCode.F5), () -> menuCompile()));



        shaderBar = new ShaderBar(this, c.shaderBar);

        Highlighter.init(this);
        OptionsWindow.init(this);
        AutoComplete.init(this);



        window.show();

        c.shaderBar.getChildren().add(new Label());


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


    public void menuNewProject(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Create project file");
        fileChooser.setInitialDirectory(new File(initialDirectory));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GLSL project", "*.glsl"));

        File file =  fileChooser.showSaveDialog(window);

        if(file == null) return;
        if(file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        project = new Project(this, file.getAbsolutePath().replace("\\", "/"));
        shaderBar.setProject(project);
    }

   public void menuOpenProject(){
       FileChooser fileChooser = new FileChooser();
       fileChooser.setTitle("Select project to open");
       fileChooser.setInitialDirectory(new File(initialDirectory));
       fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("GLSL project", "*.glsl"));

       File file =  fileChooser.showOpenDialog(window);
       if(file == null) return;
       project = new Project(this, file.getAbsolutePath().replace("\\", "/"));
       shaderBar.setProject(project);


   }


    public void menuCloseProject(){
        project = null;
        shaderBar.setProject(null);



    }


    public void menuCompile(){
        project.compile();

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

    public Project getProject(){return project;}


    public Stage getWindow(){
        return window;
    }



    public Editor(){
        windowTitle = "GLSLEditor";
    }


}
