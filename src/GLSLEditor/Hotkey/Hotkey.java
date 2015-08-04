package GLSLEditor.Hotkey;


import GLSLEditor.Editor;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;

import javafx.scene.input.KeyEvent;


import java.util.ArrayList;
import java.util.List;

import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import static org.fxmisc.wellbehaved.event.EventPattern.*;
import static org.fxmisc.wellbehaved.event.EventPattern.keyPressed;

public class Hotkey {


    private KeyCombination keyCombination;
    private List<HotkeyCallback> callbacks;
    private Editor editor;

    public Hotkey(Editor editor, KeyCombination kc, HotkeyCallback... hcc){
        keyCombination = kc;

        callbacks = new ArrayList<>();
        this.editor = editor;
        for(HotkeyCallback call : hcc){
            callbacks.add(call);

        }


        EventHandler<? super KeyEvent> activated = EventHandlerHelper
                        .on(keyPressed(keyCombination)).act(e -> {
                    for (HotkeyCallback call : callbacks) call.onHotkey();
                }).create();


        EventHandlerHelper.install(editor.getCodeArea().getArea().onKeyPressedProperty(), activated);

    }

    public Hotkey(Editor editor,KeyCombination kc){
        keyCombination = kc;
        callbacks = new ArrayList<>();


        this.editor = editor;

        EventHandler<? super KeyEvent> activated = EventHandlerHelper
                .on(keyPressed(keyCombination)).act(e -> {
                    for (HotkeyCallback call : callbacks) call.onHotkey();
                }).create();


        EventHandlerHelper.install(editor.getCodeArea().getArea().onKeyPressedProperty(), activated);


    }

    public List<HotkeyCallback> getCallbacks() {
        return callbacks;
    }


    public KeyCombination getKeyCombination() {
        return keyCombination;
    }

    public void setKeyCombination(KeyCombination keyCombination) {

        this.keyCombination = keyCombination;

    }


    public void addCallback(HotkeyCallback hc){
        callbacks.add(hc);
    }




}
