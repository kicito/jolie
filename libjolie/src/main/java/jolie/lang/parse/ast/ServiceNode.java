package jolie.lang.parse.ast;

import jolie.lang.Constants;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.ast.types.TypeDefinition;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.module.SymbolInfo.Privacy;

public class ServiceNode extends OLSyntaxNode implements SymbolNode
{
    private static final long serialVersionUID = Constants.serialVersionUID();
    private final String name;
    private Program program;
    private TypeDefinition parameterType;
    private String parameterPath;
    private Privacy privacy;

    public ServiceNode( ParsingContext context, String name )
    {
        super( context );
        this.name = name;
    }

    public ServiceNode( ParsingContext context, String name, Program p )
    {
        super( context );
        this.name = name;
        this.program = p;
    }

    public void setAcceptParameter( String paramPath, TypeDefinition paramType )
    {
        this.parameterType = paramType;
        this.parameterPath = paramPath;
    }

    public TypeDefinition parameterType()
    {
        return this.parameterType;
    }

    public String parameterPath()
    {
        return this.parameterPath;
    }

    public Program program()
    {
        return this.program;
    }

    public void setProgram( Program p )
    {
        this.program = p;
    }


    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override
    public Privacy privacy()
    {
        return this.privacy;
    }

    @Override
    public void setPrivacy( Privacy privacy )
    {
        this.privacy = privacy;
    }

    @Override
    public String name()
    {
        return this.name;
    }

    @Override
    public OLSyntaxNode node()
    {
        return this;
    }


}
