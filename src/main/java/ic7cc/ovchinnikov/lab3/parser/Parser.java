package ic7cc.ovchinnikov.lab3.parser;

import ic7cc.ovchinnikov.lab3.lexer.Lexer;
import ic7cc.ovchinnikov.lab3.lexer.Token;
import ic7cc.ovchinnikov.lab3.tree.ParseTree;

import static ic7cc.ovchinnikov.lab3.tree.ParseTree.ParseTreeNode;

import java.io.*;

public class Parser {

    private Lexer lexer;
    private Token pointer;
    private ParseTree tree;

    public Parser(String path) throws IOException {
        this.lexer = new Lexer(path);
    }

    public void parse() {
        this.pointer = lexer.next();
        tree = new ParseTree("&lt;program&gt;");
        program(tree.getRoot());
    }

    // Все верно
    private void program(ParseTreeNode node) {
        ParseTreeNode block = createNonTerminalNode("block");
        ParseTree.addNode(node, block);
        block(block);
        if (lexer.hasNext()) {
            System.out.println("format program error: " + lexer.point());
            node.setError(true);
        }
    }

    // Все верно
    public void block(ParseTreeNode node) {
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
        } else {
            System.out.println("expected rbrace: " + lexer.point());
            node.setError(true);
        }
    }

    // Все верно
    public void operatorsList(ParseTreeNode node) {
        ParseTreeNode operator = createNonTerminalNode("operator");
        ParseTree.addNode(node, operator);
        operator(operator);
        ParseTreeNode tail = createNonTerminalNode("tail");
        ParseTree.addNode(node, tail);
        tail(tail);
    }

    // Все верно
    public void operator(ParseTreeNode node) {
        String point = lexer.point();
        if (!pointer.getName().equals(Token.IDENT.getName()) && !pointer.equals(Token.LBRACE)) {
            System.out.println("expected id or lbrace: " + point);
            node.setError(true);
            pointer = lexer.next();

            if (pointer.equals(Token.ASSIGN)) {
                ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
                pointer = lexer.next();
                ParseTreeNode expression = createNonTerminalNode("expression");
                ParseTree.addNode(node, expression);
                expression(expression);
            } else {
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
            }
        } else if (pointer.getName().equals(Token.IDENT.getName())) {
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
        }
    }

    public void tail(ParseTreeNode node) {
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

    // Все верно
    public void expression(ParseTreeNode node) {
        ParseTreeNode boolExpression = createNonTerminalNode("bool_expression");
        ParseTree.addNode(node, boolExpression);
        boolExpression(boolExpression);
    }

    // Все верно
    public void boolExpression(ParseTreeNode node) {
        ParseTreeNode boolMonomial = createNonTerminalNode("bool_monomial");
        ParseTree.addNode(node, boolMonomial);
        boolMonomial(boolMonomial);
        ParseTreeNode _boolExpression = createNonTerminalNode("bool_expression'");
        ParseTree.addNode(node, _boolExpression);
        _boolExpression(_boolExpression);
    }

    // Все верно
    public void boolMonomial(ParseTreeNode node) {
        ParseTreeNode secondaryBoolExpression = createNonTerminalNode("secondary_bool_expression");
        ParseTree.addNode(node, secondaryBoolExpression);
        secondaryBoolExpression(secondaryBoolExpression);
        ParseTreeNode _boolMonomial = createNonTerminalNode("bool_monomial'");
        ParseTree.addNode(node, _boolMonomial);
        _boolMonomial(_boolMonomial);
    }

    // Все верно
    public void _boolExpression(ParseTreeNode node) {
        if (pointer.equals(Token.OR)) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
            ParseTreeNode boolMonomial = createNonTerminalNode("bool_monomial");
            ParseTree.addNode(node, boolMonomial);
            boolMonomial(boolMonomial);
            ParseTreeNode _boolExpression = createNonTerminalNode("bool_expression'");
            ParseTree.addNode(node, _boolExpression);
            _boolExpression(_boolExpression);
        } else if (pointer.getName().equals(Token.ERROR.getName())) {
            System.out.println("expected bool_expression: " + pointer.getSpell());
            pointer = lexer.next();
            node.setError(true);
            ParseTreeNode boolMonomial = createNonTerminalNode("bool_monomial");
            ParseTree.addNode(node, boolMonomial);
            boolMonomial(boolMonomial);
            ParseTreeNode _boolExpression = createNonTerminalNode("bool_expression'");
            ParseTree.addNode(node, _boolExpression);
            _boolExpression(_boolExpression);
        } else {
            ParseTree.addNode(node, createTerminalNode("\u03B5"));
        }
    }

    // Все верно
    public void secondaryBoolExpression(ParseTreeNode node) {
        if (pointer.equals(Token.NOT)) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
        }
        ParseTreeNode boolValue = createNonTerminalNode("bool_value");
        ParseTree.addNode(node, boolValue);
        boolValue(boolValue);
    }

    // Все верно
    public void _boolMonomial(ParseTreeNode node) {
        if (pointer.equals(Token.AND)) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
            ParseTreeNode secondaryBoolExpression = createNonTerminalNode("secondary_bool_expression");
            ParseTree.addNode(node, secondaryBoolExpression);
            secondaryBoolExpression(secondaryBoolExpression);
            ParseTreeNode _boolMonomial = createNonTerminalNode("bool_monomial'");
            ParseTree.addNode(node, _boolMonomial);
            _boolMonomial(_boolMonomial);
        } else if (pointer.getName().equals(Token.ERROR.getName())) {
            System.out.println("expected bool_expression: " + pointer.getSpell());
            pointer = lexer.next();
            node.setError(true);
            ParseTreeNode secondaryBoolExpression = createNonTerminalNode("secondary_bool_expression");
            ParseTree.addNode(node, secondaryBoolExpression);
            secondaryBoolExpression(secondaryBoolExpression);
            ParseTreeNode _boolMonomial = createNonTerminalNode("bool_monomial'");
            ParseTree.addNode(node, _boolMonomial);
            _boolMonomial(_boolMonomial);
        } else {
            ParseTree.addNode(node, createTerminalNode("\u03B5"));
        }
    }

    // Все верно
    public void boolValue(ParseTreeNode node) {
        if (pointer.getName().equals(Token.IDENT.getName()) || pointer.equals(Token.TRUE) || pointer.equals(Token.FALSE)) {
            ParseTree.addNode(node, createTerminalNode(pointer.getSpell()));
            pointer = lexer.next();
        } else if (pointer.getName().equals(Token.ERROR.getName())) {
            System.out.println("expected id, true or false: " + lexer.point());
            pointer = lexer.next();
            node.setError(true);
        } else {
            System.out.println("expected id, true or false: " + lexer.point());
            node.setError(true);
        }
    }

    public ParseTreeNode createTerminalNode(String spell) {
        if (spell.equals("&"))
            spell = "&amp;";
        return new ParseTreeNode(spell, ParseTreeNode.Type.TERMINAL);
    }

    public ParseTreeNode createNonTerminalNode(String name) {
        return new ParseTreeNode("&lt;" + name + "&gt;", ParseTreeNode.Type.NON_TERMINAL);
    }

    public void printParseTreePNG() throws IOException {
        tree.printPNG("result/parse_tree.png");
    }
}