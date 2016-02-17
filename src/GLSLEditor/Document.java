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

    //Returns true if the document exists and is saved
    public boolean isSaved(){return saved.get(); }

    public BooleanProperty getSavedProperty(){return saved;}

    //Returns true if this document exists as a file (e.g. it is not a "New Document")
    public boolean isFile(){return Files.exists(Paths.get(filename.get()));}

    //Returns the file of this document, or null if this document does not exist as a file
    public File getAsFile(){if(!isFile()) return null; return new File(filename.get());}

    //Returns the FILENAME of this document (for example: C:/Users/Heikki/Documents/VS.vs)
    public String getFilename(){
        return filename.get();
    }

    //Sets the filename of this document. Effectively changes the documents file
    public void setFilename(String s){filename.set(s);}

    public StringProperty getFilenameProperty(){
        return filename;
    }

    //Return current contents of this document (regardless if the document is saved or not)
    public String getText(){
        return text.getValue();
    }

    //Sets the text of this document
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
