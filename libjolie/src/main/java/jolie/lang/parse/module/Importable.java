package jolie.lang.parse.module;

import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.util.ProgramInspector;

public interface Importable
{
    String name();
    OLSyntaxNode resolve( ParsingContext context, ProgramInspector pi, String localID );
}
