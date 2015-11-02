package GLSLEditor.CodeDatabase;
import GLSLEditor.Highlighting.Highlighter;
import javafx.util.Pair;

import java.util.*;

public class CodeDatabase {

    public static Set<String> GLSLscalars, GLSLalgebraTypes, GLSLvectors, GLSLMatrices, GLSLKeywords;

    public static Set<GLSLVariable> variables;

    public static Set<GLSLType> variableTypes;

    public static Set<String> variableTypeStrings;

    static{
        variables = new HashSet<>();
        GLSLscalars = new HashSet<>();
        GLSLalgebraTypes = new HashSet<>();
        GLSLvectors = new HashSet<>();
        GLSLMatrices = new HashSet<>();
        variableTypes = new HashSet<>();
        variableTypeStrings = new HashSet<>();
        GLSLKeywords = new HashSet<>();

        Collections.addAll(GLSLKeywords, "attribute", "const", "uniform", "varying", "buffer", "shared", "coherent", "volatile", "restrict", "readonly", "writeonly",
                "centroid", "flat", "smooth", "nonperspective", "patch", "sample", "break", "continue", "do", "for", "while", "switch", "case", "default", "if",
                "else", "subroutine", "in", "out", "inout", "invariant", "precise", "discard", "return", "struct", "layout", "location");

        Collections.addAll(GLSLscalars, "bool", "int", "uint", "float", "double", "void", "atomic_uint");

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

            GLSLType b = new GLSLType("bool"), i = new GLSLType("int"), ui = new GLSLType("uint"), f = new GLSLType("float"), d = new GLSLType("double");
            variableTypes.add(b);
            variableTypes.add(i);
            variableTypes.add(ui);
            variableTypes.add(f);
            variableTypes.add(d);

            for (GLSLType t : variableTypes) {
                t.addConstructor(new ArrayList<>(Collections.singleton(b)));
                t.addConstructor(new ArrayList<>(Collections.singleton(i)));
                t.addConstructor(new ArrayList<>(Collections.singleton(ui)));
                t.addConstructor(new ArrayList<>(Collections.singleton(f)));
                t.addConstructor(new ArrayList<>(Collections.singleton(d)));
            }


            f.addConversion(i);
            f.addConversion(ui);
            f.addConversion(d);

            d.addConversion(i);
            d.addConversion(ui);
            d.addConversion(f);

            ui.addConversion(i);


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


            class Swizzler{

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

                public List<String> getStrings(char[] chars, int maxLenght){
                    List<String> res = new ArrayList<>();
                    for(int i = 1; i <= maxLenght; ++i){
                        genStrings(chars, res, i, "");
                    }
                    return res;
                }


            }

            char[] arr2 = {'x', 'y'};
            List<String> children = new Swizzler().getStrings(arr2, 4);
            for(int i = 0; i < 5; ++i){
                for(String s : children){
                    getType(v2[i]).addChild(new Pair<>(getType(s.length() == 1 ? types[i] : s.length() == 2 ? v2[i] : s.length() == 3 ? v3[i] : v4[i]), s));

                }

            }

            char[] arr3 = {'x', 'y', 'z'};
            children = new Swizzler().getStrings(arr3, 4);
            for(int i = 0; i < 5; ++i){
                for(String s : children){
                    getType(v3[i]).addChild(new Pair<>(getType(s.length() == 1 ? types[i] : s.length() == 2 ? v2[i] : s.length() == 3 ? v3[i] : v4[i]), s));

                }

            }

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



        for(GLSLType type : variableTypes){
            variableTypeStrings.add(type.getName());

        }







    }


    public static void update(String code){
        if(code.length() > new String("#version 430").length() && !code.startsWith("#version")){
            Highlighter.addError(0, new String("#version 430").length());


        }






    }


    public static GLSLType getType(String name){
        for(GLSLType t : variableTypes){
            if(t.getName().equals(name)) return t;
        }
        return null;
    }





















}
