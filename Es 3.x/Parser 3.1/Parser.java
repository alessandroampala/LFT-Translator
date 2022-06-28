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

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
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
        if(look.tag == Token.lpt.tag || look.tag == Tag.NUM)
        {
            expr();
            match(Tag.EOF);
        }
        else
            error("Error in start");
    }

    //Gui(<expr> -> <term><exprp>) = {(, NUM}
    private void expr() {
        if(look.tag == Token.lpt.tag || look.tag == Tag.NUM)
        {
            term();
            exprp();
        }
        else
            error("Error in expr");
    }

    private void exprp() {
    	switch (look.tag)
        {
            //Gui(<exprp> -> +<term><exprp>) = {+}
    	    case '+':
                match(Token.plus.tag);
                term();
                exprp();
                break;
            //Gui(<exprp> -> -<term><exprp>) = {-}
            case '-':
                match(Token.minus.tag);
                term();
                exprp();
                break;
            //Gui(<exprp> -> ε) = {), EOF}
            case ')':
            case Tag.EOF:
                break;
            default:
                error("error in exprp");
    	}
    }

    //Gui(<term> -> <fact><termp>) = {(, NUM}
    private void term() {
        if(look.tag == Token.lpt.tag || look.tag == Tag.NUM)
        {
            fact();
            termp();
        }
        else
            error("error in term");
    }

    private void termp() {
        switch(look.tag)
        {
            //Gui(<termp> -> *<fact><termp>) = {*}
            case '*':
                match(Token.mult.tag);
                fact();
                termp();
                break;
            //Gui(<termp> -> /<fact><termp>) = {/}
            case '/':
                match(Token.div.tag);
                fact();
                termp();
                break;
            //Gui(<termp> -> ε) = {+, -, ), EOF}
            case '+':
            case '-':
            case ')':
            case Tag.EOF:
                break;
            default:
                error("error in termp");
        }

    }

    private void fact() {
        //Gui(<fact> -> (<expr>)) = {(}
        if(look.tag == Token.lpt.tag)
        {
            match(Token.lpt.tag);
            expr();
            match(Token.rpt.tag);
        }
        //Gui(<fact> -> NUM) = {NUM}
        else if(look.tag == Tag.NUM)
            match(Tag.NUM);
        else
            error("error in fact");
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}