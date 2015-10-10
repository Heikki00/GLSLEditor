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


    public CodeArea(org.fxmisc.richtext.CodeArea area, Editor editor){
        this.area = area;
        this.editor = editor;
        area.setParagraphGraphicFactory(LineNumberFactory.get(area));


        area.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (newValue.contains("\t")) {


                area.replaceText(newValue.indexOf('\t'), newValue.indexOf('\t') + 1, "        ");
                return;
            }
            if (this.editor.getActiveDocument() == null || this.editor.getActiveDocument().getText().equals(newValue)) return;


            CodeDatabase.update(newValue);


            this.editor.getActiveDocument().setText(newValue);

            if(!AutoComplete.isCompleted(newValue)){
               int carPos = area.getCaretPosition();

                AutoComplete.complete(oldValue, newValue, carPos);

                area.replaceText(AutoComplete.getCompleted());

                setCaretPos(carPos);

            }



          Highlighter.highlight(newValue);


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

    private void setCaretPos(int pos){
        area.replaceText(0, pos, area.getText(0, pos));


    }




}
