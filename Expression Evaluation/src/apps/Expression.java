package apps;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	/**
	 * Expression to be evaluated
	 */
	String expr;                
    
	/**
	 * Scalar symbols in the expression 
	 */
	ArrayList<ScalarSymbol> scalars;   
	
	/**
	 * Array symbols in the expression
	 */
	ArrayList<ArraySymbol> arrays;
    
    /**
     * String containing all delimiters (characters other than variables and constants), 
     * to be used with StringTokenizer
     */
    public static final String delims = " \t*+-/()[]";
    
    /**
     * Initializes this Expression object with an input expression. Sets all other
     * fields to null.
     * 
     * @param expr Expression
     */
    public Expression(String expr) {
        this.expr = expr;
    }

    /**
     * Populates the scalars and arrays lists with symbols for scalar and array
     * variables in the expression. For every variable, a SINGLE symbol is created and stored,
     * even if it appears more than once in the expression.
     * At this time, values for all variables are set to
     * zero - they will be loaded from a file in the loadSymbolValues method.
     */
    public void buildSymbols() {
    		/** COMPLETE THIS METHOD **/
    	scalars = new ArrayList<ScalarSymbol>();
        arrays = new ArrayList<ArraySymbol>();
        Stack<String> letters = new Stack();
        String chars = "";
        StringTokenizer toke = new StringTokenizer(expr, delims, true);
        while (toke.hasMoreTokens()) {
            chars = toke.nextToken();
            if (Pattern.matches("[a-zA-Z]+", chars)/*Character.isLetter(chars.charAt(0))*/ || chars.equals("[")) {//checks if letter or for array beginning
                letters.push(chars);
            }
        }
            while (letters.isEmpty() == false)
            {
                chars = letters.pop();
                if (chars.equals("["))
                {
                    chars = letters.pop();//gets token before bracket which is the Array name
                    ArraySymbol ARRAY = new ArraySymbol(chars);
                    if(arrays.indexOf(ARRAY) == -1)//makes sure its not already in array
                        arrays.add(ARRAY);
                }
                else
                {
                    ScalarSymbol SCALAR = new ScalarSymbol(chars);//else its a scalar
                    if (scalars.indexOf(SCALAR) == -1)
                        scalars.add(SCALAR);
                }
            }
        }
   
    
    /**
     * Loads values for symbols in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     */
    public void loadSymbolValues(Scanner sc) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String sym = st.nextToken();
            ScalarSymbol ssymbol = new ScalarSymbol(sym);
            ArraySymbol asymbol = new ArraySymbol(sym);
            int ssi = scalars.indexOf(ssymbol);
            int asi = arrays.indexOf(asymbol);
            if (ssi == -1 && asi == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                scalars.get(ssi).value = num;
            } else { // array symbol
            	asymbol = arrays.get(asi);
            	asymbol.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    String tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    asymbol.values[index] = val;              
                }
            }
        }
    }
    
    
    /**
     * Evaluates the expression, using RECURSION to evaluate subexpressions and to evaluate array 
     * subscript expressions.
     * 
     * @return Result of evaluation
     */
    public float evaluate() {
    		/** COMPLETE THIS METHOD **/
        expr = expr.replaceAll("\\s+","");//removes spaces
        StringTokenizer st = new StringTokenizer(expr,delims,true);
        String name = "";
        String tokenss = "";
        while(st.hasMoreElements()) {//this loop checks and replaces scalars but leaves arrays alone
            tokenss = st.nextToken();
            if (Pattern.matches("[a-zA-Z]+", tokenss)) {
                name = tokenss;
            }
            if (tokenss.equals("+") || tokenss.equals("/") || tokenss.equals("*") || tokenss.equals("-")||!st.hasMoreElements()) {
                int p = 0;
                while (p < scalars.size()) {
                    if (scalars.get(p).name.equals(name)) {

                        expr = expr.replaceFirst(name, scalars.get(p).value + "");//uses replace first as to not mess up arrays or other scalars with similar name
                    }
                    p++;
                }
            }
            if (tokenss.equals("[")) {
                name = "";
            }
        }
        return evaluate(expr);

    }

    private float evaluate(String expression)//this method eliminates the parenthesis and gets the values for arrays
    {
        {
            System.out.println(expression);//prints the new expression after dealing with parenthesis and brackets
            int parenth = 0;//keeps track of parenthesis
            int bracket = 0;//keeps track of brackets
            StringTokenizer st = new StringTokenizer(expression, delims, true);
            int i = 0;
            int arrnamebegin = 0;
            String arr = "";//array name placeholder
            whileTop:
            while (st.hasMoreElements()) {
                String tok = st.nextToken();
                if (Character.isLetter(tok.charAt(0)))//tok.charAt(0) >= 'a' && tok.charAt(0) <= 'Z'
                {
                    arrnamebegin = i;
                    arr = tok;
                    i += tok.length();
                    continue;
                }
                switch (tok) {
                    case "(":
                        parenth = i + 1;
                        i++;
                        continue;
                    case ")":
                        float value = 0;
                        value = (evaluate(expression.substring(parenth, i)));
                        String Svalue = Float.toString(value);
                        expression = expression.substring(0, parenth - 1) + Svalue + expression.substring(i + 1);
                        expression = Float.toString(evaluate(expression));
                        i++;
                        break whileTop;//breaks to just before while loop
                    case "[":
                        bracket = i + 1;
                        i++;
                        continue;
                    case "]":
                        int values = 0;
                        values = (int) (Math.floor(evaluate(expression.substring(bracket, i))));//converts float to int and truncates to avoid array error
                        int arrvalue = 0;
                        for (int p = 0; p < arrays.size(); p++) {
                            if (arrays.get(p).name.equals(arr)) {
                                arrvalue = arrays.get(p).values[values];
                            }
                        }
                        String SBvalue = Float.toString(arrvalue);
                        expression = expression.substring(0, arrnamebegin) + SBvalue + expression.substring(i + 1);
                        expression = Float.toString(evaluate(expression));
                        i++;
                        break whileTop;

                    default:
                        i += tok.length();
                        break;
                }
            }
        }
        String negative = "";//String to check if it is negative at the end
        final String delimwithend = " \t*+-/()[]|";//new deliminator that contains expression end symbol "|"
        Stack<String> OPS = new Stack<>();
        Stack<Float> values = new Stack<>();
        if (expression.charAt(expression.length()-1) != '|') {//adds the expression end symbol if it is not there
            expression = expression + "|";
        }
        StringTokenizer st = new StringTokenizer(expression,delimwithend,true);
        int i = 0;
        String last = "";
        while(st.hasMoreElements()){
            String tok = st.nextToken();
            if (tok.equals("+") || tok.equals("-") || tok.equals("*") || tok.equals("/") || tok.equals("|"))
            {
                if(i == 0 && tok.equals("-"))//if first sign is "-", this means the answer is negative
                {
                    negative = "yes";
                    continue;
                }
                if(!tok.equals("|") && last.equals("+") || last.equals("-") || last.equals("*") || last.equals("/"))
                {
                    negative = "yes";
                    continue;
                }
                negative = "";
                OPS.push(tok);
                i++;
                last = tok;
            }
            else
            {
                if(negative.equals("yes"))
                {
                    Float temp = (Float.parseFloat(tok))*-1;
                    values.push(temp);
                    last = tok;
                    i++;
                }
                else {
                    values.push(Float.parseFloat(tok));
                    i++;
                    last = tok;
                }
            }

            Solve(values, OPS);
        }
        float answer;
        answer = values.pop();
        return answer;

    }

    private void Solve(Stack<Float> values, Stack<String> OPS)
    {
        while(OPS.size()>=2)//this includes operators and the expression end symbol
        {
        	int opp1 = 0, opp2 = 0;//giving priority to operators through int values
            String op1 = OPS.pop();
            String op2 = OPS.pop();
            switch (op1) {
                case "*":
                case "/":
                    opp1 = 3;
                    break;
                case "+":
                case "-":
                    opp1 = 2;
                    break;
                case "|":
                    opp1 = 1;
                default:
            }
            switch (op2) {
                case "*":
                case "/":
                    opp2 = 3;
                    break;
                case "+":
                case "-":
                    opp2 = 2;
                    break;
                case "|":
                    opp2 = 1;
                default:
            }
            if (opp1 <= opp2) {//performs operations in order of highest priority, which is operation 2
                float  value1 = values.pop();
                float  value2 = values.pop();
                float answer = 0;
                switch (op2) {
                    case "+":
                        answer = (value2 + value1);
                        break;
                    case "-":
                        answer = (value2 - value1);
                        break;
                    case "*":
                        answer = (value2 * value1);
                        break;
                    case "/":
                        answer = (value2 / value1);
                        break;
                    default:
                }
                values.push(answer);
                OPS.push(op1);//pushes back operator that is not used
            }
            else {//no operations need to be immediately handled (low priority)
                OPS.push(op1);
                OPS.push(op2);
                return;

            }
        }

    }

    /**
     * Utility method, prints the symbols in the scalars list
     */
    public void printScalars() {
        for (ScalarSymbol ss: scalars) {
            System.out.println(ss);
        }
    }
    
    /**
     * Utility method, prints the symbols in the arrays list
     */
    public void printArrays() {
    		for (ArraySymbol as: arrays) {
    			System.out.println(as);
    		}
    }

}
