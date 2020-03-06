package jolie.lang.parse.ast.types;

import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.context.URIParsingContext;
import jolie.lang.parse.util.ProgramInspector;
import jolie.util.Range;

public class TypeDefinitionInterface extends TypeInlineDefinition
{
	public static final String INTERFACE_KEYWORD = "interfaces";
    
    private static class LazyHolder
    {
        private LazyHolder()
        {
        }

        private final static TypeDefinitionInterface instance = new TypeDefinitionInterface();
    }

    private TypeDefinitionInterface()
    {
        super( URIParsingContext.DEFAULT, INTERFACE_KEYWORD, NativeType.STRING,
                Constants.RANGE_ONE_TO_ONE );
        TypeDefinition reqType = new TypeChoiceDefinition( URIParsingContext.DEFAULT, "operations",
                new Range( 1, Integer.MAX_VALUE ), TypeDefinitionOperation.oneWay(),
                TypeDefinitionOperation.RequestResponse() );
        super.putSubType( reqType );
    }

    public static TypeDefinitionInterface getInstance()
    {
        return LazyHolder.instance;
    }

    @Override
    public String toString()
    {
        return INTERFACE_KEYWORD;
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
