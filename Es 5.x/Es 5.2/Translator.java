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

public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    public Translator(Lexer l, BufferedReader br) {
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

    public void prog() {
	    if(look.tag == '(')
        {
            stat();
            match(Tag.EOF);
            try {
                code.toJasmin();
            }
            catch(java.io.IOException e) {
                System.out.println("IO error\n");
            };
        }
       else
            error("error in prog()");
    }

    private void statlist() {
        if(look.tag == '(')
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

    public void statp() {
        switch(look.tag) {
            case '=':
                match(Token.assign.tag);
                int read_id_addr = 0;
                if (look.tag==Tag.ID) {
                    read_id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (read_id_addr==-1) {
                        read_id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    match(Tag.ID);  
                }
                else
                    error("Error in grammar (stat) after read with " + look);
                expr();
                code.emit(OpCode.istore,read_id_addr); 
                break;
            case Tag.COND:
                match(Tag.COND);
                int bexpr_t = code.newLabel();
                int bexpr_f = code.newLabel();
                int bexpr_end = code.newLabel();
                bexpr(bexpr_t, bexpr_f);
                code.emitLabel(bexpr_t);
                stat();
                code.emit(OpCode.GOto, bexpr_end);
                code.emitLabel(bexpr_f);
                elseopt();
                code.emitLabel(bexpr_end);
                break;
            case Tag.WHILE:
                match(Tag.WHILE);
                int bexpr_start = code.newLabel();
                bexpr_t = code.newLabel();
                bexpr_f = code.newLabel();
                code.emitLabel(bexpr_start);
                bexpr(bexpr_t, bexpr_f);
                code.emitLabel(bexpr_t);
                stat();
                code.emit(OpCode.GOto, bexpr_start);
                code.emitLabel(bexpr_f);
                break;
            case Tag.DO:
                match(Tag.DO);
                statlist();
                break;
            case Tag.PRINT:
                match(Tag.PRINT);
                exprlist(OpCode.invokestatic);
                break;
            case Tag.READ:
                match(Tag.READ);
                if (look.tag==Tag.ID) {
                    read_id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (read_id_addr==-1) {
                        read_id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }
                    match(Tag.ID);
                    code.emit(OpCode.invokestatic,0); //read
                    code.emit(OpCode.istore,read_id_addr);
                }
                else
                    error("Error in grammar (stat) after read with " + look);
                break;
            default:
                error("error in statp()");
        }
    }

    private void elseopt() {
        switch(look.tag)
        {
            case '(':
                match(Token.lpt.tag);
                match(Tag.ELSE);
                stat();
                match(Token.rpt.tag);
            case ')':
                break;
            default:
                error("error in elseopt()");
        }
    }

    private void bexpr(int label_t, int label_f) {
        if(look.tag == Token.lpt.tag)
        {
            match(Token.lpt.tag);
            bexprp(label_t, label_f);
            match(Token.rpt.tag);
        }
        else
            error("error in bexpr()");
    }

/*
    Le condizioni di salto condizionato si implementano coma la negazione di 
    quelle che dovrebbero essere, in tal modo si implementa un meccanismo
    per cui se la condizione è vera non viene fatto alcun salto e si prosegue nel codice
    mentre se la condizione è falsa l'istruzione if_icmpXX salta alla porzione di codice
    da eseguire in caso di condizione falsa (else).
*/
  private void bexprp(int label_t, int label_f) {

        switch(look.tag)
        {
            case Tag.RELOP:
                OpCode ificmp = null;
                switch(((Word)look).lexeme)
                {
                    case "<":
                        ificmp = OpCode.if_icmpge;
                        break;
                    case ">":
                        ificmp = OpCode.if_icmple;
                        break;
                    case "==":
                        ificmp = OpCode.if_icmpne;
                        break;
                    case "<=":
                        ificmp = OpCode.if_icmpgt;
                        break;
                    case "<>":
                        ificmp = OpCode.if_icmpeq;
                        break;
                    case ">=":
                        ificmp = OpCode.if_icmplt;
                        break;
                }
                match(Tag.RELOP);
                expr();
                expr();
                code.emit(ificmp, label_f);
                break;

            case Tag.AND:
                match(Tag.AND);
                int second_check_and = code.newLabel();
                bexpr(second_check_and, label_f);
                code.emitLabel(second_check_and);
                bexpr(label_t, label_f);
                break;

            case Tag.OR:
                match(Tag.OR);
                int check_second_or_label = code.newLabel();
                bexpr(label_t, check_second_or_label);
                code.emit(OpCode.GOto, label_t);
                code.emitLabel(check_second_or_label);
                bexpr(label_t, label_f);
                break;

            case '!':
                match('!');
                bexpr(label_f, label_t);
                code.emit(OpCode.GOto, label_f);
                break;

            default:
                error("errror in bexprp()");
        }
    }


    private void expr() {
        switch(look.tag)
        {
            case Tag.NUM:
                code.emit(OpCode.ldc,((NumberTok)look).number);
                match(Tag.NUM);
                break;
            case Tag.ID:
                int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                if (read_id_addr==-1) {
                    //variable not declared, throw error here!
                    read_id_addr = count;
                    st.insert(((Word)look).lexeme,count++);
                }
                code.emit(OpCode.iload, read_id_addr);
                match(Tag.ID);
                break;
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
            case '+':
                match('+');
                exprlist(OpCode.iadd);
                break;
            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
                break;
            case '*':
                match(Token.mult.tag);
                exprlist(OpCode.imul);
                break;
            case '/':
                match(Token.div.tag);
                expr();
                expr();
                code.emit(OpCode.idiv);
                break;
            default:
                error("error in exprp()");
        }
    }

    private void exprlist(OpCode opCode) {
        if(look.tag == Tag.NUM || look.tag == Tag.ID || look.tag == Token.lpt.tag)
        {
            expr();
            if(opCode == OpCode.invokestatic)
                code.emit(OpCode.invokestatic, 1); //print
            exprlistp(opCode);
        }
        else
            error("error in exprlist()");
    }

    private void exprlistp(OpCode opCode) {
        switch(look.tag)
        {
            case Tag.NUM:
            case Tag.ID:
            case '(':
                expr();
                if(opCode == OpCode.invokestatic)
                    code.emit(OpCode.invokestatic, 1); //print
                else
                    code.emit(opCode);
                exprlistp(opCode);
            case ')':
                break;
            default:
                error("error in explistp()");
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "input.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator t = new Translator(lex, br);
            t.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}