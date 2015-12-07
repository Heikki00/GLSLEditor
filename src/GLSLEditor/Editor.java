package GLSLEditor;


import GLSLEditor.AutoComplete.AutoComplete;
import GLSLEditor.FileUI.FileBar;
import GLSLEditor.FileUI.FileTab;
import GLSLEditor.FileUI.ShaderBar;
import GLSLEditor.Highlighting.Highlighter;
import GLSLEditor.Hotkey.Hotkey;
import GLSLEditor.Hotkey.Hotkeys;
import GLSLEditor.Layouts.MainLayout.MainLayoutController;
import GLSLEditor.Options.Options;
import GLSLEditor.Options.OptionsWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;

//Main class that manages everything. Is the JavaFX Application class, inits the subsystems, 
public class Editor extends Application{
    private Stage window;
    private Scene scene;
    private String windowTitle = "GLSLEditor";
    private CodeArea codeArea;
    private FileBar fileBar;

    private Project project;
    private MainLayoutController controller;
    private ShaderBar shaderBar;



    //Star method of the application. Creates window, loads the first scene, inits static content, sets up hotkeys etc.
    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle(windowTitle);

        //Load main controller and layout
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Layouts/MainLayout/MainLayout.fxml"));
        Parent root = loader.load();
        controller = loader.getController();


        //Create scene
        scene = new Scene(root, 800, 600);
        window.setScene(scene);

        //Set stylesheet and application iconj
        addStyle("Layouts/MainLayout/MainLayoutStyle.css");


        window.getIcons().add(new Image("file:GLSLEditorIcon.png"));

        //Create the elements of main scene
        codeArea = new CodeArea(controller.mainCodeArea, this);
        fileBar = new FileBar(controller.activeFileBar, this);
        shaderBar = new ShaderBar(this, controller.shaderBar);
        controller.mainCodeArea.requestFocus();


        //Init static content. Some might use scene elements, so has to be done after that.
        Highlighter.init(this);
        OptionsWindow.init(this);
        AutoComplete.init(this);
        Options.init(this);

        //Set menu actions and hotkeys
        controller.newMenuItem.setOnAction(e -> menuNew());
      //  Hotkeys.setHotkey("MenuNew", new Hotkey(this, new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN), () -> menuNew()));
       // Hotkeys.setHotkey("MenuNew", new Hotkey(this, KeyCombination.valueOf("Ctrl+N"), () -> menuNew()));

