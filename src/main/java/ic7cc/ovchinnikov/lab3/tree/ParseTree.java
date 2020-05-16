package ic7cc.ovchinnikov.lab3.tree;

import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;

import static guru.nidi.graphviz.model.Factory.*;
import static javax.swing.JSplitPane.LEFT;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ParseTree {

    private ParseTreeNode root;

    public ParseTree(String rootText) {
        root = new ParseTreeNode(rootText, ParseTreeNode.Type.NON_TERMINAL);
    }

    public ParseTreeNode getRoot() {
        return root;
    }

    public static boolean addNode(ParseTreeNode parent, ParseTreeNode newNode) {
        if (parent != null && newNode != null) {
            return parent.getChildren().add(newNode);
        }
        return false;
    }

    public void clear() {
        root = null;
    }

    public void printPNG(String fullName) throws IOException {
        Deque<ParseTreeNode> deque = new ArrayDeque<>();
        deque.push(root);

        Graph g = graph("Parse Tree").directed();
        while (!deque.isEmpty()) {
            ParseTreeNode popNode = deque.pop();
            Color color = Color.rgb("1020d0");
            if (popNode.isError)
                color = Color.rgb("FF0000");
            Node oldNode = node(popNode.getId().toString()).with(Label.html(popNode.getData()), color.font());
            for (Object obj : popNode.getChildren()) {
                ParseTreeNode n = (ParseTreeNode) obj;
                Color colorN = Color.rgb("1020d0");
                if (n.isError)
                    colorN = Color.rgb("FF0000");
                Node newNode = node(n.getId().toString()).with(Label.html(n.getData()), colorN.font());
                oldNode = oldNode.link(to(newNode));
                deque.push(n);
            }
            g = g.with(oldNode);
        }
        Graphviz.fromGraph(g).width(4096).render(Format.PNG).toFile(new File(fullName));
    }

    public Map<String, Integer> info() {
        Map<String, Integer> infoMap = new HashMap<>();
        Deque<ParseTreeNode> deque = new ArrayDeque<>();
        deque.push(root);
        infoMap.put("Number of mistakes", 0);
        infoMap.put("Number of nodes", 0);
        while (!deque.isEmpty()) {
            ParseTreeNode popNode = deque.pop();
            int countNode = infoMap.get("Number of nodes");
            infoMap.put("Number of nodes", ++countNode);
            if (popNode.isError) {
                int countError = infoMap.get("Number of mistakes");
                infoMap.put("Number of mistakes", ++countError);
            }
            for (Object obj : popNode.getChildren()) {
                ParseTreeNode n = (ParseTreeNode) obj;
                deque.push(n);
            }
        }
        return infoMap;
    }

    public static class ParseTreeNode extends TreeNode<String> {

        private final UUID id;
        private boolean isError;
        private final Type type;

        public ParseTreeNode(String data, Type type) {
            super(data);
            this.id = UUID.randomUUID();
            this.type = type;
        }

        public UUID getId() {
            return id;
        }

        public Type getType() {
            return type;
        }

        public void setError(boolean error) {
            this.isError = error;
        }

        public boolean isError() {
            return isError;
        }

        public enum Type {
            TERMINAL, NON_TERMINAL
        }
    }
}
