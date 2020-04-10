package jolie.lang.parse.ast.types;

import java.util.Map;

import jolie.lang.Constants;
import jolie.lang.NativeType;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.util.ProgramInspector;
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


	@Override
	public TypeDefinition resolve( ParsingContext ctx, ProgramInspector pi, String localID )
	{
		TypeInlineDefinitionRefined localType = new TypeInlineDefinitionRefined( ctx, localID,
				this.nativeType(), this.cardinality(), this.refinementConditions() );
		localType.setDocumentation(this.getDocumentation());
		if ( this.hasSubTypes() ){
			for (Map.Entry< String, TypeDefinition > entry : this.subTypes()) {
				TypeDefinition subType = (TypeDefinition) entry.getValue().resolve(ctx, pi, entry.getKey());
				localType.putSubType(subType);
			}
		}

		return localType;
	}

    
}
