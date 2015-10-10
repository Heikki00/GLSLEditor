package GLSLEditor;


import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Document {

    private String text;
    private StringProperty filename;
    private BooleanProperty saved;



    public Document(){
        filename = new SimpleStringProperty("New Document");
        saved = new SimpleBooleanProperty();
        saved.set(false);
        text = "";
    }

    public Document(String filename){

        this.filename = new SimpleStringProperty(filename);
        load(filename);
        saved = new SimpleBooleanProperty();
        saved.set(true);
    }


    public void load(String filename){
        try {
            text = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
            text = text.replace("\r", "");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void save(){
        if(!isFile()) return;

        try {
            Files.write(Paths.get(filename.get()), getText().getBytes());
            saved.set(true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public boolean isSaved(){return saved.get(); }

    public BooleanProperty getSavedProperty(){return saved;}

    public boolean isFile(){return Files.exists(Paths.get(filename.get()));}

    public File getAsFile(){if(!isFile()) return null; return new File(filename.get());}

    public String getFilename(){
        return filename.get();
    }

    public void setFilename(String s){filename.set(s);}

    public StringProperty getFilenameProperty(){
        return filename;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){this.text = text; saved.set(false);}

    public String getName(){


        if( filename.get().contains("/")){
            return filename.get().substring(filename.get().lastIndexOf('/') + 1);

        }


        return filename.get();

    }



}
