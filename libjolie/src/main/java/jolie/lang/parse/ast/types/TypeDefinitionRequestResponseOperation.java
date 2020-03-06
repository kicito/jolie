package jolie.lang.parse.ast.types;

import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.context.URIParsingContext;
import jolie.lang.parse.util.ProgramInspector;

public class TypeDefinitionRequestResponseOperation extends TypeInlineDefinition
{
	private static class LazyHolder
	{
		private LazyHolder()
		{
		}

		private final static TypeDefinitionRequestResponseOperation instance = new TypeDefinitionRequestResponseOperation();
    }

    private TypeDefinitionRequestResponseOperation()
    {
        super( URIParsingContext.DEFAULT, "RequestResponseOperation", NativeType.VOID,
                Constants.RANGE_ONE_TO_ONE );
        TypeDefinition reqType = new TypeInlineDefinition( URIParsingContext.DEFAULT, "reqType",
                NativeType.STRING, Constants.RANGE_ONE_TO_ONE );
		TypeDefinition resType = new TypeInlineDefinition( URIParsingContext.DEFAULT, "resType",
				NativeType.STRING, Constants.RANGE_ONE_TO_ONE );
        super.putSubType( reqType );
        super.putSubType( resType );
    }

	public static TypeDefinitionRequestResponseOperation getInstance()
	{
		return LazyHolder.instance;
	}

	@Override
	public String toString()
	{
		return "RequestResponseOperation";
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
