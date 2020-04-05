package jolie.runtime.typing;

import jolie.lang.NativeType;
import jolie.runtime.Value;
import jolie.runtime.expression.Expression;

public class NumberRefinementTypeFactory extends RefinementTypeFactory
{
    NativeType type;
    NumberRefinementTypeFactory(NativeType type){
        this.type = type;
    }

    @Override
    public RefinementType create( String name, Expression[] arguments )
            throws RefinementTypeException
    {
        switch (name) {
            case "maximum":
                return new MaximumRefinementType( type, arguments );
            case "minimum":
                return new MinimumRefinementType( type, arguments );
            default:
                throw new RefinementTypeException( "refinement rule " + name + " is undefined" );
        }
    }

    class MinimumRefinementType extends NumberRefinementType
    {
        private Expression expr;

        public MinimumRefinementType( NativeType type, Expression[] aExpression )
                throws RefinementTypeException
        {
            super( type );
            if ( aExpression.length != 1 ) {
                throw new RefinementTypeException( "expected expression length to be 1" );
            }
            this.expr = aExpression[0];
        }

        @Override
        public void checkValue( Value v ) throws TypeCheckingException
        {
            Number val = super.getValue(v);
            Number expr = super.getValue(this.expr.evaluate());
            if ( super.compare(val, expr) < 0 ) {
                throw new TypeCheckingException( "refinement rule violated, expected value "
                        + v.intValue() + " to be at least " + this.expr.evaluate().intValue() );
            }
        }
    }


    class MaximumRefinementType extends NumberRefinementType
    {
        private Expression expr;

        public MaximumRefinementType( NativeType type, Expression[] aExpression )
                throws RefinementTypeException
        {
            super( type );
            if ( aExpression.length != 1 ) {
                throw new RefinementTypeException( "expected expression length to be 1" );
            }
            this.expr = aExpression[0];
        }

        @Override
        public void checkValue( Value v ) throws TypeCheckingException
        {
            Number val = super.getValue(v);
            Number expr = super.getValue(this.expr.evaluate());
            if ( super.compare(val, expr) > 0 ) {
                throw new TypeCheckingException( "refinement rule violated, expected value "
                        + v.intValue() + " to be at most " + this.expr.evaluate().intValue() );
            }
        }
    }
}
