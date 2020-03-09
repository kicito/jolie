package jolie.lang.parse.ast.types;

import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.context.URIParsingContext;
import jolie.util.Range;

public class TypeDefinitionOperation extends TypeInlineDefinition
{
	public static final String ONEWAY_KEYWORD = "oneWay";
	public static final String REQUESTRESPONSE_KEYWORD = "requestResponse";
	public static final String REQUEST_TYPE_KEYWORD = "reqType";
    public static final String RESPONSE_TYPE_KEYWORD = "resType";
    public static final String OPERATION_TYPE_KEYWORD = "operations";
    
    private static final TypeDefinition reqType = new TypeInlineDefinition(
            URIParsingContext.DEFAULT, REQUEST_TYPE_KEYWORD, NativeType.STRING, Constants.RANGE_ONE_TO_ONE );

    private static final TypeDefinition resType = new TypeInlineDefinition(
            URIParsingContext.DEFAULT, RESPONSE_TYPE_KEYWORD, NativeType.STRING, new Range(0,1) );
    

    public static TypeDefinition getInstance()
    {
        return LazyHolder.instance;
    }

    private static class LazyHolder
    {
        private LazyHolder()
        {
        }

        private final static TypeDefinitionOperation instance =
                new TypeDefinitionOperation();
    }


    private TypeDefinitionOperation()
    {
        super( URIParsingContext.DEFAULT, OPERATION_TYPE_KEYWORD, NativeType.STRING,
            new Range(1,Integer.MAX_VALUE) );
        super.putSubType( reqType );
        super.putSubType( resType );
    }

    @Override
    public String toString()
    {
        return OPERATION_TYPE_KEYWORD;
    }

    @Override
    public boolean equals( Object obj )
    {
        return super.equals( obj );
    }

}
