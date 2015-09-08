package GLSLEditor.AutoComplete;


import GLSLEditor.CodeDatabase.CodeDatabase;
import GLSLEditor.CodeDatabase.GLSLType;
import GLSLEditor.Editor;
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
        if (cursorPos == 0) return;

        StringBuilder build = new StringBuilder(newVal);


        if (newVal.charAt(cursorPos - 1) == '(') {
            build.insert(cursorPos, ')');

        }




        if (newVal.charAt(cPos - 1) != ' ') {


                int spacePos = cPos - 1;
                while(newVal.charAt(spacePos) != ' '){
                    if(spacePos == 0){
                        break;
                    }

                    spacePos--;


                }


            String typed = build.substring(spacePos == 0 ? 0 : spacePos + 1, cPos);




            contextMenu.getItems().clear();



            if(CodeDatabase.getType(typed) == null){

                for(String name : CodeDatabase.variableTypeStrings){
                    if(name.startsWith(typed)){
                        MenuItem m = new MenuItem(name);
                        Integer start = spacePos == 0 ? 0 : spacePos + 1;
                        Integer end = cPos;
                        m.setOnAction(e -> codeArea.replaceText(start, end, name));
                        contextMenu.getItems().add(m);
                    }

                }


                if (!codeArea.getPopupWindow().isShowing()) codeArea.getPopupWindow().show(editor.getWindow());



            }else{
                contextMenu.hide();
                System.out.println("FOUND");
            }



        }else{
            contextMenu.hide();

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
