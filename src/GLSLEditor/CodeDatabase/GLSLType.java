package GLSLEditor.CodeDatabase;


import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GLSLType {

    private String name;
    private List<Pair<GLSLType, String>> children;
    private List<GLSLType> arrayTypes;
    private Set<GLSLType> conversions;
    private List<List<GLSLType>> constructors;

    public GLSLType(String name, Pair<GLSLType, String> ... children){
        this.children = new ArrayList<>();
        this.name = name;
        arrayTypes = new ArrayList<>();
        conversions = new HashSet<>();
        constructors = new ArrayList<>();

        for(Pair<GLSLType, String> child : children){
            this.children.add(child);

        }

    }

    public GLSLType(String name){
        this.children = new ArrayList<>();
        arrayTypes = new ArrayList<>();
        this.name = name;
        conversions = new HashSet<>();
        constructors = new ArrayList<>();


    }

    public void setArrayType(int dimension, GLSLType type){
        if(arrayTypes.size() >= dimension){
            arrayTypes.set(dimension - 1, type);
            return;
        }
        else for(int i = arrayTypes.size(); i != dimension; ++i){
            arrayTypes.add(null);
        }
        arrayTypes.set(dimension - 1, type);

    }

    public GLSLType getArrayType(int dimension){
        if(arrayTypes.size() < dimension) return null;
        return arrayTypes.get(dimension);
    }

    public String getName() {
        return name;
    }

    public List<Pair<GLSLType, String>> getChildren() {
        return children;
    }

    public void addChild(Pair<GLSLType, String> child){
        children.add(child);
    }

    public void addConversion(GLSLType to){
        conversions.add(to);
    }

    public boolean canConvertTo(GLSLType to){
        return conversions.contains(to);
    }

    public void addConstructor(List<GLSLType> params){
        constructors.add(params);
    }

    public List<List<GLSLType>> getConstructors(){
        return constructors;
    }






}
