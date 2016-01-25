package GLSLEditor.Highlighting;

import GLSLEditor.CodeDatabase.CodeDatabase;
import GLSLEditor.CodeDatabase.GLSLVariable;
import GLSLEditor.Editor;
import GLSLEditor.Util.Range;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.time.temporal.ValueRange;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Heikki on 4.8.2015.
 */
public class Highlighter {
    private static Editor editor;
    public static void init(Editor editor){
        editor.addStyle("Highlighting/HighlightStyles.css");
        Highlighter.editor = editor;

    }

    private static String SCALAR_PATTERN = "\\b(" + String.join("|", CodeDatabase.GLSLscalars) + ")\\b";
    private static String ALGEBRATYPE_PATTERN = "\\b(" + String.join("|", CodeDatabase.GLSLalgebraTypes) + ")\\b";
    private static String COMMENT_PATTERN = "(//[^\n]*)|(/\\*.*\\*/)";
    private static String KEYWORD_PATTERN = "\\b(" + String.join("|", CodeDatabase.GLSLKeywords) + ")\\b";


    private static List<Range> errors = new ArrayList<>();




    public static void highlight(String text){
        ArrayList<String> variableNames = new ArrayList<>(), defvariableNames = new ArrayList<>();

        for(GLSLVariable v : CodeDatabase.variables){
            variableNames.add(v.getName());
        }

        for(GLSLVariable v : CodeDatabase.defaultVariables){
            defvariableNames.add(v.getName());

        }

        Matcher matcher =  Pattern.compile("(?<SCALAR>" + SCALAR_PATTERN + ")"
                + "|(?<ALGEBRATYPE>" + ALGEBRATYPE_PATTERN + ")"
                + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                + "|(?<VARIABLE>" + "\\b(" + String.join("|", variableNames) + ")\\b" + ")"
                + "|(?<DEFAULTVARIABLE>" + "\\b(" + String.join("|", defvariableNames) + ")\\b" + ")"
                , Pattern.DOTALL).matcher(text);

        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();

        while(matcher.find()) {
            String styleClass = matcher.group("SCALAR") != null ? "scalar" :
                    matcher.group("ALGEBRATYPE") != null ? "algebratype" :
                            matcher.group("COMMENT") != null ? "comment" :
                                    matcher.group("KEYWORD") != null ? "keyword" :
                                            matcher.group("VARIABLE") != null ? "variable" :
                                                    matcher.group("DEFAULTVARIABLE") != null ? "defaultvariable" :
                    "";


            if(styleClass.isEmpty())throw new InputMismatchException("ERROR: Highlight macher found a match that is not part of any group");

            char charBefore = matcher.start() == 0 ? ' ' : text.charAt(matcher.start() - 1);


                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();

        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);


        org.fxmisc.richtext.StyleSpans<Collection<String>> styles = spansBuilder.create();


        editor.getCodeArea().setStyle(styles);











    }

//Adds range that will have error style. Removes itself automatically
    public static void addError(int start, int end){
        errors.add(new Range(start, end));


    }



}
