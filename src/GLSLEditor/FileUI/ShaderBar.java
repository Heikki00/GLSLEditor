package GLSLEditor.FileUI;

import GLSLEditor.Document;
import GLSLEditor.Editor;
import GLSLEditor.Project;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * Created by Heikki on 10.10.2015.
 */
public class ShaderBar {
    private Editor editor;
    private HBox bar;
    private Map<String, Label> map;


    public ShaderBar(Editor editor, HBox bar){
        this.editor = editor;
        this.bar = bar;
        map = new HashMap<>();

        map.put("name", new Label());
        map.put("vs", new Label("VS"));
        map.put("tc", new Label("TC"));
        map.put("ts", new Label("TS"));
        map.put("gs", new Label("GS"));
        map.put("fs", new Label("FS"));
        bar.getChildren().add(map.get("name"));
        bar.getChildren().add(map.get("vs"));
        bar.getChildren().add(map.get("tc"));
        bar.getChildren().add(map.get("ts"));
        bar.getChildren().add(map.get("gs"));
        bar.getChildren().add(map.get("fs"));

        for(String s : map.keySet()){

            map.get(s).setId("ProjectTab_nono");

            map.get(s).setMaxWidth(1000);
            bar.setHgrow(map.get(s), Priority.ALWAYS);


            map.get(s).setOnMouseEntered(e -> {
                if (editor.getProject() != null) map.get(s).setId("ProjectTab_hover");
            });

            map.get(s).setOnMouseExited(e -> {
                if (editor.getProject() == null) map.get(s).setId("ProjectTab_nono");
                else if (!editor.getProject().hasDocument(s)) map.get(s).setId("ProjectTab_no");
                else map.get(s).setId("ProjectTab_yes");
            });


            map.get(s).setOnMouseClicked(e -> {
                if (editor.getProject() == null) return;
                if (s.equals("name")) return;
                if (editor.getProject().hasDocument(s)) {
                    if (editor.getFileBar().hasTab(editor.getProject().getDocument(s))) {
                        editor.select(editor.getFileBar().getTab(editor.getProject().getDocument(s)));

                    } else {
                        editor.getFileBar().addTab(new FileTab(editor.getProject().getDocument(s), editor));
                        editor.select(editor.getFileBar().getTab(editor.getProject().getDocument(s)));


                    }

                    return;
                }


                Alert a = new Alert(Alert.AlertType.CONFIRMATION);

                a.setTitle("Add Shader");
                a.setHeaderText("Do you want to create new file, add currently active file, open file or remove this stage?");

                ButtonType newFile = new ButtonType("New File");
                ButtonType openFile = new ButtonType("Open File");
                ButtonType thisFile = new ButtonType("This File");
                ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                a.getButtonTypes().setAll(newFile, openFile, thisFile, cancel);


                Optional<ButtonType> result = a.showAndWait();

                if (result.get() == newFile) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Create new file");
                    fileChooser.setInitialDirectory(new File(editor.getProject().getRelativeFolder()));
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("GLSL shader", "*." + s));

                    File file = fileChooser.showSaveDialog(null);


                    if (file == null) return;
                    if (file.exists()) file.delete();
                    try {

                        file.createNewFile();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }


                    Document doc = new Document(file.getAbsolutePath().replace("\\", "/"));

                    FileTab tab = new FileTab(doc, editor);
                    editor.getFileBar().addTab(tab);
                    editor.select(tab);

                    editor.getProject().setDocument(s, doc);

                    map.get(s).setId("ProjectTab_yes");
                } else if (result.get() == thisFile) {
                    Document doc = editor.getActiveDocument();
                    if (doc == null || !doc.isFile()) return;

                    String docPath = doc.getAsFile().getAbsolutePath().replace("\\", "/");
                    if (!docPath.substring(docPath.lastIndexOf('.')).equals("." + s)) return;


                    editor.getProject().setDocument(s, doc);

                    map.get(s).setId("ProjectTab_yes");
                } else if (result.get() == openFile){
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Open file");
                    fileChooser.setInitialDirectory(new File(editor.getProject().getRelativeFolder()));
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("GLSL shader", "*." + s));

                    File file = fileChooser.showOpenDialog(null);


                    if (file == null) return;

                    Document doc = new Document(file.getAbsolutePath().replace("\\", "/"));

                    FileTab tab = new FileTab(doc, editor);
                    editor.getFileBar().addTab(tab);
                    editor.select(tab);

                    editor.getProject().setDocument(s, doc);

                    map.get(s).setId("ProjectTab_yes");



                }


            });



        };








    }



    public void setProject(Project p){

        if(p == null){
            map.get("name").setText("");
            for(String s : map.keySet()){
                map.get(s).setId("ProjectTab_nono");
            };
            return;
        }


        map.get("name").setText(p.getName());


        if(!p.hasDocument("vs")) map.get("vs").setId("ProjectTab_no");
        if(p.hasDocument("vs")) map.get("vs").setId("ProjectTab_yes");

        if(!p.hasDocument("tc")) map.get("tc").setId("ProjectTab_no");
        if(p.hasDocument("tc")) map.get("tc").setId("ProjectTab_yes");

        if(!p.hasDocument("ts")) map.get("ts").setId("ProjectTab_no");
        if(p.hasDocument("ts")) map.get("ts").setId("ProjectTab_yes");

        if(!p.hasDocument("gs")) map.get("gs").setId("ProjectTab_no");
        if(p.hasDocument("gs")) map.get("gs").setId("ProjectTab_yes");

        if(!p.hasDocument("fs")) map.get("fs").setId("ProjectTab_no");
        if(p.hasDocument("fs")) map.get("fs").setId("ProjectTab_yes");

    }











}
