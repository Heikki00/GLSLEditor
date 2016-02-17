package GLSLEditor.Hotkey;


import GLSLEditor.Editor;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;

import javafx.scene.input.KeyEvent;

import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.EventPattern.*;
import java.util.ArrayList;
import java.util.List;


//Class that represents a single hotkey. Consists of a single KeyCombination and a list of callbacks.
//Should be added to Hotkeys.setHotkey() so all hotkeys can be easily edited
public class Hotkey {


    private KeyCombination keyCombination;
    private List<HotkeyCallback> callbacks;
    private Editor editor;

    //Constructs a Hotkey. kc is the combination that is desired, and after that user can add as many callbacks as they want
    public Hotkey(Editor editor, KeyCombination kc, HotkeyCallback... hcc){
        keyCombination = kc;

        callbacks = new ArrayList<>();
        this.editor = editor;
        for(HotkeyCallback call : hcc){
            callbacks.add(call);

        }

        //Little help from RichTextFX to install the hotkey to the codeArea
        EventHandler<? super KeyEvent> activated = EventHandlerHelper
                .on(EventPattern.keyPressed(keyCombination)).act(e -> {
                    for (HotkeyCallback call : callbacks) call.onHotkey();
                }).create();


        EventHandlerHelper.install(editor.getCodeArea().getArea().onKeyPressedProperty(), activated);

    }

    //Constructs a Hotkey. kc is the combination that is desired.
    public Hotkey(Editor editor,KeyCombination kc){
        keyCombination = kc;
        callbacks = new ArrayList<>();


        this.editor = editor;

        EventHandler<? super KeyEvent> activated = EventHandlerHelper
                .on(EventPattern.keyPressed(keyCombination)).act(e -> {
                    for (HotkeyCallback call : callbacks) call.onHotkey();
               }).create();


        EventHandlerHelper.install(editor.getCodeArea().getArea().onKeyPressedProperty(), activated);


    }

    //Returns all of the callbacks of this hotkey
    public List<HotkeyCallback> getCallbacks() {
        return callbacks;
    }

    //Returns the keyCombination of this hotkey
    public KeyCombination getKeyCombination() {
        return keyCombination;
    }

    //Sets the KeyCombination of this hotkey
    public void setKeyCombination(KeyCombination keyCombination) {

        this.keyCombination = keyCombination;

    }

    //Adds a new callback to this hotkey
    public void addCallback(HotkeyCallback hc){
        callbacks.add(hc);
    }




}
