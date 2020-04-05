package jolie.lang.parse.ast.types;

import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.context.ParsingContext;
import jolie.util.Range;

public class TypeInlineDefinitionRefined extends TypeInlineDefinition
{

    private static final long serialVersionUID = Constants.serialVersionUID();
    private RefinementCondition[] refinementConditions;


    public TypeInlineDefinitionRefined( ParsingContext context, String id, NativeType nativeType,
            Range cardinality, RefinementCondition[] refinementConditions )
    {
        super( context, id, nativeType, cardinality );
        this.refinementConditions = refinementConditions;
    }

    public RefinementCondition[] refinementConditions()
    {
        return refinementConditions;
    }
    
}
