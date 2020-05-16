package ic7cc.ovchinnikov.lab3.parser;

import ic7cc.ovchinnikov.lab3.lexer.Lexer;
import ic7cc.ovchinnikov.lab3.model.Token;
import ic7cc.ovchinnikov.lab3.tree.ParseTree;

import static ic7cc.ovchinnikov.lab3.tree.ParseTree.ParseTreeNode;

import java.io.*;
import java.util.Map;

public class Parser {

    private Lexer lexer;
    private Token pointer;
    private ParseTree tree;

    public Parser(String path) {
        this.lexer = new Lexer(path);
    }

    public void parse() {
        this.pointer = lexer.next();
        tree = new ParseTree("&lt;program&gt;");
        program(tree.getRoot());
    }

    private void program(ParseTreeNode node) {
        if (pointer.equals(Token.LBRACE)) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
        } else {
            System.out.println("expected lbrace: " + lexer.point());
            node.setError(true);
        }

        ParseTreeNode operatorsList = createNonTerminalNode("operators_list");
        ParseTree.addNode(node, operatorsList);
        operatorsList(operatorsList);
        if (pointer.equals(Token.RBRACE)) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
            if (!pointer.equals(Token.END)) {
                System.out.println("program structure failed: " + lexer.point());
                node.setError(true);
            }
        } else {
            System.out.println("expected rbrace: " + lexer.point());
            node.setError(true);
        }
    }

    private void operatorsList(ParseTreeNode node) {
        ParseTreeNode operator = createNonTerminalNode("operator");
        ParseTree.addNode(node, operator);
        operator(operator);
        ParseTreeNode tail = createNonTerminalNode("tail");
        ParseTree.addNode(node, tail);
        tail(tail);
    }

    private void operator(ParseTreeNode node) {
        String point = lexer.point();
        if (pointer.getName().equals(Token.IDENT.getName())) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
            if (pointer.equals(Token.ASSIGN)) {
                ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
                pointer = lexer.next();
            } else {
                System.out.println("expected assign: " + point);
                node.setError(true);
            }
            ParseTreeNode expression = createNonTerminalNode("expression");
            ParseTree.addNode(node, expression);
            expression(expression);
        } else if (pointer.equals(Token.LBRACE)) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
            ParseTreeNode operatorsList = createNonTerminalNode("operators_list");
            ParseTree.addNode(node, operatorsList);
            operatorsList(operatorsList);
            if (pointer.equals(Token.RBRACE)) {
                ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
                pointer = lexer.next();
            } else {
                System.out.println("expected rbrace: " + point);
                node.setError(true);
            }
        } else if (pointer.getName().equals(Token.ERROR.getName())) {
            System.out.println("expected operator: " + point);
            node.setError(true);
            pointer = lexer.next();
            if (pointer.equals(Token.ASSIGN)) {
                ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
                pointer = lexer.next();
                ParseTreeNode expression = createNonTerminalNode("expression");
                ParseTree.addNode(node, expression);
                expression(expression);
            }
        } else if (pointer.equals(Token.RBRACE) || pointer.equals(Token.END)) {
            System.out.println("expected operator: " + point);
            node.setError(true);
        } else {
            while ((Token.isOperator(pointer) && !pointer.equals(Token.ASSIGN)) || Token.isLiteral(pointer))
                pointer = lexer.next();
            operator(node);
        }
    }

    private void tail(ParseTreeNode node) {
        if (pointer.equals(Token.SEMICOLON)) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
            ParseTreeNode operator = createNonTerminalNode("operator");
            ParseTree.addNode(node, operator);
            operator(operator);
            ParseTreeNode tail = createNonTerminalNode("tail");
            ParseTree.addNode(node, tail);
            tail(tail);
        } else if (pointer.getName().equals(Token.ERROR.getName())) {
            System.out.println("expected semicolon: " + lexer.point());
            pointer = lexer.next();
            node.setError(true);
            ParseTreeNode operator = createNonTerminalNode("operator");
            ParseTree.addNode(node, operator);
            operator(operator);
            ParseTreeNode tail = createNonTerminalNode("tail");
            ParseTree.addNode(node, tail);
            tail(tail);
        } else if (pointer.getName().equals(Token.IDENT.getName()) || pointer.equals(Token.LBRACE)) {
            System.out.println("expected semicolon: " + lexer.point());
            node.setError(true);
            ParseTreeNode operator = createNonTerminalNode("operator");
            ParseTree.addNode(node, operator);
            operator(operator);
            ParseTreeNode tail = createNonTerminalNode("tail");
            ParseTree.addNode(node, tail);
            tail(tail);
        } else {
            ParseTree.addNode(node, createTerminalNode("\u03B5"));
        }
    }

    private void expression(ParseTreeNode node) {
        ParseTreeNode boolMonomial = createNonTerminalNode("bool_monomial");
        ParseTree.addNode(node, boolMonomial);
        boolMonomial(boolMonomial);
        ParseTreeNode boolExpression = createNonTerminalNode("bool_expression");
        ParseTree.addNode(node, boolExpression);
        boolExpression(boolExpression);
    }

    private void boolMonomial(ParseTreeNode node) {
        ParseTreeNode boolValue = createNonTerminalNode("boolValue");
        ParseTree.addNode(node, boolValue);
        boolValue(boolValue);
        ParseTreeNode _boolMonomial = createNonTerminalNode("bool_monomial'");
        ParseTree.addNode(node, _boolMonomial);
        _boolMonomial(_boolMonomial);
    }

    private void boolExpression(ParseTreeNode node) {
        if (pointer.equals(Token.OR)) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
            ParseTreeNode boolMonomial = createNonTerminalNode("bool_monomial");
            ParseTree.addNode(node, boolMonomial);
            boolMonomial(boolMonomial);
            ParseTreeNode boolExpression = createNonTerminalNode("bool_expression");
            ParseTree.addNode(node, boolExpression);
            boolExpression(boolExpression);
        } else if (pointer.getName().equals(Token.ERROR.getName())) {
            System.out.println("expected bool_expression: " + pointer.getSpell());
            pointer = lexer.next();
            node.setError(true);
            boolExpression(node);
        } else {
            ParseTree.addNode(node, createTerminalNode("\u03B5"));
        }
    }

    private void boolValue(ParseTreeNode node) {
        if (pointer.equals(Token.NOT)) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
            if (pointer.getName().equals(Token.IDENT.getName()) || pointer.equals(Token.TRUE) || pointer.equals(Token.FALSE)) {
                ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
                pointer = lexer.next();
            } else if (pointer.getName().equals(Token.ERROR.getName())) {
                System.out.println("expected id, true or false: " + lexer.point());
                pointer = lexer.next();
                node.setError(true);
                boolValue(node);
            } else {
                System.out.println("expected id, true or false: " + lexer.point());
                node.setError(true);
            }
        } else if (pointer.getName().equals(Token.ERROR.getName())) {
            System.out.println("expected bool value: " + lexer.point());
            pointer = lexer.next();
            node.setError(true);
            boolValue(node);
        } else {
            if (pointer.getName().equals(Token.IDENT.getName()) || pointer.equals(Token.TRUE) || pointer.equals(Token.FALSE)) {
                ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
                pointer = lexer.next();
            } else if (pointer.getName().equals(Token.ERROR.getName())) {
                System.out.println("expected id, true or false: " + lexer.point());
                pointer = lexer.next();
                node.setError(true);
                boolValue(node);
            } else {
                System.out.println("expected id, true or false: " + lexer.point());
                node.setError(true);
            }
        }
    }

    private void _boolMonomial(ParseTreeNode node) {
        if (pointer.equals(Token.AND)) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
            ParseTreeNode boolValue = createNonTerminalNode("boolValue");
            ParseTree.addNode(node, boolValue);
            boolValue(boolValue);
            ParseTreeNode _boolMonomial = createNonTerminalNode("bool_monomial'");
            ParseTree.addNode(node, _boolMonomial);
            _boolMonomial(_boolMonomial);
        } else if (pointer.getName().equals(Token.ERROR.getName())) {
            System.out.println("expected bool_expression: " + pointer.getSpell());
            pointer = lexer.next();
            node.setError(true);
            ParseTreeNode boolValue = createNonTerminalNode("boolValue");
            ParseTree.addNode(node, boolValue);
            boolValue(boolValue);
            ParseTreeNode _boolMonomial = createNonTerminalNode("bool_monomial'");
            ParseTree.addNode(node, _boolMonomial);
            _boolMonomial(_boolMonomial);
        } else {
            ParseTree.addNode(node, createTerminalNode("\u03B5"));
        }
    }

    private ParseTreeNode createTerminalNode(String spell) {
        if (spell.equals("&"))
            spell = "&amp;";
        return new ParseTreeNode(spell, ParseTreeNode.Type.TERMINAL);
    }

    private ParseTreeNode createNonTerminalNode(String name) {
        return new ParseTreeNode("&lt;" + name + "&gt;", ParseTreeNode.Type.NON_TERMINAL);
    }

    public void printParseTreePNG(String filename) throws IOException {
        tree.printPNG(filename);
    }

    public Map<String, Integer> info() {
        return tree.info();
    }
}