        controller.openMenuItem.setOnAction(e -> menuOpen());
        Hotkeys.setHotkey("MenuOpen", new Hotkey(this, new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN), () -> menuOpen()));


        controller.saveMenuItem.setOnAction(e -> menuSave());
        Hotkeys.setHotkey("MenuSave", new Hotkey(this, new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN), () -> menuSave()));


        controller.saveAsMenuItem.setOnAction(e -> menuSaveAs());
        Hotkeys.setHotkey("MenuSaveAs", new Hotkey(this, new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), () -> menuSaveAs()));

        controller.reloadMenuItem.setOnAction(e -> menuReload());

        controller.closeMenuItem.setOnAction(e -> menuClose());

        controller.optionsMenuItem.setOnAction(e -> OptionsWindow.show());

        controller.newProjectMenuItem.setOnAction(e -> menuNewProject());
        Hotkeys.setHotkey("MenuNewProject", new Hotkey(this, new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), () -> menuNewProject()));

        controller.openProjectMenuItem.setOnAction(e -> menuOpenProject());
        Hotkeys.setHotkey("MenuOpenProject", new Hotkey(this, new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN), () -> menuOpenProject()));

        controller.closeProjectMenuItem.setOnAction(e -> menuCloseProject());

        controller.compileMenuItem.setOnAction(e -> menuCompile());
        Hotkeys.setHotkey("MenuCompile", new Hotkey(this, new KeyCodeCombination(KeyCode.F5), () -> menuCompile()));


        controller.setShadersFileMenuItem.setOnAction(e -> menuSetShaderFile());




        window.show();






    }


    //Create new document and select it
    public void menuNew(){
        FileTab newTab = new FileTab(new Document(), this);
        fileBar.addTab(newTab);

        selectTab(newTab);


    }

    //Open existing document
    public void menuOpen(){
       //create dialog for opening files
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to open");
        fileChooser.setInitialDirectory(new File(Options.getDefaultFolder()));

        File file =  fileChooser.showOpenDialog(null);


        if(file != null) {

            Document doc = new Document(file.getAbsolutePath().replace('\\', '/'));


            //if file is already open, just select it
            if(fileBar.hasTab(doc)) {
                selectTab(fileBar.getTab(doc));
                return;
            }

            //Create new tab and select it
            FileTab tab = new FileTab(doc, this);
            fileBar.addTab(tab);
            selectTab(tab);


        }
    }

    //Save document, create new file if document doesnt have one
    public void menuSave(){
        if(getActiveDocument() == null) return;

        //If document has a file, save and be done with it
        if(getActiveDocument().isFile()){
            getActiveDocument().save();
            return;
        }

        //Create save dialog
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(Options.getDefaultFolder()));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Plain text file", "*.txt"), new FileChooser.ExtensionFilter("Any file", "*.*"));

        File file = fc.showSaveDialog(window);

        if(file == null) return;

        //Create new file
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //set file to document and save
        getActiveDocument().setFilename(file.getAbsolutePath().replace('\\', '/'));

        getActiveDocument().save();

    }

    //Save to a new file, doesn't modify the original file(if there is one)
    public void menuSaveAs(){
        if(getActiveDocument() == null) return;

        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(Options.getDefaultFolder()));
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

    //Loads active document from file. Document loses all changes
    public void menuReload(){
        if(getActiveDocument() == null) return;
        if(!getActiveDocument().isFile()) return;


            //Sets documents text to files text, updates CodeArea to new text and sets saved to true(needs to be done so visuals realize that the document is saved)
            getActiveDocument().load(getActiveDocument().getFilename());
            getCodeArea().updateActiveDocument();
            getActiveDocument().getSavedProperty().setValue(true);




    }

    //Closes current document without saving
    public void menuClose(){
        if(getActiveDocument() == null) return;

        fileBar.removeTab(fileBar.getSelectedTab());
    }

    //Creates new project
    public void menuNewProject(){
        //Create save dialog, because there is no new dialog, and project always needs a file.
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Create project file");
        fileChooser.setInitialDirectory(new File(Options.getDefaultFolder()));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GLSL project", "*.glsl"));

        File file =  fileChooser.showSaveDialog(window);

        if(file == null) return;

        //If user chose an existing file, delete and re-create it(so basically replace it)
        if(file.exists()) file.delete();
        try {
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        project = new Project(this, file.getAbsolutePath().replace("\\", "/"));
        shaderBar.updateProject();

        menuSetShaderFile();




    }


    //Open project file
   public void menuOpenProject(){
       FileChooser fileChooser = new FileChooser();
       fileChooser.setTitle("Select project to open");
       fileChooser.setInitialDirectory(new File(Options.getDefaultFolder()));
       fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("GLSL project", "*.glsl"));

       File file =  fileChooser.showOpenDialog(window);
       if(file == null) return;
       project = new Project(this, file.getAbsolutePath().replace("\\", "/"));
       shaderBar.updateProject();


   }

    //Closes current project file(without compiling)
    public void menuCloseProject(){
        project = null;
        shaderBar.updateProject();



    }

    //Compiles the project
    public void menuCompile(){
        if(project == null) return;
        project.compile();

    }

    //Sets current projects .shaders file
    public void menuSetShaderFile(){

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select .shaders File");
        fileChooser.setInitialDirectory(new File(getProject().getRelativeFolder()));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Shaders file", "*.shaders"), new FileChooser.ExtensionFilter("Any file", "*.*"));

        File file = fileChooser.showOpenDialog(window);
        if(file == null) return;

        project.setShadersFile(file.getAbsolutePath().replace("\\", "/"));


    }


    //Adds stylesheet to scene
    public void addStyle(String file){
        scene.getStylesheets().add(Editor.class.getResource(file).toExternalForm());
    }

    //Selects fileTab from filebar and sets the document to CodeArea
    public void selectTab(FileTab tab){
        fileBar.selectTab(tab);
        codeArea.updateActiveDocument();
    }

    //Returns currently active document, or null if there is no active document
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




}
