package GLSLEditor.AutoComplete;


import GLSLEditor.CodeDatabase.CodeDatabase;
import GLSLEditor.CodeDatabase.GLSLType;
import GLSLEditor.Editor;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import org.fxmisc.richtext.PopupAlignment;
import org.fxmisc.richtext.TwoDimensional;

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

        if (newVal.charAt(cPos - 1) != ' ' && newVal.charAt(cPos - 1) != '\n') {


                int spacePos = cPos - 1;
                while(newVal.charAt(spacePos) != ' '&& newVal.charAt(spacePos) != '\n'){
                    if(spacePos == 0){
                        break;
                    }

                    spacePos--;


                }


            String typed = build.substring(spacePos == 0 ? 0 : spacePos + 1, cPos);




            contextMenu.getItems().clear();



                for(String name : CodeDatabase.variableTypeStrings){
                    if(name.matches("mat\\dx\\d")) continue;
                    if(!typed.isEmpty() && name.startsWith(typed)){
                        if(typed.contains(name)) continue;
                        MenuItem m = new MenuItem(name);
                        Integer start = spacePos == 0 ? 0 : spacePos + 1;
                        Integer end = cPos;
                        m.setOnAction(e -> codeArea.replaceText(start, end, name));
                        contextMenu.getItems().add(m);
                    }

                }


                if (!codeArea.getPopupWindow().isShowing() && !contextMenu.getItems().isEmpty()){
                    codeArea.getPopupWindow().show(editor.getWindow());

                }

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



    }

    public static int getCursorPos(){return cursorPos;}

    public static String getCompleted(){
        return completed;
    }

    public static boolean isCompleted(String val){
        return val.equals(completed);
    }










}
