package GLSLEditor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by Heikki on 10.10.2015.
 */
public class Project {
    private Map<String, Document> documents;
    private Document own;
    private Editor editor;
    private String relativePathStart;



    public Project(Editor editor, String filename){
        documents = new HashMap<>();
        this.editor = editor;
        own = new Document(filename);

        relativePathStart = filename.substring(0, filename.lastIndexOf("/") + 1);
        Scanner scan = new Scanner(own.getText());

        while(scan.hasNext()){
            String s = scan.nextLine();
            String s1 = relativePathStart + s.substring(2);
            final String COMPILE_KEY = "_COMPILED";
            s1 = s1.substring(0, s1.indexOf(COMPILE_KEY)) + s1.substring(s1.indexOf(COMPILE_KEY) + COMPILE_KEY.length());

            documents.put(s.substring(0, 2), new Document(s1));
        }


    }


    public void setDocument(String stage, Document doc){
        if(!doc.isFile()) throw new IllegalArgumentException("ERROR: Tried to add non-file document to project");
        if(!doc.getAsFile().getAbsolutePath().replace("\\", "/").startsWith(relativePathStart)) throw new IllegalArgumentException("ERROR: called Project.setDocument with document that wasn't under project file's directory");
        documents.put(stage, doc);

    }

    public Document getDocument(String stage){
        return documents.get(stage);

    }

    public void removeDocument(String stage){
        documents.put(stage, null);
    }

    public boolean hasDocument(String stage){
        return documents.get(stage) != null;
    }

    public String getName(){
        return own.getName();

    }

    public String getAbsolutePath(){return own.getAsFile().getAbsolutePath().replace("\\", "/");}

    public String getRelativeFolder(){return relativePathStart;}

    public void compile(){
        own.setText("");
        for(String s : documents.keySet()){

            File file = new File(documents.get(s).getFilename().substring(0, documents.get(s).getFilename().lastIndexOf('.')) + "_COMPILED." + s);

            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            Document doc = new Document(file.getAbsolutePath().replace("\\", "/"));

            String text = documents.get(s).getText();

            String parsedText = parseIncludes(text, new ArrayList<>());

            doc.setText(parsedText);



            own.setText(own.getText() + s + doc.getAsFile().getAbsolutePath().replace("\\", "/").substring(relativePathStart.length()) + "\n");


            own.save();
            doc.save();
            documents.get(s).save();
        }



    }


    private String parseIncludes(String src, List<String> includedFiles){
        final String INCLUDE_KEY = "#include";

        while(src.indexOf(INCLUDE_KEY) != -1){

            int keyIndex = src.indexOf(INCLUDE_KEY);
            int firstQ = src.indexOf("\"", keyIndex);
            int lastQ = src.indexOf("\"", firstQ + 1);

            String filename = src.substring(firstQ + 1, lastQ);


            StringBuilder sb = new StringBuilder(src);

            if(includedFiles.contains(filename)){

                sb.replace(keyIndex, lastQ + 1, "");

                src = sb.toString();
                continue;
            }

            File file = new File(relativePathStart + (filename.contains(".ghl") ? filename : filename + ".glh"));

            String includedText = "";

            try {
                includedText = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
                includedText = includedText.replace("\r", "");
            } catch (IOException e) {
                e.printStackTrace();
            }

            includedFiles.add(filename);
            includedText = parseIncludes(includedText, includedFiles);




            sb.replace(keyIndex, lastQ + 1, includedText);

            src = sb.toString();

        }


        return src;
    }











}
