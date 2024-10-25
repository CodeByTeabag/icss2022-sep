package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.HashMap;

public class Checker {
    private final HANLinkedList<HashMap<String, ExpressionType>> VARIABLE_TYPES;

    public Checker() {
        VARIABLE_TYPES = new HANLinkedList<>();
        VARIABLE_TYPES.addFirst(new HashMap<>());
    }

    public void check(AST ast) {
        checkStylesheet(ast.root);
    }

    private void checkStylesheet(Stylesheet node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof VariableAssignment) {
                checkVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Stylerule) {
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
            if (isValidVariable(node)) {
                checkVariable(node.expression);
            }

            else if (isValidLiteralForWidth(node)) {
                node.setError("Prop 'width' had no valid type");
            }
        }
        else if (node.property.name.equals("color")) {
            if (isValidVariable(node)) {
                checkVariable(node.expression);
            }

            else if (isValidLiteralForColor(node)) {
                node.setError("Prop 'color' had no valid type");
            }
        }
        else if (node.property.name.equals("background-color")) {
            if (isValidVariable(node)) {
                checkVariable(node.expression);
            }

            else if (isValidLiteralForBackgroundColor(node)) {
                node.setError("Prop 'bg-color' had no valid type");
            }
        }
    }

    public void checkVariable(Expression expression) {
        if (expression instanceof VariableReference) {
            String variableName = ((VariableReference) expression).name;

            ExpressionType type = findVariableType(variableName);
            if (type == null) {
                expression.setError("Variable " + variableName + " is not defined.");
            }
        }
    }

    private ExpressionType findVariableType(String variableName) {
        for (int i = 0; i < VARIABLE_TYPES.getSize(); i++) {
            HashMap<String, ExpressionType> scope = VARIABLE_TYPES.get(i);
            if (scope.containsKey(variableName)) {
                return scope.get(variableName);
            }
        }
        return null;
    }

    private void checkVariableAssignment(VariableAssignment node) {
        ExpressionType type = determineExpressionType(node.expression);

        if (type != ExpressionType.UNDEFINED) {
            VARIABLE_TYPES.getFirst().put(node.name.name, type);
        } else {
            node.setError("Invalid type for variable assignment: " + node.name.name);
        }
    }

    private ExpressionType determineExpressionType(Expression expression) {
        ExpressionType expressionType = null;

        if (expression instanceof ColorLiteral) {
            expressionType = ExpressionType.COLOR;
        } else if (expression instanceof PixelLiteral) {
            expressionType = ExpressionType.PIXEL;
        } else if (expression instanceof PercentageLiteral) {
            expressionType = ExpressionType.PERCENTAGE;
        } else if (expression instanceof BoolLiteral) {
            expressionType = ExpressionType.BOOL;
        }
        return expressionType;
    }

    public boolean isValidVariable(Declaration node) {
        return node.expression instanceof VariableReference;
    }

    public boolean isValidLiteralForWidth(Declaration node) {
        return !(node.expression instanceof PixelLiteral) && !(node.expression instanceof PercentageLiteral);
    }

    public boolean isValidLiteralForColor(Declaration node) {
        return !(node.expression instanceof ColorLiteral);
    }

    public boolean isValidLiteralForBackgroundColor(Declaration node) {
        return !(node.expression instanceof ColorLiteral);
    }
}
