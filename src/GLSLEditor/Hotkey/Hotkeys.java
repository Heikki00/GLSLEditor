package GLSLEditor.Hotkey;

import javafx.scene.input.KeyCombination;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Heikki on 2.8.2015.
 */
public class Hotkeys {
    private static Map<String, Hotkey> hotkeyMap;

    static {
        hotkeyMap = new HashMap<>();
    }


    public static void setHotkey(String name, Hotkey hotkey){
        hotkeyMap.put(name, hotkey);
    }

    public static Hotkey getHotkey(String name){
        return hotkeyMap.get(name);
    }







}
