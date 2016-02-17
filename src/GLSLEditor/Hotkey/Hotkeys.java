package GLSLEditor.Hotkey;

import javafx.scene.input.KeyCombination;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Heikki on 2.8.2015.
 */

//Class that (hopefully) contains all of the hotkeys as key-value pairs. Contains only static functions and variables
public class Hotkeys {
    private static Map<String, Hotkey> hotkeyMap;

    static {
        hotkeyMap = new HashMap<>();
    }

    //Adds the hotkey to this class. Add all hotkeys to this class
    public static void setHotkey(String name, Hotkey hotkey){
        hotkeyMap.put(name, hotkey);
    }

    //Returns the hotkey that has been named name. If a hotkey was not found, returns null
    public static Hotkey getHotkey(String name){
        return hotkeyMap.get(name);
    }







}
