package GLSLEditor.AutoComplete;


import GLSLEditor.CodeDatabase.CodeDatabase;
import GLSLEditor.CodeDatabase.GLSLFunction;
import GLSLEditor.CodeDatabase.GLSLType;
import GLSLEditor.CodeDatabase.GLSLVariable;
import GLSLEditor.Document;
import GLSLEditor.Editor;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.util.Pair;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            completeBracesEtc(build);

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


            addMenuItems(typed, spacePos, cPos);


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
                        }
                    }
                }
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


        //Really hacky, but seems to work alright...
        IntegerProperty changed = new SimpleIntegerProperty(0);
        codeArea.textProperty().addListener(e ->{

            changed.setValue(2);
        });
        codeArea.caretPositionProperty().addListener(e -> {
            if (changed.get() != 0){
                changed.setValue(changed.get() - 1);
                return;
            }

            contextMenu.getItems().clear();
            contextMenu.hide();
            codeArea.requestFocus();


        });

        Popup pop = new Popup();

        Label popupMsg = new Label();
        popupMsg.setStyle(
                "-fx-background-color: black;" +
                        "-fx-text-fill: white;" +
                        "-fx-padding: 5;");
        pop.getContent().add(popupMsg);

        codeArea.setMouseOverTextDelay(Duration.ofSeconds(1));
        codeArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            //Index of the hover
            int chIdx = e.getCharacterIndex();
            String code = codeArea.getText();
            //The space before the word and the space after the word
            int firstSpace = code.substring(0, chIdx).lastIndexOf(' ');
            int lastSpace = code.indexOf(' ', chIdx);

            //The line break before the word and the line break after the word
            int firstLinebr = code.substring(0, chIdx).lastIndexOf('\n');
            int lastLinebr = code.indexOf('\n', chIdx);

            //If we did not find anything before or after the word, just return
            if ((firstSpace == -1 && firstLinebr == -1) || (lastSpace == -1 && lastLinebr == -1)) return;

            //Start and end of the word. Chooses between space and \n, avoids -1
            int wordStart = firstSpace > firstLinebr ? firstSpace : firstLinebr;
            int wordEnd = lastSpace < lastLinebr ? lastSpace : lastLinebr;
            if (wordEnd == -1) wordEnd = lastSpace > lastLinebr ? lastSpace : lastLinebr;

            //The word that is hovered upon. NOTE: contains semicolons and braces and all that stuff
            String word = code.substring(wordStart, wordEnd);


            //Find the actual words from the block of characters(eg. "foo(MVP,")
            Pattern p = Pattern.compile("[a-zA-Z0-9_]+");
            Matcher m = p.matcher(word);


            while (m.find()){
                //Is the cursor in the found word?
                if(chIdx >= wordStart + m.start() && chIdx < wordStart + m.end()){
                    word = m.group(0);
                }

            }

            //Get the description for the word
            String desc = getHoverString(word);

            popupMsg.setText(desc);
            Point2D pos = e.getScreenPosition();
            //Show the tooltip, if the description is not empty
            if (!desc.isEmpty()) pop.show(codeArea, pos.getX(), pos.getY() + 10);
        });

        codeArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            pop.hide();
        });


    }

    //Internal function, returns a description that is hovered upon. If this function returns an empty string, the tooltip should not be shown.
    private static String getHoverString(String word){


        //If the word is variable, show the type and the name
        if(CodeDatabase.getVariable(word) != null){
            GLSLVariable v = CodeDatabase.getVariable(word);
            return v.getType().getName() + " " + v.getName();

        }

        //If the word is function, show return type, name, parameter typas and names
        else  if(CodeDatabase.getFunction(word) != null){
            GLSLFunction v = CodeDatabase.getFunction(word);
            String res = ( v.getReturnType() == null ? "void" : v.getReturnType().getName()) + " " + v.getName() + "(";

            //Add the parameters to the result
           boolean first = true;
        for(Pair<GLSLType, String> par : v.getParameters(0)){
                if(!first) res += ", ";
                if(first) first = false;

                res += par.getKey().getName() + par.getValue();


            }

            res += ")";
            return res;

        }


        //Looks better
        else if(CodeDatabase.getType(word) != null){
           return CodeDatabase.getType(word).getName();

        }

        return "";


    }

    //Internal function, adds all items to the contex menu.
    private static void addMenuItems(String typed, int spacePos, int cPos){

        contextMenu.getItems().clear();

        //Default variable types(vec3, float, mat3x4 etc.)
        for(GLSLType t : CodeDatabase.variableTypes){
            //If it is mat4x4 for example, just skip it
            if(t.getName().matches("mat\\dx\\d")) continue;

            //If the name maches
            if(!typed.isEmpty() && t.getName().startsWith(typed)){
                //if it is complete, just continue
                if(typed.contains(t.getName())) continue;

                //Create menuitem and set action to replace the text with the name
                MenuItem m = new MenuItem(t.getName());
                Integer start = spacePos == 0 ? 0 : spacePos + 1;
                Integer end = cPos;
                m.setOnAction(e -> codeArea.replaceText(start, end, t.getName()));
                contextMenu.getItems().add(m);
            }

        }

        //User-defined types(Baselight, PointLight)
        for(GLSLType t : CodeDatabase.userTypes){

            //If the name maches
            if(!typed.isEmpty() && t.getName().startsWith(typed)){
                //if it is complete, just continue
                if(typed.contains(t.getName())) continue;

                //Create menuitem and set action to replace the text with the name
                MenuItem m = new MenuItem(t.getName());
                Integer start = spacePos == 0 ? 0 : spacePos + 1;
                Integer end = cPos;
                m.setOnAction(e -> codeArea.replaceText(start, end, t.getName()));
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

        //Child variable autocompletion
        if(!typed.isEmpty() && typed.contains(".")){
            String beforeDot = typed.substring(0, typed.indexOf('.'));
            String afterDot = typed.substring(typed.indexOf('.') + 1);

            for(GLSLVariable v : CodeDatabase.defaultVariables){
                if(beforeDot.equals(v.getName())){
                    for(Pair<GLSLType, String> p : v.getType().getChildren()){
                        MenuItem m = new MenuItem(p.getValue());
                        contextMenu.getItems().add(m);
                    }
                }
            }

            for(GLSLVariable v : CodeDatabase.variables){
                if(beforeDot.equals(v.getName())){
                    //System.out.println(v.getType().getChildren().size());
                    for(Pair<GLSLType, String> p : v.getType().getChildren()){

                       if(p.getValue().startsWith(afterDot)) {
                           MenuItem m = new MenuItem(p.getValue());

                           m.setOnAction(e -> codeArea.replaceText(spacePos + typed.indexOf('.') + 2, cPos, p.getValue()));
                           contextMenu.getItems().add(m);
                       }
                    }
                }
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

    }

    private static void completeBracesEtc(StringBuilder build){
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
