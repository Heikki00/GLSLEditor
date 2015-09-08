package GLSLEditor.Highlighting;

import GLSLEditor.CodeDatabase.CodeDatabase;
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

    private static String SCALAR_PATTERN = "\\b(" + String.join("|", CodeDatabase.GLSLscalars) + ")\\b";
    private static String ALGEBRATYPE_PATTERN = "\\b(" + String.join("|", CodeDatabase.GLSLalgebraTypes) + ")\\b";
    private static String COMMENT_PATTERN = "(//[^\n]*)|(/\\*.*\\*/)";

    private static Pattern PATTERN = Pattern.compile("(?<SCALAR>" + SCALAR_PATTERN + ")"
            + "|(?<ALGEBRATYPE>" + ALGEBRATYPE_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"

    , Pattern.DOTALL);





    public static StyleSpans<Collection<String>> highlight(String text){


        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();

        while(matcher.find()) {
            String styleClass = matcher.group("SCALAR") != null ? "scalar" :
                    matcher.group("ALGEBRATYPE") != null ? "algebratype" :
                            matcher.group("COMMENT") != null ? "comment" :
                    "";


            if(styleClass.isEmpty())throw new InputMismatchException("ERROR: Highlight macher found a match that is not part of any group");

            char charBefore = matcher.start() == 0 ? ' ' : text.charAt(matcher.start() - 1);

            if(true) {
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();
            }
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();




    }



}
