package jolie.runtime.typing;

import java.util.Map;
import java.util.Map.Entry;
import jolie.lang.NativeType;
import jolie.runtime.Value;
import jolie.runtime.ValueVector;
import jolie.runtime.expression.Expression;
import jolie.util.Range;

public class TypeRefinement extends TypeImpl
{

    private RefinementType refinementType;
    private Expression defaultExpression;

    public TypeRefinement( NativeType nativeType, Range cardinality, boolean undefinedSubTypes,
            Map< String, Type > subTypes, RefinementType refinementType, Expression defaultExpression )
    {
        super( nativeType, cardinality, undefinedSubTypes, subTypes );
        this.refinementType = refinementType;
        this.defaultExpression = defaultExpression;
    }

	public void assignDefault( Value value )
	{
        if( !value.isDefined() ){
            value.assignValue(this.defaultExpression.evaluate());
        }
    }
    
    @Override
    protected void check( Value value, StringBuilder pathBuilder ) throws TypeCheckingException
    {
        super.check( value, pathBuilder );
        if (refinementType != null){
            refinementType.check(value);
        }
    }
}
