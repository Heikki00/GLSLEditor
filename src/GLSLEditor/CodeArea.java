package GLSLEditor;


import GLSLEditor.AutoComplete.AutoComplete;
import GLSLEditor.CodeDatabase.CodeDatabase;
import GLSLEditor.Highlighting.Highlighter;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.IndexRange;
import javafx.scene.web.HTMLEditor;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.TwoDimensional;

import java.util.*;


public class CodeArea {
    private org.fxmisc.richtext.CodeArea area;
    private Editor editor;
    private boolean updated;

    public CodeArea(org.fxmisc.richtext.CodeArea area, Editor editor){
        this.area = area;
        this.editor = editor;
        area.setParagraphGraphicFactory(LineNumberFactory.get(area));
        updated = false;


        area.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (this.editor.getActiveDocument() == null || this.editor.getActiveDocument().getText().equals(newValue)) return;

            if(!updated){
                String text = processText(oldValue, newValue);
                area.replaceText(text);
                setCaretPos(AutoComplete.getCursorPos());
                Highlighter.highlight(text);


            }
            else{
                updated = true;

            }



        });

        area.setEditable(false);


        



    }


   public  void changeActiveDocument(){
        area.replaceText(editor.getActiveDocument().getText());
        area.setEditable(true);
    }


    public void disable(){
        area.setEditable(false);
        area.replaceText("");
    }


    public org.fxmisc.richtext.CodeArea getArea(){
        return area;
    }

    public void setStyle(org.fxmisc.richtext.StyleSpans<Collection<String>> styles){
        area.setStyleSpans(0, styles);


    }


    private void setCaretPos(int pos){
        area.replaceText(0, pos, area.getText(0, pos));


    }

    private String processText(String oldVal, String newVal){

        if (newVal.contains("\t")) {

            newVal = newVal.replace("\t", "        ");


        }







        if(!AutoComplete.isCompleted(newVal)){
            int carPos = area.getCaretPosition();

            AutoComplete.complete(oldVal, newVal, carPos);

            newVal = AutoComplete.getCompleted();


        }

        this.editor.getActiveDocument().setText(newVal);

        CodeDatabase.update(newVal);

        updated = false;

        return newVal;
    }


}
