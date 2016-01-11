package GLSLEditor;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Class that represents an GLSL Shader project. Includes many shaders, that can be compiled to .shaders file that is shared between projects.
public class Project {
    private Map<String, Document> documents;
    private Document own;
    private Editor editor;
    private String workFolder;
    private String shadersFile;
    private BooleanProperty saved;
    private final static char SHADERS_DELIMITER = '$';
    private org.fxmisc.undo.UndoManager undoManager;

    //Constructs a new Project. Unlike document, project always needs a file.
    public Project(Editor editor, String filename, String workFolder){
        documents = new HashMap<>();
        this.editor = editor;
        own = new Document(filename);
        saved = new SimpleBooleanProperty(false);

        this.workFolder = workFolder;


        Scanner scan = new Scanner(own.getText());
        scan.useDelimiter("\n");
        //Read the .glsl file. First line contains .shaders file path relative to work folder
        //Rest of the lines contain an identifier of shader(2 chars long) and relative filename to that shader
        int num = 0;
        while(scan.hasNext()){
            saved.setValue(true);
            if(num == 0){
                String s = scan.nextLine();
                workFolder = s;
                System.out.println(workFolder);
                continue;
            }

            else if(num == 1){
                String s = scan.nextLine();
                shadersFile = s;
                continue;
            }

            String s = scan.nextLine();
            if(s.isEmpty()) continue;
            String s1 = workFolder + s.substring(2);

            documents.put(s.substring(0, 2), new Document(s1));
        }

        num++;
    }

    //Sets the .shaders file. Parameter file is a ABSOLUTE path to .shaders file, this function shortens it to relative path.
    public void setShadersFile(String file){
        shadersFile = file.substring(workFolder.length());

    }

    //Sets a shader. Stage is an identifier(vs,gs,tc,ts,fs)
    public void setDocument(String stage, Document doc){
        if(!doc.isFile()) throw new IllegalArgumentException("ERROR: Tried to add non-file document to project");
        if(!doc.getAsFile().getAbsolutePath().replace("\\", "/").startsWith(workFolder)) throw new IllegalArgumentException("ERROR: called Project.setDocument with document that wasn't under project file's directory");
        documents.put(stage, doc);

        doc.getSavedProperty().addListener(e ->{
            saved.setValue(false);

        });

        saved.setValue(false);

    }


    public Document getDocument(String stage){
        return documents.get(stage);

    }


    public void removeDocument(String stage){
        documents.remove(stage);
        saved.setValue(false);
        editor.getShaderBar().updateProject();
    }

    public void removeDocument(Document doc){

        for(String s : documents.keySet()){
            if(documents.get(s).equals(doc)){
                documents.remove(s);
                saved.setValue(false);
                editor.getShaderBar().updateProject();
                return;
            }
        }

        throw new IllegalArgumentException("ERROR: Tried to remove document " + doc.getFilename() + " from project " + own.getFilename());

    }


    public boolean hasDocument(String stage){
        return documents.get(stage) != null;
    }

    public boolean hasDocument(Document doc)
    {
        for(String s : documents.keySet()){
            if(documents.get(s).equals(doc)){

                return true;
            }
        }

        return false;
    }

    //Returns the NAME of .glsl document(note: not filename). Used for UI purposes.
    public String getName(){
        return own.getName();

    }

    //Returns true if the project is saved(compiled)
    public boolean getSaved(){
        return saved.getValue();
    }

    //Self-explanatory, look "getSaved()"
    public BooleanProperty getSavedProperty(){
        return saved;
    }

    //Returns the full filename of .glsl document
    public String getFilename(){return own.getFilename();}

    //Returns path to folder that contains the .glsl file
    public String getWorkFolder(){return workFolder;}

