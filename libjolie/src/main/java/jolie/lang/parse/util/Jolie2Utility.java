package jolie.lang.parse.util;

import jolie.lang.parse.ast.DefinitionNode;
import jolie.lang.parse.ast.ImportStatement;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.ast.Program;
import jolie.lang.parse.ast.ServiceNode;
import jolie.lang.parse.ast.SymbolNode;

/**
 * A Utility class to handle backward compatibility of Jolie 1
 * 
 */
public class Jolie2Utility
{
    public static boolean isJolie1( Program p )
    {
        for (OLSyntaxNode node : p.children()) {
            if ( node instanceof DefinitionNode ) {
                if ( ((DefinitionNode) node).id().equals( "main" ) ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Program transform( Program p )
    {
        ProgramBuilder moduleProgramBuilder = new ProgramBuilder( p.context() );
        ProgramBuilder mainServiceProgramBuilder = new ProgramBuilder( p.context() );
        ServiceNode mainService = new ServiceNode( p.context(), "main" );

        // foreach node s.t. is a SymbolNode but init & main: add to module (file) scope
        // else, add to newly created main service
        for (OLSyntaxNode node : p.children()) {
            if ( node instanceof SymbolNode ) {
                if ( node instanceof DefinitionNode ) {
                    DefinitionNode defNode = (DefinitionNode) node;
                    if ( defNode.id().equals( "main" ) || defNode.id().equals( "init" ) ) {
                        mainServiceProgramBuilder.addChild( node );
                    } else {
                        moduleProgramBuilder.addChild( node );
                    }
                } else {
                    moduleProgramBuilder.addChild( node );
                }
            } else {
                if ( node instanceof ImportStatement ) {
                    moduleProgramBuilder.addChild( node );
                }else{
                    mainServiceProgramBuilder.addChild( node );
                }
            }
        }mainService.setProgram(mainServiceProgramBuilder.toProgram());moduleProgramBuilder.addChild(mainService);return moduleProgramBuilder.toProgram();
}}
