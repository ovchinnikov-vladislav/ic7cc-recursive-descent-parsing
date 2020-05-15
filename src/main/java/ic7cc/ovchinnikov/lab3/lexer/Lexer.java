package ic7cc.ovchinnikov.lab3.lexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Lexer {

    private int prevPointer;
    private int pointer;
    private int rowNumber;
    private int columnNumber;
    private BufferedReader reader;
    private String sourceCode;

    public Lexer(String path) {
        try {
            reader = new BufferedReader(new FileReader(path));
            this.pointer = 0;
            this.prevPointer = 0;
            this.columnNumber = 1;
            this.rowNumber = 0;
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public boolean hasNext() {
        try {
            return reader.ready() || hasNextString();
        } catch (IOException exc) {
            exc.printStackTrace();
            return false;
        }
    }

    private boolean hasNextString() {
        return sourceCode != null && pointer < sourceCode.length() && !sourceCode.isEmpty();
    }

    private void setNewString(String string) {
        this.sourceCode = string;
        this.rowNumber++;
        this.columnNumber = 1;
        this.pointer = 0;
    }

    public Token next() {
        Token token;
        if (!hasNextString()) {
            try {
                setNewString(reader.readLine() + "\n");
                while (reader.ready() && (sourceCode.isEmpty())) {
                    setNewString(reader.readLine() + "\n");
                }
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
        prevPointer = pointer;
        switch (sourceCode.charAt(pointer++)) {
            case '{':
                token = Token.LBRACE;
                break;
            case '}':
                token = Token.RBRACE;
                break;
            case ';':
                token = Token.SEMICOLON;
                break;
            case '=':
                token = Token.ASSIGN;
                break;
            case '!':
                token = Token.OR;
                break;
            case '&':
                token = Token.AND;
                break;
            case '~':
                token = Token.NOT;
                break;
            case '$':
                token = generateIdentToken();
                break;
            case 't':
                token = generateTrueToken();
                pointer++;
                break;
            case 'f':
                token = generateFalseToken();
                pointer++;
                break;
            case '\n':
            case '\r':
                columnNumber = 1;
            case '\t':
            case ' ':
                while (pointer < sourceCode.length() && Character.isSpaceChar(sourceCode.charAt(pointer))) {
                    pointer++;
                }
                token = next();
                break;
            case '\0':
                token = Token.END;
                break;
            default:
                if (sourceCode.equals("null\n"))
                    token = Token.END;
                else
                    token = generateErrorToken();
        }
        return token;
    }

    public void back() {
        pointer = prevPointer;
    }

    private Token generateTrueToken() {
        StringBuilder trueString = new StringBuilder("t");
        if (sourceCode.charAt(pointer) == 'r')
            trueString.append('r');
        else pointer--;
        if (sourceCode.charAt(++pointer) == 'u')
            trueString.append('u');
        else pointer--;
        if (sourceCode.charAt(++pointer) == 'e')
            trueString.append('e');
        else pointer--;
        String trueToken = trueString.toString();
        if (trueToken.equals(Token.TRUE.getSpell())) {
            return Token.TRUE;
        } else {
            return Token.buildError(trueToken, rowNumber, pointer);
        }
    }

    private Token generateFalseToken() {
        StringBuilder falseString = new StringBuilder("f");
        if (sourceCode.charAt(pointer) == 'a')
            falseString.append('a');
        else pointer--;
        if (sourceCode.charAt(++pointer) == 'l')
            falseString.append('l');
        else pointer--;
        if (sourceCode.charAt(++pointer) == 's')
            falseString.append('s');
        else pointer--;
        if (sourceCode.charAt(++pointer) == 'e')
            falseString.append('e');
        else pointer--;
        String falseToken = falseString.toString();
        if (falseToken.equals(Token.FALSE.getSpell())) {
            return Token.FALSE;
        } else {
            return Token.buildError(falseToken, rowNumber, pointer);
        }
    }

    private Token generateIdentToken() {
        StringBuilder ident = new StringBuilder("$");
        for (int i = pointer; i < sourceCode.length(); i++) {
            pointer++;
            char c = sourceCode.charAt(i);
            if (Character.isDigit(c) || Character.isAlphabetic(c))
                ident.append(sourceCode.charAt(i));
            else if (Character.isSpaceChar(c) || c == '=' || c == '&' || c == '!' || c == '~' || c == ';' || c == '}' || c == '{') {
                pointer--;
                break;
            }
        }
        if (ident.length() > 1)
            return Token.buildID(ident.toString());
        else
            return Token.buildError(ident.toString(), rowNumber, pointer);
    }

    private Token generateErrorToken() {
        StringBuilder ident = new StringBuilder(sourceCode.charAt(pointer-1) + "");
        for (int i = pointer; i < sourceCode.length(); i++) {
            pointer++;
            char c = sourceCode.charAt(i);
            if (Character.isDigit(c) || Character.isAlphabetic(c))
                ident.append(sourceCode.charAt(i));
            else if (Character.isSpaceChar(c) || c == '=' || c == '&' || c == '!' || c == '~' || c == ';' || c == '}' || c == '{') {
                pointer--;
                break;
            }
        }

        return Token.buildError(ident.toString(), rowNumber, pointer);
    }

    public String point() {
        return String.format("(%d, %d)", rowNumber, pointer);
    }
}
