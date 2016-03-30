package GLSLEditor.AutoComplete;


import GLSLEditor.CodeDatabase.CodeDatabase;
import GLSLEditor.CodeDatabase.GLSLFunction;
import GLSLEditor.CodeDatabase.GLSLType;
import GLSLEditor.CodeDatabase.GLSLVariable;
import GLSLEditor.Document;
import GLSLEditor.Editor;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import org.fxmisc.richtext.MouseOverTextEvent;
import org.fxmisc.richtext.PopupAlignment;
import org.fxmisc.richtext.TwoDimensional;

import javafx.stage.Popup;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AutoComplete {
    private static String completed;
    private static int cursorPos;
    private static ContextMenu contextMenu;
    private static Editor editor;
    private static org.fxmisc.richtext.CodeArea codeArea;


    public static void complete(String oldVal, String newVal, int cPos) {

        AutoComplete.cursorPos = cPos;
        completed = newVal;
        if (cursorPos == 0){
            contextMenu.getItems().clear();
            contextMenu.hide();
            codeArea.requestFocus();
            return;
        };

        StringBuilder build = new StringBuilder(newVal);

        //If only one character is inputted
        if(oldVal.length() + 1 == newVal.length()) {

            char open[] = {'(', '{', '['};
            char close[] = {')', '}', ']'};

            //Loop through autocomplitable characters
            for(int i = 0; i < 3; ++i){

                //If the opening char was the input and the last charater, just add the closing char
                if(build.charAt(cursorPos - 1) == open[i] && cursorPos == build.length()) build.insert(cursorPos, close[i]);

                //If opening char was inputted
                if (build.charAt(cursorPos - 1) == open[i])
                    //If char after opening is space, line end or any other permitted character
                    if (build.charAt(cursorPos) == ' ' || build.charAt(cursorPos) == '\n' ||
                            build.charAt(cursorPos) == close[i == 0 ? 1 : i == 1 ? 2 : 0] || build.charAt(cursorPos) == close[i == 0 ? 2 : i == 1 ? 0 : 1] ||
                            build.charAt(cursorPos) == open[i == 0 ? 1 : i == 1 ? 2 : 0] || build.charAt(cursorPos) == open[i == 0 ? 2 : i == 1 ? 0 : 1])
                                     build.insert(cursorPos, close[i]);





            }




        }

        //If the user typed a character
        if (newVal.charAt(cPos - 1) != ' ' && newVal.charAt(cPos - 1) != '\n') {

                //Find first space, or start of the document
                int spacePos = cPos - 1;
                while(newVal.charAt(spacePos) != ' '&& newVal.charAt(spacePos) != '\n'){
                    if(spacePos == 0){
                        break;
                    }

                    spacePos--;


                }

            //What has been typed so far
            String typed = build.substring(spacePos == 0 ? 0 : spacePos + 1, cPos);




            contextMenu.getItems().clear();

                //Variable types(vec3, float, mat3x4 etc.)
                for(String name : CodeDatabase.variableTypeStrings){
                    //If it is mat4x4 for example, just skip it
                    if(name.matches("mat\\dx\\d")) continue;

                    //If the name maches
                    if(!typed.isEmpty() && name.startsWith(typed)){
                        //if it is complete, just continue
                        if(typed.contains(name)) continue;

                        //Create menuitem and set action to replace the text with the name
                        MenuItem m = new MenuItem(name);
                        Integer start = spacePos == 0 ? 0 : spacePos + 1;
                        Integer end = cPos;
                        m.setOnAction(e -> codeArea.replaceText(start, end, name));
                        contextMenu.getItems().add(m);
                    }

                }


            //Default variables(gl_Position, gl_in, gl_FragCoord etc.)
                for(GLSLVariable v : CodeDatabase.defaultVariables){
                    //If the name maches
                    if(!typed.isEmpty() && v.getName().startsWith(typed)){
                        //if it is complete, just continue
                        if(typed.contains(v.getName())) continue;

                        //Create menuitem and set action to replace the text with the name
                        MenuItem m = new MenuItem(v.getName());
                        Integer start = spacePos == 0 ? 0 : spacePos + 1;
                        Integer end = cPos;
                        m.setOnAction(e -> codeArea.replaceText(start, end, v.getName()));
                        contextMenu.getItems().add(m);
                    }

                }


            //User-defined variables(MVP, tangent, i, etc.)
            for(GLSLVariable v : CodeDatabase.variables){

                //If the name maches
                if(!typed.isEmpty() && v.getName().startsWith(typed)){
                    //if it is complete, just continue
                    if(typed.contains(v.getName())) continue;

                    //If we are not in scope, skip it
                    if(!v.isInScope(cPos)) continue;


                    //Create menuitem and set action to replace the text with the name
                    MenuItem m = new MenuItem(v.getName());
                    Integer start = spacePos == 0 ? 0 : spacePos + 1;
                    Integer end = cPos;
                    m.setOnAction(e -> codeArea.replaceText(start, end, v.getName()));
                    contextMenu.getItems().add(m);


                }

            }

            //Default functions (cos, max, smoothstep etc.)
            for(GLSLFunction f : CodeDatabase.defaultFunctions){

                //If the name maches
                if(!typed.isEmpty() && f.getName().startsWith(typed)){
                    //if it is complete, just continue
                    if(typed.contains(f.getName())) continue;

                    //Create menuitem and set action to replace the text with the name
                    MenuItem m = new MenuItem(f.getName());
                    Integer start = spacePos == 0 ? 0 : spacePos + 1;
                    Integer end = cPos;
                    m.setOnAction(e -> codeArea.replaceText(start, end, f.getName()));
                    contextMenu.getItems().add(m);


                }

            }

            //User-defined functions (foo, calcLight, displaceCoords etc.)
            for(GLSLFunction f : CodeDatabase.functions){

                //If the name maches
                if(!typed.isEmpty() && f.getName().startsWith(typed)){
                    //if it is complete, just continue
                    if(typed.contains(f.getName())) continue;

                    //Create menuitem and set action to replace the text with the name
                    MenuItem m = new MenuItem(f.getName());
                    Integer start = spacePos == 0 ? 0 : spacePos + 1;
                    Integer end = cPos;
                    m.setOnAction(e -> codeArea.replaceText(start, end, f.getName()));
                    contextMenu.getItems().add(m);


                }

            }

            String tillCursor = build.substring(0, cursorPos);
            if(tillCursor.lastIndexOf("(") > tillCursor.lastIndexOf(")")){
                int lastEnd = tillCursor.lastIndexOf(";");
                int lastBrace = tillCursor.lastIndexOf("}") > tillCursor.lastIndexOf("{") ? tillCursor.lastIndexOf("}") : tillCursor.lastIndexOf("{");

                lastEnd = lastEnd > lastBrace ? lastEnd : lastBrace;

                if(tillCursor.lastIndexOf("(") == -1){}
                else{
                    if(lastEnd < tillCursor.lastIndexOf("(")){
                        String s = tillCursor.substring(lastEnd + 1, tillCursor.lastIndexOf("("));
                        s = s.trim();

                        if(CodeDatabase.getFunction(s) != null){
                            System.out.println(s);
                        }else{


                        }




                    }
                }
            }


            if(typed.equals("123")){
                for(GLSLFunction f : CodeDatabase.functions){
                    System.out.println(f.getName());
                }

            }

                //If there is items in there, show the popup
                if (!codeArea.getPopupWindow().isShowing() && !contextMenu.getItems().isEmpty()){
                    codeArea.getPopupWindow().show(editor.getWindow());

                }

                //if no items, hide the popup
                if(contextMenu.getItems().isEmpty() || CodeDatabase.getType(typed) != null){
                    contextMenu.hide();
                    contextMenu.getItems().clear();
                    codeArea.requestFocus();


                }







        }else{
            contextMenu.getItems().clear();
            contextMenu.hide();
            codeArea.requestFocus();

        }






        completed = build.toString();
    }

    public static void init(Editor editor){
        AutoComplete.editor = editor;
        editor.getCodeArea().getArea().setContextMenu(contextMenu);

        completed = "";
        contextMenu = new ContextMenu();

        codeArea = editor.getCodeArea().getArea();



        codeArea.setPopupWindow(contextMenu);

        codeArea.setPopupAlignment(PopupAlignment.CARET_BOTTOM);

        Popup pop = new Popup();

        Label popupMsg = new Label();
        popupMsg.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 5;");
        pop.getContent().add(popupMsg);

        codeArea.setMouseOverTextDelay(Duration.ofSeconds(1));
        codeArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            int chIdx = e.getCharacterIndex();

            Point2D pos = e.getScreenPosition();
            pop.show(codeArea, pos.getX(), pos.getY() + 10);
        });
        codeArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            pop.hide();
        });


    }

    public static int getCursorPos(){return cursorPos;}

    public static String getCompleted(){
        return completed;
    }

    public static boolean isCompleted(String val){
        return val.equals(completed);
    }



    //NOTE: Three parsing functions below are relocated from Project.

    //Returns the parsed text of the specified document. SearchPath is the path from where the included documents should be searched. Should end with "/"
    public static String getDocumentParsed(Document doc, String searchPath){
        return parseIncludes(doc.getText(), searchPath);
    }


    //Parses the includes of some file. Src is full source of the file, returns parsed version of that shader.
    private static String parseIncludes(String src, String path){
        return parseIncludesRecrusive(src, path, new ArrayList<>());

    }

    //Actually does the include parsing, includedFiles is list of files that are already included, used because this function is recrusive. Should be new List when called from elsewhere.
    private static String parseIncludesRecrusive(String src, String path, List<String> includedFiles){




        final String INCLUDE_KEY = "#include";

        //While there is include statements
        while(src.indexOf(INCLUDE_KEY) != -1){

            int keyIndex = src.indexOf(INCLUDE_KEY);

            //Find indexes of quotes
            int firstQ = src.indexOf("\"", keyIndex);
            int lastQ = src.indexOf("\"", firstQ + 1);

            StringBuilder sb = new StringBuilder(src);

            if(firstQ == -1 || lastQ == -1){

                sb.replace(keyIndex, keyIndex + INCLUDE_KEY.length(), "");
                src = sb.toString();

                continue;
            }


            String filename = null;
            try {
                filename = src.substring(firstQ + 1, lastQ);
            } catch (Exception e) {
                System.out.println("F:" + firstQ + "     L:" + lastQ);
                e.printStackTrace();

            }


            //If file has been already included, remove include statement and skip it
            if(includedFiles.contains(filename)){

                sb.replace(keyIndex, lastQ + 1, "");

                src = sb.toString();
                continue;
            }


            String includedText = "";

            //Read the file
            try {
                includedText = new String(Files.readAllBytes(Paths.get(path + filename)), StandardCharsets.UTF_8);
                includedText = includedText.replace("\r", "");
            } catch (Exception e) {
                //If the file was not found, just don't care. Maybe the include statent is being written at the moment etc.
            }

            //Add file to includedFiles and parse it
            includedFiles.add(filename);
            includedText = parseIncludesRecrusive(includedText,path, includedFiles);



            //replace include statement with parsed file
            sb.replace(keyIndex, lastQ + 1, includedText);

            src = sb.toString();

        }


        return src;
    }







}
