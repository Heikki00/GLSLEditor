package GLSLEditor.Highlighting;

import GLSLEditor.CodeDatabase.CodeDatabase;
import GLSLEditor.CodeDatabase.GLSLFunction;
import GLSLEditor.CodeDatabase.GLSLType;
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
    private static String PREPROCESSOR_PATTERN = "[ \n]*(" + String.join("|", CodeDatabase.GLSLPreprocessor) + "\\w+)";
    private static String NUMBER_PATTERN = "\\b([1-9.]+)\\b";
    private static String STRING_PATTERN = "(\"[^\"]*\")";
    private static String SAMPLER_PATTERN = "\\b(" + String.join("|", CodeDatabase.GLSLSamplers) + ")\\b";

    private static List<Range> errors = new ArrayList<>();




    public static void highlight(String text){

        //Create the matcher from pattern
        Matcher matcher =  Pattern.compile(generateStylePattern(), Pattern.DOTALL).matcher(text);

        //lastKwEnd is the end of the latest highlighted area
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();


        while(matcher.find()) {
            //Find what class the highlighted word is
            String styleClass = matcher.group("SCALAR") != null ? "scalar" :
                    matcher.group("ALGEBRATYPE") != null ? "algebratype" :
                            matcher.group("COMMENT") != null ? "comment" :
                                    matcher.group("KEYWORD") != null ? "keyword" :
                                            matcher.group("PREPROCESSOR") != null ? "preprocessor" :
                                                  matcher.group("VARIABLE") != null ? "variable" :
                                                       matcher.group("DEFAULTVARIABLE") != null ? "defaultvariable" :
                                                            matcher.group("DEFAULTFUNCTION") != null ? "defaultfunction" :
                                                                    matcher.group("NUMBER") != null ? "number" :
                                                                            matcher.group("SAMPLER") != null ? "sampler" :
                                                                                matcher.group("STRING") != null ? "string" :
                                                                                    matcher.group("FUNCTION") != null ? "function" :
                                                                                        matcher.group("USERTYPES") != null ? "usertypes" :
                    "";




            //If something is found but it wasn't any class(can't see how that would happen)
            if(styleClass.isEmpty())throw new InputMismatchException("ERROR: Highlight macher found a match that is not part of any group");



            //First add empty style from last highlight's end to this highlight's start. Then add correct style for this word
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastKwEnd = matcher.end();

        }
        //Add empty style till' the end!
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);


        org.fxmisc.richtext.StyleSpans<Collection<String>> styles = spansBuilder.create();


        editor.getCodeArea().setStyle(styles);











    }

    //Internal function to generate a regex to find styles with
    private static String generateStylePattern(){
        //Lists that contain the names that should be highlighted
        ArrayList<String> variableNames = new ArrayList<>(), defvariableNames = new ArrayList<>(), defaultFunctions = new ArrayList<>(), functions = new ArrayList<>(), userTypes = new ArrayList<>();

        //User-defined variable names
        for(GLSLVariable v : CodeDatabase.variables){
            variableNames.add(v.getName());

        }

        //Default variables (gl_Position etc.)
        for(GLSLVariable v : CodeDatabase.defaultVariables){
            defvariableNames.add(v.getName());

        }

        //Default functions (cos() etc.)
        for(GLSLFunction f : CodeDatabase.defaultFunctions){
            defaultFunctions.add(f.getName());

        }

        //User-defined functions
        for(GLSLFunction f : CodeDatabase.functions){
            functions.add(f.getName());
        }

        //User-defined types
        for(GLSLType t : CodeDatabase.userTypes){
            userTypes.add(t.getName());
        }

        String pattern = "(?<SCALAR>" + SCALAR_PATTERN + ")"
                + "|(?<ALGEBRATYPE>" + ALGEBRATYPE_PATTERN + ")"
                + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                + "|(?<PREPROCESSOR>" + PREPROCESSOR_PATTERN + ")"
                + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                + "|(?<STRING>" + STRING_PATTERN + ")"
                + "|(?<SAMPLER>" + SAMPLER_PATTERN + ")"
                + "|(?<DEFAULTVARIABLE>" + "\\b(" + String.join("|", defvariableNames) + ")\\b" + ")"
                + "|(?<DEFAULTFUNCTION>" + "\\b(" + String.join("|", defaultFunctions) + ")\\b" + ")";


        //If the arrays are empty, set the group to unmatchable regex
        if(!variableNames.isEmpty()) pattern +=  "|(?<VARIABLE>" + "\\b(" + String.join("|", variableNames) + ")\\b" + ")";
        else pattern +=  "|(?<VARIABLE>(/(?!)/))";

        if(!functions.isEmpty()) pattern += "|(?<FUNCTION>" + "\\b(" + String.join("|", functions) + ")\\b" + ")";
        else pattern +=  "|(?<FUNCTION>(/(?!)/))";

        if(!userTypes.isEmpty()) pattern += "|(?<USERTYPES>" + "\\b(" + String.join("|", userTypes) + ")\\b" + ")";
        else pattern +=  "|(?<USERTYPES>(/(?!)/))";

        return pattern;

    }



}
