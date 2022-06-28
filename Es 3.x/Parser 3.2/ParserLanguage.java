/*
	Written by ALESSANDRO AMPALA
	Insiemi guida:
	Gui(<prog> -> <stat>EOF) = {(}
	Gui(<statlist> -> <stat><statlistp>) = {(}
	Gui(<statlistp> -> <stat><statlistp>) = {(}
	Gui(<statlistp> -> ε) = {)}
	Gui(<stat> -> (<statp>)) = {(}
	Gui(<statp> -> =ID<expr>) = {=}
	Gui(<statp> -> cond<bexpr><stat><elseopt>) = {c}
	Gui(<statp> -> while<bexpr><stat>) = {w}
	Gui(<statp> -> do<statlist>) = {d}
	Gui(<statp> -> print<exprlist>) = {p}
	Gui(<statp> -> read ID) = {r}
	Gui(<elseopt> -> (else<stat>)) = {(}
	Gui(<elseopt> -> ε) = {)}
	Gui(<bexpr> -> (<bexprp>)) = {(}
	Gui(<bexprp> -> RELOP<expr><expr>) = {R}
	Gui(<expr> -> NUM) = {NUM}
	Gui(<expr> -> ID) = {ID}
	Gui(<expr> -> (<exprp>)) = {(}
	Gui(<exprp> -> +<exprlist>) = {+}
	Gui(<exprp> -> -<expr><expr>) = {-}
	Gui(<exprp> -> *<exprlist>) = {*}
	Gui(<exprp> -> /<expr><expr>) = {/}
	Gui(<exprlist> -> <expr><exprlistp>) = {NUM, ID, (}
	Gui(<exprlistp> -> <expr><exprlistp>) = {NUM, ID, (}
	Gui(<exprlistp> -> ε) = {)}
*/

import java.io.*;

public class ParserLanguage {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public ParserLanguage(Lexer l, BufferedReader br) {
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

    //Gui(<prog> -> <stat>EOF) = {(}
    public void prog() {
    	if(look.tag == Token.lpt.tag)
    	{
    		stat();
    		match(Tag.EOF);
    	}
    	else
    		error("error in prog()");
    }

    //Gui(<statlist> -> <stat><statlistp>) = {(}
    private void statlist() {
    	if(look.tag == Token.lpt.tag)
    	{
    		stat();
    		statlistp();
    	}
    	else
    		error("error in statlist()");
    }

	private void statlistp() {
		switch(look.tag)
		{
			//Gui(<statlistp> -> <stat><statlistp>) = {(}
			case '(':
				stat();
				statlistp();
			//Gui(<statlistp> -> ε) = {)}
			case ')':
				break;
			default:
				error("error in statlistp()");
		}
	}

	//Gui(<stat> -> (<statp>)) = {(}
	private void stat() {
		if(look.tag == Token.lpt.tag)
		{
			match(Token.lpt.tag);
			statp();
			match(Token.rpt.tag);
		}
		else
			error("error in stat()");
	}

	private void statp() {
		switch(look.tag)
		{
			//Gui(<statp> -> =ID<expr>) = {=}
			case '=':
				match(Token.assign.tag);
				match(Tag.ID);
				expr();
				break;
			//Gui(<statp> -> cond<bexpr><stat><elseopt>) = {c}
			case Tag.COND:
				match(Tag.COND);
				bexpr();
				stat();
				elseopt();
				break;
			//Gui(<statp> -> while<bexpr><stat>) = {w}
			case Tag.WHILE:
				match(Tag.WHILE);
				bexpr();
				stat();
				break;
			//Gui(<statp> -> do<statlist>) = {d}
			case Tag.DO:
				match(Tag.DO);
				statlist();
				break;
			//Gui(<statp> -> print<exprlist>) = {p}
			case Tag.PRINT:
				match(Tag.PRINT);
				exprlist();
				break;
			//Gui(<statp> -> read ID) = {r}
			case Tag.READ:
				match(Tag.READ);
				match(Tag.ID);
				break;
			default:
				error("error in statp()");
		}
	}

	private void elseopt() {
		switch(look.tag)
		{
			//Gui(<elseopt> -> (else<stat>)) = {(}
			case '(':
				match(Token.lpt.tag);
				match(Tag.ELSE);
				stat();
				match(Token.rpt.tag);
			//Gui(<elseopt> -> ε) = {)}
			case ')':
				break;
			default:
				error("error in elseopt()");
		}
	}

	//Gui(<bexpr> -> (<bexprp>)) = {(}
	private void bexpr() {
		if(look.tag == Token.lpt.tag)
		{
			match(Token.lpt.tag);
			bexprp();
			match(Token.rpt.tag);
		}
		else
			error("error in bexpr()");
	}

	//Gui(<bexprp> -> RELOP<expr><expr>) = {R}
	private void bexprp() {
		if(look.tag == Tag.RELOP)
		{
			match(Tag.RELOP);
			expr();
			expr();
		}
		else
			error("errror in bexprp()");
	}

	private void expr() {
		switch(look.tag)
		{
			//Gui(<expr> -> NUM) = {NUM}
			case Tag.NUM:
				match(Tag.NUM);
				break;
			//Gui(<expr> -> ID) = {ID}
			case Tag.ID:
				match(Tag.ID);
				break;
			//Gui(<expr> -> (<exprp>)) = {(}
			case '(':
				match(Token.lpt.tag);
				exprp();
				match(Token.rpt.tag);
				break;
			default:
				error("error in expr()");
		}
	}

	private void exprp() {
		switch(look.tag)
		{
			//Gui(<exprp> -> +<exprlist>) = {+}
			case '+':
				match(Token.plus.tag);
				exprlist();
				break;
			//Gui(<exprp> -> -<expr><expr>) = {-}
			case '-':
				match(Token.minus.tag);
				expr();
				expr();
				break;
			//Gui(<exprp> -> *<exprlist>) = {*}
			case '*':
				match(Token.mult.tag);
				exprlist();
				break;
			//Gui(<exprp> -> /<expr><expr>) = {/}
			case '/':
				match(Token.div.tag);
				expr();
				expr();
				break;
			default:
				error("error in exprp()");
		}
	}

	//Gui(<exprlist> -> <expr><exprlistp>) = {NUM, ID, (}
	private void exprlist() {
		if(look.tag == Tag.NUM || look.tag == Tag.ID || look.tag == Token.lpt.tag)
		{
			expr();
			exprlistp();
		}
		else
			error("error in exprlist()");
	}

	private void exprlistp() {
		switch(look.tag)
		{
			//Gui(<exprlistp> -> <expr><exprlistp>) = {NUM, ID, (}
			case Tag.NUM:
			case Tag.ID:
			case '(':
				expr();
				exprlistp();
			//Gui(<exprlistp> -> ε) = {)}
			case ')':
				break;
			default:
				error("error in explistp()");
		}
	}

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "input.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            ParserLanguage parser = new ParserLanguage(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}