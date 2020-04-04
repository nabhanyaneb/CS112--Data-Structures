package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	
    
    	
    	StringTokenizer sToken=new StringTokenizer(expr, delims);
    	String str="";
    	
    	while (sToken.hasMoreTokens()) { 
    		str=sToken.nextToken();
    		if (Character.isLetter(str.charAt(0))) {
    			int i=expr.indexOf(str);
    			int length=str.length();
    			
    			int il=i+length;
    			
    			
    			if (il+1>expr.length())
    				vars.add(new Variable(str));
    			
    			else if(expr.charAt(il)=='[') {
    				Array temp=new Array(str);
    				if (!(arrays.contains(temp)))
    					arrays.add(temp);
    			}
    			else {
    				Variable temp=new Variable(str);
    				if (!(vars.contains(temp)))
    					vars.add(new Variable(str));
    			}
    		}
    	}
    	System.out.println(vars);
    	System.out.println(arrays);
    }
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
    }
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	    	
    	String expr2=expr;
    	expr2=expr2.replaceAll(" ", "");
    	expr2=expr2.replaceAll("\t", "");
    	
    	String curr="";
    	String prev="";
    	float res=0;
    	
    	Stack<Float> vals=new Stack<Float>();
    	Stack<Character> ops=new Stack<Character>();
    	
    	StringTokenizer sToken=new StringTokenizer(expr2, delims, true);
    	
    	while (sToken.hasMoreTokens()) {
    		curr=sToken.nextToken();
    		
    		if (isNumber(curr))
    			vals.push(Float.parseFloat(curr));
    		else if (Character.isLetter(curr.charAt(0))) {
    			boolean check=false;
    				float f=0f;
    				for (Variable v: vars) {
    					if (v.name.equals(curr)) {
    						f=(float)v.value;
    						check=true;
    					}
    				}
    				if (check)
    					vals.push(f);
    		}
    		else if (curr.equals("+") || curr.equals("-") || curr.equals("*") || curr.equals("/")) {
    			while(!(ops.isEmpty()) && (checkOpPemdas(curr.charAt(0),ops.peek()))) {
    				char myOp=ops.pop();
    				float val1=vals.pop();
    				float val2=vals.pop();
    				
    				vals.push(doOpMath(myOp, val1, val2));			
    			}
    			if (!(isNumber(curr)))
    				ops.push(curr.charAt(0));
    		}
    		else if (curr.equals("[")) {
    			Stack <Character> bracks=new Stack<Character>();
    			float f=0f;
    			String temp="";
    			bracks.push(curr.charAt(0));
    			curr=sToken.nextToken();
    			
    			while (sToken.hasMoreTokens()) {
    				if (curr.equals("[")) {
    					bracks.push('[');
    					temp+=curr.toString();
    				}
    				else if (curr.equals("]")) {
    					bracks.pop();
    					if (bracks.isEmpty())
    						break;
    					temp+=curr.toString();
    				}
    				else 
    					temp+=curr.toString();
    				
    				curr=sToken.nextToken();
    			}
    			if (!(temp.contains("]") && temp.contains("[")))
    			temp+=(curr.toString());
    			f=(evaluate(temp,vars,arrays));
    			int index=0;
    			for (int i=0;i<arrays.size();i++) {
    				if ((arrays.get(i).name).equals(prev.toString())) {
    					index=i;
    				}
    			}
    			
    			int [] arr = arrays.get(index).values;
    			for (int j=0;j<arr.length;j++) {
    				if (j==(int)f) {
    					res=((float)arr[j]);
    					vals.push(res);
    				}	
    			}
    		}
    		
    		else if (curr.equals("(")) {
    			//String exp="";
    			Stack<String> pars = new Stack<String>();
    			float f=0f;
    			String temp="";
    			pars.push("(");
    			curr=sToken.nextToken();
    	
    			while (sToken.hasMoreTokens()) {

    				if (curr.equals("("))
    				{
    					pars.push("(");
    					temp+=(curr.toString());
    				}
    				else if (curr.equals(")")  )
    				{
    					pars.pop();
    					if (pars.isEmpty())
    						break;
    					temp+=(curr.toString());
    				}
    				else if (!curr.equals(")")){
	    				temp+=(curr.toString());
	    				
    				}
    				curr=sToken.nextToken();
    				
    			}
    			
    			if (!(temp.contains(")") && temp.contains("(")))
    			temp+=(curr.toString());
    			res=((evaluate(temp,vars,arrays)));
    			//v.add(answer);
    			vals.push(res);
    		}
    		prev=curr;
    	}
    	while (!ops.isEmpty()) {
    		vals.push(doOpMath(ops.pop(), vals.pop(), vals.pop()));
    	}
    	return vals.pop();
    }
    
    private static boolean checkOpPemdas(char op1, char op2) {
    	if (op2=='(' || op2==')' || op2=='[' || op2==']')
    			return false;
    	if ((op1=='*' || op1=='/') && (op2=='+' || op2=='-'))
    		return false;
    	
    	return true;
    }
  
    
    private static float doOpMath(char op, float val2, float val1) {
    	
    	float res=0;
    	switch(op) {
    	
    		case '*':
	    		res=val1*val2;
	    		break;
    		case '/':
    			res=val1/val2;
    			break;
    		case '+': 
    			res=val1+val2;
    			break;
    		case '-': 
    			res=val1-val2;  	
    	
    	}
    	
    	return res;
    }
    
	private static boolean isNumber(String check){
		try{
			Float.parseFloat(check);
		}
		catch(Exception ex){
			return false;
		}
		return true;
	}

}