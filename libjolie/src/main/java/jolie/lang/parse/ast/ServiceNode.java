package jolie.lang.parse.ast;

import java.util.Optional;
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
    private Optional< TypeDefinition > parameterType;
    private Optional< String > parameterPath;
    private Privacy privacy;
    private Constants.EmbeddedServiceType type;

    public ServiceNode( ParsingContext context, String name )
    {
        this( context, name, null, Constants.EmbeddedServiceType.JOLIE );
    }

    public ServiceNode( ParsingContext context, String name, Program p )
    {
        this( context, name, p, Constants.EmbeddedServiceType.JOLIE );
    }

    public ServiceNode( ParsingContext context, String name, Program p, Constants.EmbeddedServiceType type )
    {
        super( context );
        this.name = name;
        this.program = p;
        this.parameterType = Optional.empty();
        this.parameterPath = Optional.empty();
        this.type = type;
    }

    public void setAcceptParameter( String paramPath, TypeDefinition paramType )
    {
        this.parameterType = paramType == null ? Optional.empty() : Optional.of( paramType );
        this.parameterPath = paramPath == null ? Optional.empty() : Optional.of( paramPath );
    }

    public Optional< TypeDefinition > parameterType()
    {
        return this.parameterType;
    }

    public Optional< String > parameterPath()
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

    public Constants.EmbeddedServiceType type()
    {
        return type;
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
    public void setPrivate( boolean isPrivate )
    {
        this.privacy = isPrivate ? Privacy.PRIVATE : Privacy.PUBLIC;
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
