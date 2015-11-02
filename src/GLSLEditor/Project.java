package GLSLEditor;

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
    private String relativePathStart;
    private String shadersFilePath;

    //Constructs a new Project. Unlike document, project always needs a file.
    public Project(Editor editor, String filename){
        documents = new HashMap<>();
        this.editor = editor;
        own = new Document(filename);

        relativePathStart = filename.substring(0, filename.lastIndexOf("/") + 1);


        Scanner scan = new Scanner(own.getText());

        //Read the .glsl file. First line contains .shaders file path relative to project file(.glsl)
        //Rest of the lines contain an identifier of shader(2 chars long) and relative filename to that shader
        boolean first = true;
        while(scan.hasNext()){

            if(first){
                String s = scan.nextLine();
                shadersFilePath = s;
                first = false;
                continue;
            }

            String s = scan.nextLine();
            if(s.isEmpty()) continue;
            String s1 = relativePathStart + s.substring(2);

            documents.put(s.substring(0, 2), new Document(s1));
        }


    }

    //Sets the .shaders file. Parameter file is a ABSOLUTE path to .shaders file, this function shortens it to relative path.
    public void setShadersFile(String file){
        shadersFilePath = file.substring(relativePathStart.length());

    }

    //Sets a shader. Stage is an identifier(vs,gs,tc,ts,fs)
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

    //Returns the NAME of .glsl document(note: not filename). Used for UI purposes.
    public String getName(){
        return own.getName();

    }

    //Returns the full filename of .glsl document
    public String getFilename(){return own.getFilename();}

    //Returns path to folder that contains the .glsl file
    public String getRelativeFolder(){return relativePathStart;}

    //Compiles the shader. Parses all the includes of shaders and writes them to .shaders file
    public void compile(){
        own.setText("");

        if(shadersFilePath == null ||shadersFilePath.isEmpty()){
            throw new IllegalStateException("ERROR: Tried to compile shader " + own.getFilename() + " without.shaders file!");

        }

        own.setText(shadersFilePath + "\n");

        //Get text from .shaders file
        Document shaders = new Document(relativePathStart + shadersFilePath);
        StringBuilder shadersText = new StringBuilder(shaders.getText());


        //Loop through all the shaders
        for(String s : documents.keySet()){




            //Parse the shader
            String parsedText = parseIncludes(documents.get(s).getText());



            
            if(shadersText.length() == 0){
                shadersText.append("\n");
                shadersText.append('$');
                shadersText.append("\n");
            }

            int markIndex =  shadersText.indexOf("$");


         //   if(true) return;

            String relativeFilename = documents.get(s).getFilename().substring(relativePathStart.length());

            String shaderTable = shadersText.substring(0, markIndex);

            if(shaderTable.contains(relativeFilename)){
                    Pattern p  = Pattern.compile("([^ \n]+) (\\d+) (\\d+)\n");
                    Matcher m = p.matcher(shaderTable);
                    StringBuffer b = new StringBuffer();

                    int change = 0;


                    while(m.find()){
                        String fileName = m.group(1);
                        int start = Integer.parseInt(m.group(2));
                        int end = Integer.parseInt(m.group(3));

                        if(fileName.equals(relativeFilename)){


                            shadersText.replace(start + markIndex, end + markIndex, parsedText);
                            change = parsedText.length() - (end - start);
                            m.appendReplacement(b, fileName + " " + start + " " + (end + change) + "\n");
                            continue;
                        }

                        m.appendReplacement(b, fileName + " " + (start + change) + " " + (end + change) + "\n");


                    }


                shadersText.replace(0, markIndex, b.toString());

            }else{

                shadersText.insert(markIndex, relativeFilename + " " + (shadersText.length() - markIndex) + " " + (shadersText.length() - markIndex + parsedText.length()) + "\n");

                shadersText.append(parsedText);

            }


            shaders.setText(shadersText.toString());
            shaders.save();

            own.setText(own.getText() + s + documents.get(s).getFilename().substring(relativePathStart.length()) + "\n");


            own.save();

            documents.get(s).save();
        }



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

            //Create file. If filename does not contain .glh extension, add it
            File file = new File(relativePathStart + (filename.contains(".ghl") ? filename : filename + ".glh"));

            String includedText = "";

            //Read the file
            try {
                includedText = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
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
