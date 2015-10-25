package GLSLEditor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Heikki on 10.10.2015.
 */
public class Project {
    private Map<String, Document> documents;
    private Document own;
    private Editor editor;
    private String relativePathStart;
    private String shadersFilePath;


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

    public void setShadersFile(String file){
        shadersFilePath = file;

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

           if(shadersFilePath.isEmpty()){
               throw new IllegalStateException("ERROR: Tried to compile shader " + own.getFilename() + " without.shaders file!");

           }

            Document shaders = new Document(shadersFilePath);

            String text = documents.get(s).getText();

            String parsedText = parseIncludes(text, new ArrayList<>());

            StringBuilder shadersText = new StringBuilder(shaders.getText());

            if(shadersText.length() == 0){
                shadersText.append("\n");
                shadersText.append((char)255);

            }

            int markIndex =  shadersText.indexOf(Character.toString((char) 255));


            String relativeFilename = documents.get(s).getFilename().substring(relativePathStart.length());

            String shaderTable = shadersText.substring(0, markIndex);

            if(shaderTable.contains(relativeFilename)){
                    Pattern p  = Pattern.compile("([* \n]+) (\\d+) (\\d+)\n");
                    Matcher m = p.matcher(shaderTable);
                    StringBuffer b = new StringBuffer();

                    int change = 0;

                    while(m.find()){
                        String fileName = m.group(1);
                        int start = Integer.parseInt(m.group(2));
                        int end = Integer.parseInt(m.group(3));

                        if(fileName.equals(relativeFilename)){
                            shadersText.replace(start, end, parsedText);
                            change = parsedText.length() - (end - start);
                            m.appendReplacement(b, fileName + " " + start + " " + (end + change) + "\n");
                            continue;
                        }

                        m.appendReplacement(b, fileName + " " + (start + change) + " " + (end + change) + "\n");


                    }

                shadersText.replace(0, markIndex, b.toString());

            }else{

                shadersText.insert(markIndex, relativeFilename + " " + shadersText.)


            }




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
