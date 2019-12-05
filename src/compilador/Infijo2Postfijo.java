/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.util.Stack;

/**
 *
 * @author Alonso Martinez
 */
public class Infijo2Postfijo {
    public static Stack Infijo2PosfijoTxt(Stack infijo){
		Stack p2 = Infijo2Posfijo(infijo);
                Stack p1 = new Stack();
                int t =p2.size();
                for(int i=0;i<t;i++)
                    p1.push(p2.pop());
		return p1;
	}
   private static Stack Infijo2Posfijo(Stack infijo) {
		infijo .push(")"); // Agregamos al final del infijo un &#8216;)&#8217
		int tamaño = infijo.size();
		Stack PilaDefinitiva = new Stack();
		Stack PilaTemp = new Stack();
		PilaTemp.push("("); // Agregamos a la pila temporal un &#8216;(&#8216;<br />
		for (int i = 0; i < tamaño; i++) {
			String caracter =  infijo.pop().toString();
			switch (caracter) {
			case "(":
				PilaTemp.push(caracter);
				break;
			case "+":case "-":case "^":case "*":case "/":
				while (!PilaTemp.empty()&&Jerarquia(caracter) <= Jerarquia(PilaTemp.peek().toString()))
					PilaDefinitiva.push(PilaTemp.pop());
				PilaTemp.push(caracter);
				break;
			case ")":
				while (!"(".equals(PilaTemp.peek().toString()))
					PilaDefinitiva.push(PilaTemp.pop());
				PilaTemp.pop();
				break;
			default:
				PilaDefinitiva.push(caracter);
			}
		}
                while(!PilaTemp.empty())
                    PilaDefinitiva.push(PilaTemp.pop());
		return PilaDefinitiva;
	}
   private static int Jerarquia(String elemento) {
		int res = 0;
		switch (elemento) {
		case ")":
			res = 5; break;
		case "^":
			res = 4; break;
		case "*": case "/":
			res = 3; break;
		case "+": case "-":
			res = 2; break;
		case "(":
			res = 1; break;
		}
		return res;
	}
}
