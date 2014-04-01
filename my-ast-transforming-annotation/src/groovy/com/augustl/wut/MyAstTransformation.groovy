package com.augustl.wut

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.VariableScope
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.DeclarationExpression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.ExpressionStatement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Token
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import java.lang.reflect.Modifier

@GroovyASTTransformation(phase = CompilePhase.CANONICALIZATION)
class MyAstTransformation implements ASTTransformation {
    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        if (!(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof MethodNode)) {
            throw new RuntimeException('Internal error: expected @MyAstTransformingAnnotation annotation to be used on a method.')
        }

        AnnotationNode annotation = (AnnotationNode)nodes[0]
        MethodNode method = (MethodNode)nodes[1]

        method.declaringClass.addField(
            new FieldNode(
                "myField",
                Modifier.PRIVATE,
                new ClassNode(String.class),
                new ClassNode(method.declaringClass.getClass()),
                new ConstantExpression("I am a field!")
            )
        )

        ExpressionStatement contextAssignmentExpression = new ExpressionStatement(
            new DeclarationExpression(
                new VariableExpression("myLocalVariable"),
                Token.newSymbol(Types.EQUAL, 0, 0),
                new MethodCallExpression(
                    new VariableExpression("myField"),
                    "getConfig",
                    new ArgumentListExpression()
                )
            )
        )

        BlockStatement newMethod = new BlockStatement([], new VariableScope())
        newMethod.statements.add(contextAssignmentExpression)
        method.code = newMethod
    }
}
