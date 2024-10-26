package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.BoolLiteral;

import java.util.HashMap;

public class Evaluator implements Transform {

    private final IHANLinkedList<HashMap<String, Literal>> VARIABLE_VALUES;

    public Evaluator() {
        VARIABLE_VALUES = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        applyStylesheet(ast.root);
    }

    private void applyStylesheet(Stylesheet node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Stylerule) {
                applyStylerule((Stylerule) child);
            }
        }
    }

    private void applyStylerule(Stylerule node) {
        for (ASTNode child : node.getChildren()) {
            if (child instanceof Declaration) {
                applyDeclaration((Declaration) child);
            }
        }

    }

    private void applyDeclaration(Declaration node) {
        Literal literal = evalExpression(node.expression);

        if (literal != null) {
            node.expression = literal;
        }
    }

    public Literal evalExpression(Expression expression) {
        if (expression instanceof VariableReference) {
            return findVariableValue(((VariableReference) expression).name);
        }
        else if (expression instanceof PixelLiteral) {
            return (PixelLiteral) expression;
        }
        else if (expression instanceof PercentageLiteral) {
            return (PercentageLiteral) expression;
        }
        else if (expression instanceof BoolLiteral) {
            return (BoolLiteral) expression;
        }
        return null;
    }

    private Literal findVariableValue(String variableName) {
        for (int i = 0; i < VARIABLE_VALUES.getSize(); i++) {
            HashMap<String, Literal> scope = VARIABLE_VALUES.get(i);
            if (scope.containsKey(variableName)) {
                return scope.get(variableName);
            }
        }
        return null;
    }
}
