package jolie.runtime.typing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jolie.runtime.Value;
import jolie.runtime.expression.Expression;

public class StringRefinementTypeFactory extends RefinementTypeFactory
{

    @Override
    public RefinementType create( String name, Expression[] arguments )
            throws RefinementTypeException
    {
        switch (name) {
            case "maxLength":
                return new MaximumLengthRefinementType( arguments );
            case "minLength":
                return new MinimumLengthRefinementType( arguments );
            case "pattern":
                return new PatternRefinementType( arguments );
            default:
                throw new RefinementTypeException( "refinement rule " + name + " is undefined" );
        }
    }


    class MaximumLengthRefinementType extends StringRefinementType
    {
        private Expression expr;

        public MaximumLengthRefinementType( Expression[] aExpression ) throws RefinementTypeException
        {
            if ( aExpression.length != 1 ) {
                throw new RefinementTypeException( "expected expression length to be 1" );
            }
            this.expr = aExpression[0];
        }

        @Override
        public void checkValue( Value v ) throws TypeCheckingException
        {
            if ( v.strValue().length() < this.expr.evaluate().intValue() ) {
                throw new TypeCheckingException(
                        "refinement rule violated, expected value " + v.strValue()
                                + " to have length at least " + this.expr.evaluate().intValue() );
            }
        }
    }


    class MinimumLengthRefinementType extends StringRefinementType
    {
        private Expression expr;

        public MinimumLengthRefinementType( Expression[] aExpression ) throws RefinementTypeException
        {
            if ( aExpression.length != 1 ) {
                throw new RefinementTypeException( "expected expression length to be 1" );
            }
            this.expr = aExpression[0];
        }

        @Override
        public void checkValue( Value v ) throws TypeCheckingException
        {
            if ( v.strValue().length() > this.expr.evaluate().intValue() ) {
                throw new TypeCheckingException(
                        "refinement rule violated, expected value " + v.strValue()
                                + " to have length at most " + this.expr.evaluate().intValue() );
            }
        }
    }

    class PatternRefinementType extends StringRefinementType
    {
        private Pattern p;

        public PatternRefinementType( Expression[] aExpression ) throws RefinementTypeException
        {
            if ( aExpression.length != 1 ) {
                throw new RefinementTypeException( "expected expression length to be 1" );
            }
            this.p = Pattern.compile(aExpression[0].evaluate().strValue());
        }

        @Override
        public void checkValue( Value v ) throws TypeCheckingException
        {
            Matcher m = p.matcher(v.strValue());
            if ( !m.matches() ) {
                throw new TypeCheckingException(
                        "refinement rule violated, expected value " + v.strValue()
                                + " to matched pattern" + this.p.pattern() );
            }
        }
    }
}
