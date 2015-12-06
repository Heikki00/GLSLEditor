package GLSLEditor.Options;

import GLSLEditor.Editor;
import GLSLEditor.Hotkey.Hotkey;
import GLSLEditor.Hotkey.Hotkeys;
import javafx.scene.input.KeyCombination;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Heikki on 2.12.2015.
 */
public class Options {

        private static Editor editor;
        private static String optionsFile = "Settings.txt";


        public static void init(Editor editor){
            Options.editor = editor;
            String text = "";

            try {
                File file = new File(Options.class.getResource(optionsFile).getFile());
                text = new String(Files.readAllBytes(file.toPath()));
                text = (text.replace("\r", ""));

            } catch (IOException e) {
                e.printStackTrace();
            }

            Pattern p  = Pattern.compile("([^ \n]+) ([^ \n]+) ([^ \n]+)");

            Matcher m = p.matcher(text.substring(text.indexOf("<hotkeys>"), text.indexOf("</hotkeys>")));

            //System.out.println(text.substring(text.indexOf("<hotkeys>"), text.indexOf("</hotkeys>")));

            while(m.find()){


                try {
                    Method met = editor.getClass().getDeclaredMethod(m.group(3));

                    Hotkeys.setHotkey(m.group(1), new Hotkey(editor, KeyCombination.valueOf(m.group(2)), () -> {
                        try {
                            met.invoke(editor);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }

                    }));

                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();

                }

                System.out.println(m.group(1) + ":" + m.group(2) + ":" + m.group(3));


                        }


            }







}
