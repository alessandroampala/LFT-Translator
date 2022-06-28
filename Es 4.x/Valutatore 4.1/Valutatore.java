/*
    Written by ALESSANDRO AMPALA
    Insiemi guida:
    Gui(<start> -> <expr>EOF) = {(, NUM}
    Gui(<expr> -> <term><exprp>) = {(, NUM}
    Gui(<exprp> -> +<term><exprp>) = {+}
    Gui(<exprp> -> -<term><exprp>) = {-}
    Gui(<exprp> -> ε) = {), EOF}
    Gui(<term> -> <fact><termp>) = {(, NUM}
    Gui(<termp> -> *<fact><termp>) = {*}
    Gui(<termp> -> /<fact><termp>) = {/}
    Gui(<termp> -> ε) = {+, -, ), EOF}
    Gui(<fact> -> (<expr>)) = {(}
    Gui(<fact> -> NUM) = {NUM}
*/
import java.io.*; 

public class Valutatore {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Valutatore(Lexer l, BufferedReader br) { 
	lex = l; 
	pbr = br;
	move(); 
    }
   
    void move() { 
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
       throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
        if (look.tag == t) {
            if (look.tag != Tag.EOF) move();
        } else error("syntax error");
    }

    //Gui(<start> -> <expr>EOF) = {(, NUM}
    public void start() {
        int expr_val;
    	if(look.tag == Token.lpt.tag || look.tag == Tag.NUM)
        {
            expr_val = expr();
        	match(Tag.EOF);
            System.out.println(expr_val);
        }
        else
            error("Error in start");
    }

    //Gui(<expr> -> <term><exprp>) = {(, NUM}
    private int expr() {
        if(look.tag == Token.lpt.tag || look.tag == Tag.NUM)
            return exprp(term());
        else
        {
            error("Error in expr");
            return -1;
        }
    }

    private int exprp(int exprp_i) {
    	switch (look.tag) {
        	case '+':
                    match('+');
                    return exprp(exprp_i + term());
            case '-':
                    match('-');
                    return exprp(exprp_i - term());
            case ')':
            case Tag.EOF:
                return exprp_i;
            default:
                error("error in exprp");
                return -1;
    	}
    }

    private int term() {
        if(look.tag == '(' || look.tag == Tag.NUM)
            return termp(fact());
        else
        {
            error("error in term");
            return -1;
        }
    }
    
    private int termp(int termp_i) {
        switch(look.tag)
        {
            case '*':
                match('*');
                return termp(termp_i * fact());
            case '/':
                match('/');
                return termp(termp_i / fact());
            case '+':
            case '-':
            case ')':
            case Tag.EOF:
                return termp_i;
            default:
                error("error in termp");
                return -1;
        }
    }
    
    private int fact() {
        int fact_val;
        if(look.tag == Token.lpt.tag)
        {
            match('(');
            fact_val = expr();
            match(')');
            return fact_val;
        }
        else if(look.tag == Tag.NUM)
        {
            fact_val = ((NumberTok) look).number;
            match(Tag.NUM);
            return fact_val;
        }
        else
        {
            error("error in fact");
            return -1;
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "input.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Valutatore valutatore = new Valutatore(lex, br);
            valutatore.start();
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}