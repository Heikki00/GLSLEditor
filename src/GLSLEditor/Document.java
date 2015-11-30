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

//Class that represents a singe document. Might or might not be actual file, and contents might not march those of files.
public class Document {

    private StringProperty text;
    private StringProperty filename;
    private BooleanProperty saved;


    //Constructs an empty document that has no file
    public Document(){
        filename = new SimpleStringProperty("New Document");
        saved = new SimpleBooleanProperty();
        saved.set(false);
        text = new SimpleStringProperty("");
    }

    //Constructs a document from a file
    public Document(String filename){

        this.filename = new SimpleStringProperty(filename);
        this.text = new SimpleStringProperty("");
        saved = new SimpleBooleanProperty();
        load(filename);
        saved.set(true);
    }

    //Sets the text of document to files text
    public void load(String filename){
        try {
            text.setValue(new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8));
            text.setValue(text.getValue().replace("\r", ""));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    //Saves the document. If this document has no file, does nothing
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
        return text.getValue();
    }

    public void setText(String text){this.text.setValue(text); saved.set(false);}

    public StringProperty getTextProperty(){return text;}

    //Returns the name of the file, with extension, without path. Used in UI elements.
    public String getName(){


        if( filename.get().contains("/")){
            return filename.get().substring(filename.get().lastIndexOf('/') + 1);

        }


        return filename.get();

    }



}
