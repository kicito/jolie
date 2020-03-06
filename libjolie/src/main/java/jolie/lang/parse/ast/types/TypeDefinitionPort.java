package jolie.lang.parse.ast.types;

import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.context.URIParsingContext;
import jolie.lang.parse.util.ProgramInspector;

public class TypeDefinitionPort extends TypeInlineDefinition
{
	public static final String PORT_KEYWORD = "port";

    private static class LazyHolder
    {
        private LazyHolder()
        {
        }

        private final static TypeDefinitionPort instance = new TypeDefinitionPort();
    }

    private TypeDefinitionPort()
    {
        super( URIParsingContext.DEFAULT, TypeDefinitionPort.PORT_KEYWORD, NativeType.VOID, Constants.RANGE_ONE_TO_ONE );
        TypeDefinition protocol = new TypeInlineDefinition( URIParsingContext.DEFAULT, "protocol",
                NativeType.STRING, Constants.RANGE_ONE_TO_ONE );
        TypeDefinition location = new TypeInlineDefinition( URIParsingContext.DEFAULT, "location",
                NativeType.STRING, Constants.RANGE_ONE_TO_ONE );
        super.putSubType( protocol );
        super.putSubType( location );
        super.putSubType( TypeDefinitionInterface.getInstance() );
    }

    public static TypeDefinitionPort getInstance()
    {
        return LazyHolder.instance;
    }

    @Override
    public String toString()
    {
        return TypeDefinitionPort.PORT_KEYWORD;
    }

    @Override
    public boolean equals( Object obj )
    {
        return super.equals( obj );
    }

    @Override
    public TypeDefinition resolve( ParsingContext ctx, ProgramInspector pi, String localID )
    {
        return this;
    }
}
