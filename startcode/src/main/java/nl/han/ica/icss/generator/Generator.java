package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;

public class Generator {

    public String generate(AST ast) {
        return generateStylesheet(ast.root);
    }

    private String generateStylesheet(Stylesheet root) {
        StringBuilder result = new StringBuilder();
        for (ASTNode child : root.getChildren()) {
            if (child instanceof Stylerule) {
                result.append(generateStylerule((Stylerule) child));
            }
        }
        return result.toString();
    }

    private String generateStylerule(Stylerule stylerule) {
        StringBuilder result = new StringBuilder();

        result.append(generateSelector(stylerule));
        result.append(" {\n");
        result.append(generateDeclaration(stylerule));
        result.append("}\n");

        return result.toString();
    }

    private String generateSelector(Stylerule stylerule) {
        StringBuilder result = new StringBuilder();
        for (ASTNode selector : stylerule.selectors) {
            result.append(selector.toString());
        }
        return result.toString();
    }

    private String generateDeclaration(Stylerule stylerule) {
        StringBuilder result = new StringBuilder();
        for (ASTNode node : stylerule.getChildren()) {
            if (node instanceof Declaration) {
                Declaration declaration = (Declaration) node;
                result
                        .append("\t ")
                        .append(declaration.property.name)
                        .append(": ");
                if (declaration.expression != null) {
                    result.append(generateExpression(declaration.expression));
                }
                result.append(";\n");
            }
        }
        return result.toString();
    }

    private String generateExpression(Expression expression) {
        StringBuilder exprResult = new StringBuilder();

        if (expression instanceof PixelLiteral) {
            PixelLiteral pixelLiteral = (PixelLiteral) expression;
            exprResult
                    .append(pixelLiteral.value)
                    .append("px");
        } else if (expression instanceof PercentageLiteral) {
            PercentageLiteral percentageLiteral = (PercentageLiteral) expression;
            exprResult
                    .append(percentageLiteral.value)
                    .append("%");
        } else if (expression instanceof ColorLiteral) {
            ColorLiteral colorLiteral = (ColorLiteral) expression;
            exprResult.append(colorLiteral.value);
        } else if (expression instanceof ScalarLiteral) {
            ScalarLiteral scalarLiteral = (ScalarLiteral) expression;
            exprResult.append(scalarLiteral.value);
        }
        return exprResult.toString();
    }
}
