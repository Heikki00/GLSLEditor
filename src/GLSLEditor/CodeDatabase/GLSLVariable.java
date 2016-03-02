package GLSLEditor.CodeDatabase;

import java.util.List;

//Represents a GLSL Variable
public class GLSLVariable{
    private String name;
    private GLSLType type;

    private int scopeFrom, scopeTo;

    //Constructor. If the scopeFrom AND scopeTo == 0, the scope is considered infinite
        public GLSLVariable(GLSLType type, String name, int scopeFrom, int scopeTo) {
            this.type = type;
            this.name = name;
            this.scopeFrom = scopeFrom;
            this.scopeTo = scopeTo;
        }



        public String getName() {
            return name;
        }



    public GLSLType getType() {
        return type;
    }


    public int getScopeFrom() {
        return scopeFrom;
    }

    public void setScopeFrom(int scopeFrom) {
        this.scopeFrom = scopeFrom;
    }

    public int getScopeTo() {
        return scopeTo;
    }

    public void setScopeTo(int scopeTo) {
        this.scopeTo = scopeTo;

    }


    public boolean isInScope(int location){
        return (location > scopeFrom && location < scopeTo) || (scopeFrom == 0 && scopeTo == 0);
    }
}
