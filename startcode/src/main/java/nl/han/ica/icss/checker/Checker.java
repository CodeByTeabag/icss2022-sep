package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Stylerule) {
                checkStylerule((Stylerule) child);
            }
        }
    }

    private void checkStylerule(Stylerule node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                checkDeclaration((Declaration) child);
            }
        }
    }

    private void checkDeclaration(Declaration node) {
        if (node.property.name.equals("width")) {
            if (!(node.expression instanceof PixelLiteral)) {
                node.setError("Prop 'width' had no valid type");
            }
        }
        else if (node.property.name.equals("color")) {
            if (!(node.expression instanceof ColorLiteral)) {
                node.setError("Prop 'color' had no valid type");
            }
        }
        else if (node.property.name.equals("background-color")) {
            if (!(node.expression instanceof ColorLiteral)) {
                node.setError("Prop 'bg-color' had no valid type");
            }
        }
    }


}
