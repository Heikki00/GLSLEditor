package GLSLEditor.CodeDatabase;

import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * Created by heikki.simojoki on 7.12.2015.
 */
public class GLSLFunction {
    private GLSLType returnType;
    private List<Pair<GLSLType, String>> parameters;
    private String name;

    public GLSLFunction(GLSLType returnType, String name, Pair<GLSLType, String> ... parameters) {
        this.returnType = returnType;
        this.parameters = Arrays.asList(parameters);
        this.name = name;
    }

    public GLSLType getReturnType() {
        return returnType;
    }

    public List<Pair<GLSLType, String>> getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }
}
