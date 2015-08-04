package GLSLEditor.Highlighting;

import GLSLEditor.Editor;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Heikki on 4.8.2015.
 */
public class Highlighter {

    public static void init(Editor editor){
        editor.addStyle("Highlighting/HighLightStyles.css");
    }

    private static String[] KEYWORDS = new String[]{"bool", "short", "int", "float", "double", "vec2", "vec3", "vec4", "mat2", "mat3", "mat4"};


    private static String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")//b";



    private static Pattern PATTERN = Pattern.compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")");





    public static StyleSpans<Collection<String>> highlight(String text){

        Pattern p = Pattern.compile("(?<KEYWORD>\\b" + String.join("|", KEYWORDS) + "\\b)");


        Matcher matcher = p.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();

        while(matcher.find()) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword" : "";


            if(styleClass.isEmpty())throw new InputMismatchException("ERROR: Highlight macher found a match that is not part of any group");

            char charBefore = matcher.start() == 0 ? ' ' : text.charAt(matcher.start() - 1);
            if(charBefore == ' ' || charBefore == '\n' || charBefore == '\t') {
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();
            }
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();




    }



}
