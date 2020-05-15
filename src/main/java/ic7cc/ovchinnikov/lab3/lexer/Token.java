package ic7cc.ovchinnikov.lab3.lexer;

import com.kitfox.svg.A;
import ic7cc.ovchinnikov.lab3.model.Terminal;

public class Token extends Terminal {

    public static final Token LBRACE = new Token("LBRACE", "{");
    public static final Token RBRACE = new Token("RBRACE", "}");
    public static final Token SEMICOLON = new Token("SEMICOLON", ";");
    public static final Token ASSIGN = new Token("ASSIGN", "=");
    public static final Token OR = new Token("OR", "!");
    public static final Token AND = new Token("AND", "&");
    public static final Token NOT = new Token("NOT", "~");
    public static final Token TRUE = new Token("TRUE", "true");
    public static final Token FALSE = new Token("FALSE", "false");
    public static final Token IDENT = new Token("IDENT", "");
    public static final Token ERROR = new Token("ERROR", "");
    public static final Token END = new Token("END", "\0");

    private Token(String name, String spell) {
        super(name, spell);
    }

    public static Token buildID(String spell) {
        if (spell.charAt(0) == '$')
            return new Token("IDENT", spell);
        throw new UnsupportedOperationException();
    }

    public static Token buildError(String spell, Integer rowNumber, Integer columnNumber) {
        return new Token("ERROR", String.format("%s: (%d, %d)", spell, rowNumber, columnNumber));
    }

    public static boolean isValidToken(Token token) {
        return token.equals(LBRACE) || token.equals(RBRACE) || token.equals(SEMICOLON) ||
                token.equals(ASSIGN) || token.equals(OR) || token.equals(AND) ||
                token.equals(NOT) || token.equals(TRUE) || token.equals(FALSE) || token.getName().equals(IDENT.getName());
    }
}
