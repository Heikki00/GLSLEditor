package GLSLEditor.CodeDatabase;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by heikki.simojoki on 7.12.2015.
 */
public class GLSLFunction {


    private List<GLSLType> returnTypes;
    private List<List<Pair<GLSLType, String>>> parameters;
    private String name;
    private int ranking;

    //null == void
    public GLSLFunction(GLSLType returnType, String name, int ranking, Pair<GLSLType, String> ... parameters) {
        this.returnTypes = new ArrayList<>();
        this.returnTypes.add(returnType);
        this.parameters  = new ArrayList<>();
        if(!Arrays.asList(parameters).isEmpty()) this.parameters.add(Arrays.asList(parameters));
        this.name = name;
    }

    public GLSLFunction(GLSLType returnType, String name, int ranking, List<Pair<GLSLType, String>> parameters) {
        this.returnTypes = new ArrayList<>();
        this.returnTypes.add(returnType);
        this.parameters  = new ArrayList<>();
        this.parameters.add(parameters);
        this.name = name;
    }

    public GLSLType getReturnType(int overload) {
        return returnTypes.get(overload);
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

    public void addOverload(List<Pair<GLSLType, String>> parameters){
        this.parameters.add(parameters);
    }

    public void addReturnType(GLSLType type){returnTypes.add(type);}

    public String getName() {
        return name;
    }

    public int getRanking(){return ranking;}

    public void increaseRanking(){ranking++;}

}
