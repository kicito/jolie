package jolie.lang.parse.ast.types;

import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.context.ParsingContext;
import jolie.util.Range;

public class TypeInlineDefinitionRefined extends TypeInlineDefinition
{

    private static final long serialVersionUID = Constants.serialVersionUID();
    private String refinementCondition;


    public TypeInlineDefinitionRefined( ParsingContext context, String id, NativeType nativeType,
            Range cardinality, String refinementCondition )
    {
        super( context, id, nativeType, cardinality );
        this.refinementCondition = refinementCondition;
    }

    public String refinementCondition()
    {
        return refinementCondition;
    }

}
