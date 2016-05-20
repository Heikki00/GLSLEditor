package GLSLEditor.CodeDatabase;
import GLSLEditor.AutoComplete.AutoComplete;
import GLSLEditor.Editor;
import GLSLEditor.Highlighting.Highlighter;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeDatabase {

    private static Editor editor;

    public static Set<String> GLSLscalars, GLSLalgebraTypes, GLSLvectors, GLSLMatrices, GLSLKeywords, GLSLPreprocessor, GLSLSamplers;

    public static Set<GLSLVariable> defaultVariables;

    public static Set<GLSLFunction> defaultFunctions;

    public static Set<GLSLType> variableTypes, internalTypes, userTypes;

    public static Set<String> variableTypeStrings;

    public static List<GLSLVariable> variables = new ArrayList<>();

    public static List<GLSLFunction> functions = new ArrayList<>();



    public static void init(Editor editor){
        CodeDatabase.editor = editor;

        defaultVariables = new HashSet<>();
        GLSLscalars = new HashSet<>();
        GLSLalgebraTypes = new HashSet<>();
        GLSLvectors = new HashSet<>();
        GLSLMatrices = new HashSet<>();
        defaultFunctions = new HashSet<>();
        GLSLPreprocessor = new HashSet<>();
        GLSLSamplers = new HashSet<>();

        variableTypes = new HashSet<>();
        userTypes = new HashSet<>();
        variableTypeStrings = new HashSet<>();
        GLSLKeywords = new HashSet<>();
        internalTypes = new HashSet<>();

        //Add the basic keywords from GLSL 430
        Collections.addAll(GLSLKeywords, "attribute", "const", "uniform", "varying", "buffer", "shared", "coherent", "volatile", "restrict", "readonly", "writeonly",
                "centroid", "flat", "smooth", "nonperspective", "patch", "sample", "break", "continue", "do", "for", "while", "switch", "case", "default", "if",
                "else", "subroutine", "in", "out", "inout", "invariant", "precise", "discard", "return", "struct", "layout", "location");

        //Add the basic scalar types
        Collections.addAll(GLSLscalars, "bool", "int", "uint", "float", "double", "void", "atomic_uint");

        //Add the basic preprocessor commands
        Collections.addAll(GLSLPreprocessor, "#include", "#version", "#line", "#define", "#undef", "#if", "#ifdef", "#ifndef", "#else", "elif", "#endif");


        //First add all samplers to temp list, then copy them to the real one with all possible data types
        HashSet<String> tempSamplerSet = new HashSet<>();
        Collections.addAll(tempSamplerSet, "sampler1D", "sampler2D", "sampler3D", "samplerCube", "sampler2DRect", "sampler1DArray", "sampler2DArray", "samplerCubeArray",
                "samplerBuffer", "sampler2DMS", "sampler2DMSArray");

        for(String s : tempSamplerSet){
            GLSLSamplers.add(s);
            GLSLSamplers.add("i" + s);
            GLSLSamplers.add("u" + s);
        }

        //Add matrices and vectors of all dimensions and data types
        for(int i = 2; i <= 4; ++i){
            Collections.addAll(GLSLvectors, "bvec" + i, "ivec" + i, "uvec" + i, "vec" + i, "dvec" + i);

            Collections.addAll(GLSLMatrices, "mat" + i);
            Collections.addAll(GLSLMatrices, "dmat" + i);
            for(int i1 = 2; i1 <= 4; ++i1){
                Collections.addAll(GLSLMatrices, "mat" + i1 + "x" + i);
                Collections.addAll(GLSLMatrices, "dmat" + i1 + "x" + i);
            }


        }

        GLSLalgebraTypes.addAll(GLSLvectors);
        GLSLalgebraTypes.addAll(GLSLMatrices);

        //SCALARS
        {
            //GLSLTypes for basic scalars
            GLSLType b = new GLSLType("bool"), i = new GLSLType("int"), ui = new GLSLType("uint"), f = new GLSLType("float"), d = new GLSLType("double");
            variableTypes.add(b);
            variableTypes.add(i);
            variableTypes.add(ui);
            variableTypes.add(f);
            variableTypes.add(d);

            //What can convert to what?
            f.addConversion(i);
            f.addConversion(ui);
            f.addConversion(d);

            d.addConversion(i);
            d.addConversion(ui);
            d.addConversion(f);

            ui.addConversion(i);


        }


        //SAMPLERS

        {
            for(String s : GLSLSamplers){
                variableTypes.add(new GLSLType(s));
            }


        }


        //VECTORS
        {



            for(String s : GLSLvectors){
                variableTypes.add(new GLSLType(s));

                }


            String[] v2 = {"vec2", "ivec2", "uvec2", "dvec2", "bvec2"};
            String[] v3 = {"vec3", "ivec3", "uvec3", "dvec3", "bvec3"};
            String[] v4 = {"vec4", "ivec4", "uvec4", "dvec4", "bvec4"};
            String[] types = {"float", "int", "uint", "double", "bool"};

            //Internal utility class to generate children for vectors
            class Swizzler{

                //Internal, recrusive function
                private void genStrings(char[] arr, List<String> res, int lenght, String current){
                    if(current.length() == lenght - 1){
                        for(char c : arr){
                            res.add(current + String.valueOf(c));

                        }

                        return;

                    }



                    for(char c : arr) {
                        String temp = current + String.valueOf(c);
                        genStrings(arr, res, lenght, new String(temp));

                    }

                }

                //A function that takes an array of chars and a maximum length, and returns all possible combinations of those chars that are shorter or as long as the maxLenght
                public List<String> getStrings(char[] chars, int maxLenght){
                    List<String> res = new ArrayList<>();
                    for(int i = 1; i <= maxLenght; ++i){
                        genStrings(chars, res, i, "");
                    }
                    return res;
                }


            }

            //Add children for vec2
            char[] arr2 = {'x', 'y'};
            List<String> children = new Swizzler().getStrings(arr2, 4);
            for(int i = 0; i < 5; ++i){
                for(String s : children){
                    getType(v2[i]).addChild(new Pair<>(getType(s.length() == 1 ? types[i] : s.length() == 2 ? v2[i] : s.length() == 3 ? v3[i] : v4[i]), s));

                }

            }
            //Add children for vec3
            char[] arr3 = {'x', 'y', 'z'};
            children = new Swizzler().getStrings(arr3, 4);
            for(int i = 0; i < 5; ++i){
                for(String s : children){
                    getType(v3[i]).addChild(new Pair<>(getType(s.length() == 1 ? types[i] : s.length() == 2 ? v2[i] : s.length() == 3 ? v3[i] : v4[i]), s));

                }

            }
            //Add children for vec4
            char[] arr4 = {'x', 'y', 'z', 'w'};
            children = new Swizzler().getStrings(arr4, 4);
            for(int i = 0; i < 5; ++i){
                for(String s : children){
                    getType(v4[i]).addChild(new Pair<>(getType(s.length() == 1 ? types[i] : s.length() == 2 ? v2[i] : s.length() == 3 ? v3[i] : v4[i]), s));

                }

            }








        }
        //MATRICES
        {
            for(String s : GLSLMatrices){
                     GLSLType type = new GLSLType(s);


                    type.setArrayType(1, getType((s.charAt(0) == 'd' ? "d" : "") + "vec" + s.charAt(s.length() - 1)));
                    type.setArrayType(2, getType(s.charAt(0) == 'd' ? "double" : "float"));
                    variableTypes.add(type);

            }







        }

        //Types that we don't want to show on autocomplete or highlight
       internalTypes.add(new GLSLType("gl_PerVertex", new Pair<GLSLType, String>(getType("vec4"), "gl_Position"), new Pair<GLSLType, String>(getType("float"), "gl_PointSize"), new Pair<GLSLType, String>(getType("float"), "gl_ClipDistance")));

        //Types to strings
        for(GLSLType type : variableTypes){
            variableTypeStrings.add(type.getName());

        }



        //Add default variables
        Collections.addAll(defaultVariables, new GLSLVariable(getType("int"), "gl_VertexID", 0, 0), new GLSLVariable(getType("int"), "gl_InstanceID", 0, 0),
                new GLSLVariable(getType("vec4"), "gl_Position", 0, 0), new GLSLVariable(getType("float"), "gl_PointSize", 0, 0), new GLSLVariable(getType("float"), "gl_ClipDistance", 0, 0),
                new GLSLVariable(getType("int"), "gl_PatchVerticesIn", 0, 0), new GLSLVariable(getType("int"), "gl_PrimitiveID", 0, 0), new GLSLVariable(getType("int"), "gl_InvocationID", 0, 0),
                new GLSLVariable(getType("float"), "gl_TessLevelOuter", 0, 0),  new GLSLVariable(getType("float"), "gl_TessLevelInner", 0, 0),  new GLSLVariable(getType("vec3"), "gl_TessCoord", 0, 0),
                new GLSLVariable(getType("gl_PerVertex"), "gl_in", 0, 0), new GLSLVariable(getType("int"), "gl_PrimitiveIDIn", 0, 0), new GLSLVariable(getType("int"), "gl_Layer", 0, 0),
                new GLSLVariable(getType("int"), "gl_ViewportIndex", 0, 0), new GLSLVariable(getType("vec4"), "gl_FragCoord", 0, 0), new GLSLVariable(getType("bool"), "gl_FrontFacing", 0, 0),
                new GLSLVariable(getType("vec2"), "gl_PointCoord", 0, 0), new GLSLVariable(getType("int"), "gl_SampleID", 0, 0), new GLSLVariable(getType("vec2"), "gl_SamplePosition", 0, 0),
                new GLSLVariable(getType("int"), "gl_SampleMaskIn", 0, 0), new GLSLVariable(getType("float"), "gl_FragDepth", 0, 0), new GLSLVariable(getType("int"), "gl_SampleMask", 0, 0)
                );


        //DEFAULT FUNCTIONS

        //Read the file
        String defaultFunctions = "";
        try {
            File file = new File(CodeDatabase.class.getResource("DefaultFunctions").getFile());
            defaultFunctions = new String(Files.readAllBytes(file.toPath()));
            defaultFunctions = (defaultFunctions.replace("\r", ""));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Pattern to find functions
       Pattern p = Pattern.compile("([a-zA-Z_$][\\w$]*)[ \n]+([a-zA-Z_$][\\w]*)[(]([^(;]*)[)]([0-9]+)[\n]+");



        Matcher m = p.matcher(defaultFunctions);

        //Are we parsing texture functions?
        boolean textures = false;

        ArrayList<String> notReadyVariables = new ArrayList<>();
        Collections.addAll(notReadyVariables, "gvec2", "gve3", "gvec4", "gsampler1D", "gsampler2D", "gsampler3D", "gsamplerCube");

        //Loop functions
        while(m.find()){



            GLSLFunction f = null;
            if(m.group(2).equals("TEXTURESTART")) textures = true;
            //Is the function a new function or just an overload
            boolean isNew = true;
            for(GLSLFunction func : CodeDatabase.defaultFunctions){
                if(func.getName().equals(m.group(2))){
                    isNew = false;
                    f = func;
                    break;
                }

            }

            if(!textures) {



                //Create a new GLSLFunction
                if (isNew) {
                    f = new GLSLFunction(getType(m.group(1)), m.group(2), Integer.parseInt(m.group(4)));
                }

                //Split parameters by commas
                String[] parameters = m.group(3).split(",");

                //Parameters of this function
                ArrayList<Pair<GLSLType, String>> paraList = new ArrayList<>();

                //Loop through parameters
                for (int i = 0; i < parameters.length; i++) {
                    parameters[i] = parameters[i].trim();

                    int spacePos = parameters[i].indexOf(' ');

                    //If the writing is still in progress or some shit
                    if (spacePos == -1) continue;

                    //Type and name of the parameter
                    String typeString = parameters[i].substring(0, spacePos);
                    String nameString = parameters[i].substring(typeString.length());

                    paraList.add(new Pair<GLSLType, String>(getType(typeString), nameString));

                }

                //Add the overload
                f.addOverload(paraList);

                //add the function
                CodeDatabase.defaultFunctions.add(f);
            }

            else{
                if(m.group(2).equals("TEXTURESTART")) continue;




                ArrayList<String> readyFunctions = new ArrayList<>();
                char preFixes[] = {'i', 'u', ' '};



                for(int i = 0; i < 3; ++i) {
                    String func = m.group(0);
                    for (String s : notReadyVariables) {
                        if (func.contains(s)) {

                            func = func.replace(s, preFixes[i] == ' ' ? s.substring(1) : preFixes[i] + s.substring(1));

                        }
                    }
                    if(!func.equals(m.group(0))){
                        readyFunctions.add(func);
                    }


                }
                if(readyFunctions.isEmpty()) readyFunctions.add(m.group(0));

                Matcher tempMatch = p.matcher(String.join("\n", readyFunctions));

                while(tempMatch.find()){
                    System.out.println(tempMatch.group(2));
                    //Create a new GLSLFunction
                    for (GLSLFunction function : CodeDatabase.defaultFunctions){

                        if(tempMatch.group(2).equals(function.getName())){
                           isNew = false;
                        }
                    }

                    if(isNew) f = new GLSLFunction(getType(tempMatch.group(1)), tempMatch.group(2), Integer.parseInt(tempMatch.group(4)));

                    //Parameters of this function
                    ArrayList<Pair<GLSLType, String>> paraList = new ArrayList<>();

                    //Split parameters by commas
                    String[] parameters = tempMatch.group(3).split(",");

                    //Loop through parameters
                    for (int i = 0; i < parameters.length; i++) {
                        parameters[i] = parameters[i].trim();

                        int spacePos = parameters[i].indexOf(' ');

                        //If the writing is still in progress or some shit
                        if (spacePos == -1) continue;

                        //Type and name of the parameter
                        String typeString = parameters[i].substring(0, spacePos);
                        String nameString = parameters[i].substring(typeString.length());

                        paraList.add(new Pair<GLSLType, String>(getType(typeString), nameString));
                        System.out.println(typeString);
                    }

                    //Add the overload
                    f.addOverload(paraList);
                    f.addReturnType(getType(tempMatch.group(1)));
                    //add the function
                    CodeDatabase.defaultFunctions.add(f);





                }






            }

        }





    }


    //Retunrns all user-created variables
    public static List<GLSLVariable> getVariables(){
        return variables;

    }

    //Updates the variables, functions etc. according to the code
    public static void update(String code){
        boolean wasProjectParsed = false;
        if(editor.getProject() != null){

            if(editor.getProject().hasDocument(editor.getActiveDocument())) {
                code = AutoComplete.getDocumentParsed(editor.getActiveDocument(), editor.getProject().getWorkFolder() + "/");
                wasProjectParsed = true;
            }

        }

        if(editor.getActiveDocument().isFile() && !wasProjectParsed){
            String filename = editor.getActiveDocument().getFilename();
            filename = filename.substring(0, filename.lastIndexOf("/") + 1);


            code = AutoComplete.getDocumentParsed(editor.getActiveDocument(), filename);


        }

        //STRUCTS

        userTypes.clear();

        //Pattern to find structs(group 1 is the name, group 2 is everything inside braces)
        Pattern p = Pattern.compile("struct[ \n]+(\\w+)[ \n]*[{]([^}]*)[}][ \n]*;");

        Matcher m = p.matcher(code);

        while(m.find()){
            //Pattern to find member variables of the struct
            Pattern varPat = Pattern.compile("([\\w]+)[ \n]+([\\w]+)[ \n]*;");

            //Give the group 2 to matcher
            Matcher varMat = varPat.matcher(m.group(2));

            //Create type for the new struct
            GLSLType t = new GLSLType(m.group(1));

            while(varMat.find()){
                GLSLType childType = getType(varMat.group(1));

                //Shouldn't happen, but can't hurt
                if(childType == null) continue;

                //Add the mamber to struct
                t.addChild(new Pair<GLSLType, String>(childType, varMat.group(2)));

            }

            userTypes.add(t);
        }


        //VARIABLES:

        //Clear old variables
        variables.clear();

        //Pattern to find variables
        p = Pattern.compile("([a-zA-Z_$][\\w$]*)[ \n]+([a-zA-Z_$][\\w]*)(?:[ \n]*|[ \n]*=.*);");

        m = p.matcher(code);

        //Loop through varables
        while(m.find()){


            //If there is no curly brace before variable, set infinite scope
            int start = code.lastIndexOf("{", m.start());
            int end = code.indexOf("}", m.end());
            if((start == -1 || end == -1) || (code.indexOf("}", start) != end)){
                start = 0;
                end = 0;
            }

            //add the variable
            variables.add(new GLSLVariable(getType(m.group(1)), m.group(2), start == 0 ? 0 : m.start() , end));

        }


        //FUNCTIONS:

        functions.clear();

        //Pattern to find functions
        p = Pattern.compile("([a-zA-Z_$][\\w$]*)[ \n]+([a-zA-Z_$][\\w]*)[(]([^;]*)[)][ \n]*\\{");



        m = p.matcher(code);

        //Loop through functions
        while(m.find()){

            //Split the parameters by commas
            String[] parameters = m.group(3).split(",");

            GLSLFunction f = null;

            //If the function is new(not a overload), create it. Ether way set the value
            boolean isNew = true;
            for(GLSLFunction func : CodeDatabase.defaultFunctions){
                if(func.getName().equals(m.group(1))){
                    isNew = false;
                    f = func;
                    break;
                }

            }

            if(isNew) {
                f = new GLSLFunction(getType(m.group(1)), m.group(2), Integer.parseInt(m.group(4)));
            }

            //List of parameters
            ArrayList<Pair<GLSLType, String>> paraList = new ArrayList<>();

            //Loop through parameters
            for(int i = 0; i < parameters.length; i++){
                //Take off the extra spaces
                parameters[i] = parameters[i].trim();
                int spacePos = parameters[i].indexOf(' ');

                //If the format is weird, just skip it. Probably the user is in the middle of writing it
                if(spacePos == -1) continue;

                //Type and name of the parameter
                String typeString = parameters[i].substring(0, spacePos);
                String nameString = parameters[i].substring(typeString.length());

                paraList.add(new Pair<GLSLType, String>(getType(typeString), nameString));

            }
            //Add the parameters
            f.addOverload(paraList);

           functions.add(f);




        }




    }


    //Returns the GLSLType that maches the name. Works for default-, internal- and user defined types. Returns null if the type is not found(something weird or "void")
    public static GLSLType getType(String name){
        for(GLSLType t : userTypes){
            if(t.getName().equals(name)) return t;
        }

        for(GLSLType t : variableTypes){
            if(t.getName().equals(name)) return t;
        }

        for(GLSLType t : internalTypes){
            if(t.getName().equals(name)) return t;
        }

        return null;
    }

    //Returns the GLSLFunction that matches the name. Works for user-defined and default functions. Returns null if the type is not found
    public static GLSLFunction getFunction(String name){
        for(GLSLFunction f : functions){
            if(f.getName().equals(name)) return f;
        }

        for(GLSLFunction f : defaultFunctions){
            if(f.getName().equals(name)) return f;
        }

        return null;
    }

    //Returns the GLSLSVariable that matches to the type. Works for user-defined variables and default variables. Returns void if the variable is not found. If there is multiple
    //variables with the same name, returns the first user-defined one.
    public static GLSLVariable getVariable(String name){
        for(GLSLVariable v : variables){
            if(v.getName().equals(name)) return v;
        }

        for(GLSLVariable v : defaultVariables) {
            if (v.getName().equals(name)) return v;
        }

        return null;


    }


    public static void saveRankings(){
        String defaultFunctionsString = "";
        try {
            File file = new File(CodeDatabase.class.getResource("DefaultFunctions").getFile());
            defaultFunctionsString = new String(Files.readAllBytes(file.toPath()));
            defaultFunctionsString = (defaultFunctionsString.replace("\r", ""));

        } catch (IOException e) {
            e.printStackTrace();
        }


        for(GLSLFunction f : CodeDatabase.defaultFunctions){

            String functionString = ( f.getReturnType(0) == null ? "void" : f.getReturnType(0).getName()) + " " + f.getName() + "(";

            //Add the parameters to the result
            boolean first = true;
            for(Pair<GLSLType, String> par : f.getParameters(0)){
                if(!first) functionString += ", ";
                if(first) first = false;

                functionString += par.getKey().getName() + par.getValue();


            }

            functionString += ")";


            int ind = defaultFunctionsString.indexOf(functionString);
            






        }

    }














}
