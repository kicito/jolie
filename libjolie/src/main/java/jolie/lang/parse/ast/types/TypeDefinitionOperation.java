package jolie.lang.parse.ast.types;

import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.context.URIParsingContext;
import jolie.lang.parse.util.ProgramInspector;

public class TypeDefinitionOperation
{
	public static final String ONEWAY_KEYWORD = "oneWay";
	public static final String REQUESTRESPONSE_KEYWORD = "requestResponse";
	public static final String REQUEST_TYPE_KEYWORD = "oneWay";
	public static final String RESPONSE_TYPE_KEYWORD = "requestResponse";
    
    private static final TypeDefinition reqType = new TypeInlineDefinition(
            URIParsingContext.DEFAULT, REQUEST_TYPE_KEYWORD, NativeType.STRING, Constants.RANGE_ONE_TO_ONE );

    private static final TypeDefinition resType = new TypeInlineDefinition(
            URIParsingContext.DEFAULT, RESPONSE_TYPE_KEYWORD, NativeType.STRING, Constants.RANGE_ONE_TO_ONE );

    public static TypeDefinition requestType()
    {
        return reqType;
    }


    public static TypeDefinition responseType()
    {
        return resType;
    }


    public static TypeDefinitionOneWayOperation oneWay()
    {
        return OneWayLazyHolder.instance;
    }

    public static TypeDefinitionRequestResponseOperation requestResponse()
    {
        return RequestResponseLazyHolder.instance;
    }
    
    private static class OneWayLazyHolder
    {
        private OneWayLazyHolder()
        {
        }

        private final static TypeDefinitionOneWayOperation instance =
                new TypeDefinitionOneWayOperation();
    }

    private static class RequestResponseLazyHolder
    {
        private RequestResponseLazyHolder()
        {
        }

        private final static TypeDefinitionRequestResponseOperation instance =
                new TypeDefinitionRequestResponseOperation();
    }


    static class TypeDefinitionOneWayOperation extends TypeInlineDefinition
    {

        private TypeDefinitionOneWayOperation()
        {
            super( URIParsingContext.DEFAULT, ONEWAY_KEYWORD, NativeType.VOID,
                    Constants.RANGE_ONE_TO_ONE );
            super.putSubType( reqType );
        }

        @Override
        public String toString()
        {
            return ONEWAY_KEYWORD;
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

    static class TypeDefinitionRequestResponseOperation extends TypeInlineDefinition
    {

        private TypeDefinitionRequestResponseOperation()
        {
            super( URIParsingContext.DEFAULT, REQUESTRESPONSE_KEYWORD, NativeType.VOID,
                    Constants.RANGE_ONE_TO_ONE );
            super.putSubType( reqType );
            super.putSubType( resType );
        }

        @Override
        public String toString()
        {
            return REQUESTRESPONSE_KEYWORD;
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

}
