package GLSLEditor;


import GLSLEditor.Highlighting.Highlighter;
import javafx.beans.value.ObservableValue;
import org.fxmisc.richtext.LineNumberFactory;


public class CodeArea {
    private org.fxmisc.richtext.CodeArea area;
    private Editor editor;

    public CodeArea(org.fxmisc.richtext.CodeArea area, Editor editor){
        this.area = area;
        this.editor = editor;


        area.setParagraphGraphicFactory(LineNumberFactory.get(area));

        area.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if(this.editor.getActiveDocument() == null || this.editor.getActiveDocument().getText().equals(newValue)) return;

           this.editor.getActiveDocument().setText(newValue);


            area.setStyleSpans(0, Highlighter.highlight(newValue));
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





}
