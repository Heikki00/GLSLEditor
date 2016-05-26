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

//Hub for everything in the Settings.txt file
public class Options {

        private static Editor editor;
        private static String optionsFile = "Settings.txt";
        private static String defaultFolder;

        public static void init(Editor editor){
            Options.editor = editor;
            String text = "";

            //Read the file
            try {
                File file = Editor.getFile(Options.class, optionsFile);
                text = new String(Files.readAllBytes(file.toPath()));
                text = (text.replace("\r", ""));

            } catch (IOException e) {
                e.printStackTrace();
            }

            //Pattern and Matcher for finding hotkeys
            Pattern p  = Pattern.compile("([^ \n]+) ([^ \n]+) ([^ \n]+)");

            Matcher m = p.matcher(text.substring(text.indexOf("<hotkeys>"), text.indexOf("</hotkeys>")));

            //Loop through hotkeys, add them
            while(m.find()){


                try {

                    //Reflection magic, lambdas for hotkeys
                    Method met = Editor.class.getDeclaredMethod(m.group(3));

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
                }


            //Pattern to find key-value pairs
                p  = Pattern.compile("([^ \n]+) ([^\n]+)");

            //Macher to find the default folder
                m = p.matcher(text.substring(text.indexOf("<defFolder>"), text.indexOf("</defFolder>")));

                while(m.find()){
                    if(m.group(1).equals("defFolderProperty")){
                        if(!m.group(2).equals("null")){
                            defaultFolder = System.getProperty(m.group(2));
                            break;
                        }


                    }

                    if(m.group(1).equals("defFolder")){
                        defaultFolder = m.group(2);

                        break;
                    }


                }


            }

            //Returns a path to the default folder specified in Settings.txt
            public static String getDefaultFolder(){
                return defaultFolder;

            }




}
