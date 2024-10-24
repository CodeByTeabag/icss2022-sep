package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private AST ast;

    //Use this to keep track of the parent nodes when recursively traversing the ast
    private HANStack<ASTNode> currentContainer;

    public ASTListener() {
        ast = new AST();
        currentContainer = new HANStack<>(); //TODO: Dit moet HANStack worden!!!!
    }

    public AST getAST() {
        return ast;
    }

    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        Stylesheet stylesheet = new Stylesheet();
        currentContainer.push(stylesheet);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        ast.root = (Stylesheet) currentContainer.pop();
    }

    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        VariableAssignment variableAssignment = new VariableAssignment();
        variableAssignment.name = new VariableReference(ctx.variableReference().getText());

        if (ctx.value().COLOR() != null) {
            variableAssignment.expression = new ColorLiteral(ctx.value().COLOR().getText());
        } else if (ctx.value().PIXELSIZE() != null) {
            variableAssignment.expression = new PixelLiteral(ctx.value().PIXELSIZE().getText());
        } else if (ctx.value().SCALAR() != null) {
            variableAssignment.expression = new ScalarLiteral(ctx.value().SCALAR().getText());
        } else if (ctx.value().PERCENTAGE() != null) {
            variableAssignment.expression = new PercentageLiteral(ctx.value().PERCENTAGE().getText());
        } else if (ctx.value().TRUE() != null || ctx.value().FALSE() != null) {
            variableAssignment.expression = new BoolLiteral(ctx.value().getText());
        }
        currentContainer.push(variableAssignment);
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        VariableAssignment variableAssignment = (VariableAssignment) currentContainer.pop();
        currentContainer.peek().addChild(variableAssignment);
    }

    @Override
    public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
        VariableReference variableReference = new VariableReference(ctx.getText());
        currentContainer.push(variableReference);
    }

    @Override
    public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
        VariableReference variableReference = (VariableReference) currentContainer.pop();
        currentContainer.peek().addChild(variableReference);
    }

    @Override
    public void enterStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule stylerule = new Stylerule();
        currentContainer.push(stylerule);
    }

    @Override
    public void exitStylerule(ICSSParser.StyleruleContext ctx) {
        Stylerule stylerule = (Stylerule) currentContainer.pop();
        currentContainer.peek().addChild(stylerule);
    }

    @Override
    public void enterSelector(ICSSParser.SelectorContext ctx) {
        Selector selector = null;
        if (ctx.ID_IDENT() != null) {
            selector = new IdSelector(ctx.getText());
        } else if (ctx.CLASS_IDENT() != null) {
            selector = new ClassSelector(ctx.getText());
        } else if (ctx.LOWER_IDENT() != null) {
            selector = new TagSelector(ctx.getText());
        }
        currentContainer.push(selector);
    }

    @Override
    public void exitSelector(ICSSParser.SelectorContext ctx) {
        Selector selector = (Selector) currentContainer.pop();

        Stylerule currentRule = (Stylerule) currentContainer.peek();
        currentRule.addChild(selector);
    }

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = new Declaration();
        PropertyName propertyName = new PropertyName(ctx.property().getText());
        declaration.property = propertyName;
        Expression expression = null;

        if (ctx.value().CAPITAL_IDENT() != null) {
            String variabeleName = ctx.value().CAPITAL_IDENT().getText();
            expression = new VariableReference(variabeleName);
        }
        // Color literal: #ffffff
        else if (ctx.value().COLOR() != null) {
            expression = new ColorLiteral(ctx.value().COLOR().getText());
        }
        // Pixel literal: 500px
        else if (ctx.value().PIXELSIZE() != null) {
            expression = new PixelLiteral(ctx.value().PIXELSIZE().getText());
        }
        // Scalar literal: 5
        else if (ctx.value().SCALAR() != null) {
            expression = new ScalarLiteral(ctx.value().SCALAR().getText());
        }
        // Percentage literal: %
        else if (ctx.value().PERCENTAGE() != null) {
            expression = new PercentageLiteral(ctx.value().PERCENTAGE().getText());
        }
        // Bool literal: True of False
        else if (ctx.value().TRUE() != null || ctx.value().FALSE() != null) {
            expression = new BoolLiteral(ctx.value().getText());
        }
        declaration.expression = expression;
        currentContainer.push(declaration);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        Declaration declaration = (Declaration) currentContainer.pop();
        currentContainer.peek().addChild(declaration);
    }
}