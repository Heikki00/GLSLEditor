package GLSLEditor.CodeDatabase;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by heikki.simojoki on 7.12.2015.
 */
public class GLSLFunction {


    private GLSLType returnType;
    private List<List<Pair<GLSLType, String>>> parameters;
    private String name;

    //null == void
    public GLSLFunction(GLSLType returnType, String name, Pair<GLSLType, String> ... parameters) {
        this.returnType = returnType;
        this.parameters  = new ArrayList<>();
        this.parameters.add(Arrays.asList(parameters));
        this.name = name;
    }

    public GLSLFunction(GLSLType returnType, String name, List<Pair<GLSLType, String>> parameters) {
        this.returnType = returnType;
        this.parameters  = new ArrayList<>();
        this.parameters.add(parameters);
        this.name = name;
    }

    public GLSLType getReturnType() {
        return returnType;
    }

    public List<Pair<GLSLType, String>> getParameters(int overload) {
        return parameters.get(overload);
    }

    public int getOverloadAmt(){
        return parameters.size();
    }

    public void addOverload(Pair<GLSLType, String> ... parameters){
        this.parameters.add(Arrays.asList(parameters));
    }
    //huehue
    public void addOverload(List<Pair<GLSLType, String>> parameters){
        this.parameters.add(parameters);
    }

    public String getName() {
        return name;
    }
}
