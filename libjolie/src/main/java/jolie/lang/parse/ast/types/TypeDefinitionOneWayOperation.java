package jolie.lang.parse.ast.types;

import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.context.URIParsingContext;
import jolie.lang.parse.util.ProgramInspector;

public class TypeDefinitionOneWayOperation extends TypeInlineDefinition
{
	private static class LazyHolder
	{
		private LazyHolder()
		{
		}

		private final static TypeDefinitionOneWayOperation instance = new TypeDefinitionOneWayOperation();
    }

    private TypeDefinitionOneWayOperation()
    {
        super( URIParsingContext.DEFAULT, "OneWayOperation", NativeType.VOID,
                Constants.RANGE_ONE_TO_ONE );
        TypeDefinition reqType = new TypeInlineDefinition( URIParsingContext.DEFAULT, "reqType",
                NativeType.STRING, Constants.RANGE_ONE_TO_ONE );
        super.putSubType( reqType );
    }

	public static TypeDefinitionOneWayOperation getInstance()
	{
		return LazyHolder.instance;
	}

	@Override
	public String toString()
	{
		return "OneWayOperation";
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
