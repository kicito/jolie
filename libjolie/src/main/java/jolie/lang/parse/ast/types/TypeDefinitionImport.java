package jolie.lang.parse.ast.types;

import java.util.Iterator;
import jolie.lang.Constants;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.ast.OLSyntaxNode;
import jolie.lang.parse.context.ParsingContext;
import jolie.util.Pair;

public class TypeDefinitionImport extends TypeDefinition
{

    /**
    *
    */
    private static final long serialVersionUID = -4907457028564889966L;

    public TypeDefinitionImport(ParsingContext context, String id){
        super(context, id, Constants.RANGE_ONE_TO_ONE);
    }

    @Override
    protected boolean containsPath( Iterator< Pair< OLSyntaxNode, OLSyntaxNode > > it )
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void accept( OLVisitor visitor )
    {
		visitor.visit( this );
    }
}