package com.augustl.wut

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotationNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.VariableScope
import org.codehaus.groovy.ast.expr.ArgumentListExpression
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
    private class WatWutObject {
        private class WutWatObject {
            public wutWat() {
                println "Calling method on returned object"
            }
        }

        public watWut() {
            println "Creating first inner object"
            return new WutWatObject()
        }
    }

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        if (!(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof MethodNode)) {
            throw new RuntimeException('Internal error: expected @MyAstTransformingAnnotation annotation to be used on a method.')
        }

        AnnotationNode annotation = (AnnotationNode)nodes[0]
        MethodNode method = (MethodNode)nodes[1]

        String watWutObjectFieldName = "watWutInternalTestingField"
        String contextVariableName = "watWutReturnValueLocalVariableName"
        String originalMethodName = "_watWutNameOfOriginalMethod_${method.name}"

        method.declaringClass.addMethod(
            new MethodNode(originalMethodName, Modifier.PRIVATE, method.returnType, method.parameters, method.exceptions, method.code)
        )

        method.declaringClass.addField(
            new FieldNode(
                watWutObjectFieldName,
                Modifier.PRIVATE,
                ClassHelper.make(WatWutObject),
                method.declaringClass,
                new ConstructorCallExpression(ClassHelper.make(WatWutObject), new ArgumentListExpression())
            )
        )

        ExpressionStatement contextAssignmentExpression = new ExpressionStatement(
            new DeclarationExpression(
                new VariableExpression(contextVariableName),
                Token.newSymbol(Types.EQUAL, 0, 0),
                new MethodCallExpression(new VariableExpression(watWutObjectFieldName, ClassHelper.make(WatWutObject)), "watWut", new ArgumentListExpression())
            )
        )

        ExpressionStatement callOriginalMethod = new ExpressionStatement(
            new MethodCallExpression(
                new VariableExpression("this"),
                originalMethodName,
                new ArgumentListExpression()
            )
        )

        ExpressionStatement contextStopExpression = new ExpressionStatement(
            new MethodCallExpression(
                new VariableExpression(contextVariableName, ClassHelper.make(WatWutObject.WutWatObject)), "wutWat", new ArgumentListExpression()
            )
        )

        BlockStatement newMethod = new BlockStatement([], new VariableScope())
        newMethod.statements.add(contextAssignmentExpression)
        newMethod.statements.add(new TryCatchStatement(callOriginalMethod, contextStopExpression))
        method.code = newMethod
    }
}
