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


        area.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (this.editor.getActiveDocument() == null || this.editor.getActiveDocument().getText().equals(newValue))
                return;


            String text = processText(oldValue, newValue);
            this.editor.getActiveDocument().setText(text);

            //Does not actually call this listener for some reason
            area.replaceText(text);

            setCaretPos(AutoComplete.getCursorPos());
            Highlighter.highlight(text);


        });

        area.setEditable(false);


        



    }


    //Replaces text in the area with text from currently selected document. Also enables the area.
   public  void updateActiveDocument(){


       area.replaceText(editor.getActiveDocument().getText());
       area.setEditable(true);
       Highlighter.highlight(editor.getActiveDocument().getText());
      area.getUndoManager().forgetHistory();
   }

    //Empties and disables the area
    public void disable(){
        area.setEditable(false);
        area.replaceText("");
    }

    //Returns the CodeArea class form RichTextFX
    public org.fxmisc.richtext.CodeArea getArea(){
        return area;
    }


    public void setStyle(org.fxmisc.richtext.StyleSpans<Collection<String>> styles){
        area.setStyleSpans(0, styles);


    }


    private void setCaretPos(int pos){
        area.replaceText(0, pos, area.getText(0, pos));


    }
    //Fincltion to process inputted text(chages tabs to spaces, autocompletes it, that sort of thing)
    private String processText(String oldVal, String newVal){

        //Change tab to spaces
        if (newVal.contains("\t")) {

            newVal = newVal.replace("\t", "        ");
            area.positionCaret(area.getCaretPosition() + 8);

        }






        //Autocomplete the text
        if(!AutoComplete.isCompleted(newVal)) {
            int carPos = area.getCaretPosition();

            AutoComplete.complete(oldVal, newVal, carPos);

            newVal = AutoComplete.getCompleted();

            area.positionCaret(AutoComplete.getCursorPos());
        }



        CodeDatabase.update(newVal);

        updated = false;

        return newVal;
    }


}