    //Compiles the shader. Parses all the includes of shaders and writes them to .shaders file
    public void compile(){
        own.setText("");

        if(shadersFile == null || shadersFile.isEmpty()){
            throw new IllegalStateException("ERROR: Tried to compile shader " + own.getFilename() + " without.shaders file!");

        }

        own.setText(workFolder + "\n" + shadersFile + "\n");


        //Get text from .shaders file
        Document shaders = new Document(workFolder + shadersFile);
        StringBuilder shadersText = new StringBuilder(shaders.getText());


        //Loop through all the shaders
        for(String s : documents.keySet()){




            //Parse the shader
            String parsedText = parseIncludes(documents.get(s).getText());



            //If the shaders file is empty, add the delimiter
            if(shadersText.length() == 0){
                shadersText.append("\n");
                shadersText.append(SHADERS_DELIMITER);
                shadersText.append("\n");
            }

            int markIndex =  shadersText.indexOf(Character.toString(SHADERS_DELIMITER));



            //Filename of the current shader relative to .glsl file
            String relativeFilename = documents.get(s).getFilename().substring(workFolder.length());

            String shaderTable = shadersText.substring(0, markIndex);

            //If this shader is already stored in this .shaders file
            if(shaderTable.contains(relativeFilename)){
                //(not space or \n) (1 or more digits) (one or more digits)\n
                    Pattern p  = Pattern.compile("([^ \n]+) (\\d+) (\\d+)\n");
                    Matcher m = p.matcher(shaderTable);
                    //Updated shader table
                    StringBuffer b = new StringBuffer();

                //Change of lenght after updating shader source
                    int change = 0;


                    while(m.find()){
                        String fileName = m.group(1);
                        int start = Integer.parseInt(m.group(2));
                        int end = Integer.parseInt(m.group(3));

                        if(fileName.equals(relativeFilename)){

                            //Replace shader source, calculate change of lenght, update values in shader table
                            shadersText.replace(start + markIndex, end + markIndex, parsedText);
                            change = parsedText.length() - (end - start);
                            m.appendReplacement(b, fileName + " " + start + " " + (end + change) + "\n");
                            continue;
                        }

                        m.appendReplacement(b, fileName + " " + (start + change) + " " + (end + change) + "\n");


                    }


                shadersText.replace(0, markIndex, b.toString());

            }else{
                //Add new row to shader table and add the source
                shadersText.insert(markIndex, relativeFilename + " " + (shadersText.length() - markIndex) + " " + (shadersText.length() - markIndex + parsedText.length()) + "\n");

                shadersText.append(parsedText);

            }


            shaders.setText(shadersText.toString());
            shaders.save();

            //Add this shader to .glsl file (with the prefix)
            own.setText(own.getText() + s + relativeFilename + "\n");




            documents.get(s).save();
        }

        own.save();
        saved.setValue(true);

    }

    //Parses the includes of some shader file. Src is full source of the shader, retuns parsed version of that shader.
    private String parseIncludes(String src){
        return parseIncludesRecrusive(src, new ArrayList<>());

    }

    //Actually does the include parsing, includedFiles is list of files that are already included, used because this function is recrusive. Should be new List when called from elsewhere
    private String parseIncludesRecrusive(String src, List<String> includedFiles){




        final String INCLUDE_KEY = "#include";

        //While there is include statements
        while(src.indexOf(INCLUDE_KEY) != -1){

            int keyIndex = src.indexOf(INCLUDE_KEY);

            //Find indexes of quotes
            int firstQ = src.indexOf("\"", keyIndex);
            int lastQ = src.indexOf("\"", firstQ + 1);

            String filename = src.substring(firstQ + 1, lastQ);


            StringBuilder sb = new StringBuilder(src);

            //If file has been already included, remove include statement and skip it
            if(includedFiles.contains(filename)){

                sb.replace(keyIndex, lastQ + 1, "");

                src = sb.toString();
                continue;
            }


            String includedText = "";

            //Read the file
            try {
                includedText = new String(Files.readAllBytes(Paths.get(workFolder + filename)), StandardCharsets.UTF_8);
                includedText = includedText.replace("\r", "");
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Add file to includedFiles and parse it
            includedFiles.add(filename);
            includedText = parseIncludesRecrusive(includedText, includedFiles);



            //replace include statement with parsed file
            sb.replace(keyIndex, lastQ + 1, includedText);

            src = sb.toString();

        }


        return src;
    }








}
