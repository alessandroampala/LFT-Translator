import java.io.*;
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;
            case '(':
                peek = ' ';
                return Token.lpt;
            case ')':
                peek = ' ';
                return Token.rpt;
            case '{':
                peek = ' ';
                return Token.lpg;
            case '}':
                peek = ' ';
                return Token.rpg;
            case '+':
                peek = ' ';
                return Token.plus;
            case '-':
                peek = ' ';
                return Token.minus;
            case '*':
                peek = ' ';
                return Token.mult;
            case '/':
                peek = ' ';
                return Token.div;
            case ';':
                peek = ' ';
                return Token.semicolon;

	// ... gestire i casi di (, ), {, }, +, -, *, /, ; ... //

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character after & : "  + peek );
                    return null;
                }

	// ... gestire i casi di ||, <, >, <=, >=, ==, <>, = ... //
            case '|':
                readch(br);
                if(peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character after | : "  + peek );
                    return null;
                }
            case '<':
                readch(br);
                if(peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if(peek == '>') {
                    peek = ' ';
                    return Word.ne;
                } else
                    return Word.lt;
            case '>':
                readch(br);
                if(peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else
                    return Word.gt;
            case '=':
                readch(br);
                if(peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else
                    return Token.assign;


            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek)) {
                // ... gestire il caso degli identificatori e delle parole chiave //
                    String s = "";
                    do {
                        s += peek;
                        readch(br);
                    } while(Character.isLetter(peek) || Character.isDigit(peek));
                    if(s.equals(Word.cond.lexeme))
                        return Word.cond;
                    else if(s.equals(Word.when.lexeme))
                        return Word.when;
                    else if(s.equals(Word.then.lexeme))
                        return Word.then;
                    else if(s.equals(Word.elsetok.lexeme))
                        return Word.elsetok;
                    else if(s.equals(Word.whiletok.lexeme))
                        return Word.whiletok;
                    else if(s.equals(Word.dotok.lexeme))
                        return Word.dotok;
                    else if(s.equals(Word.seq.lexeme))
                        return Word.seq;
                    else if(s.equals(Word.print.lexeme))
                        return Word.print;
                    else if(s.equals(Word.read.lexeme))
                        return Word.read;
                    else
                        return new Word(Tag.ID, s);
                } else if (Character.isDigit(peek)) {
	           // ... gestire il caso dei numeri ... //
                    String s = "";
                    do {
                        s += peek;
                        readch(br);
                    } while(Character.isDigit(peek));
                    if(s.charAt(0) == '0' && s.length() > 1)
                    {
                        System.err.println("Error. Number starts with 0: " + s);
                        return null;
                    }
                    else
                        return new NumberTok(Integer.parseInt(s));
                } else {
                        System.err.println("Erroneous character: " + peek );
                        return null;
                }
         }
    }
		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "prova.txt"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}