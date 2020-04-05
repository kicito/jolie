package jolie.runtime.typing;

import java.math.BigDecimal;
import java.util.Comparator;
import jolie.lang.NativeType;
import jolie.runtime.Value;

public abstract class NumberRefinementType extends RefinementType
{

    private NativeType type;

    NumberRefinementType( NativeType type ) throws RefinementTypeException
    {
        switch (type) {
            case INT:
            case LONG:
            case DOUBLE:
                this.type = type;
                return;
            default:
                throw new RefinementTypeException(
                        "invalid type " + type.name() + " for NumberRefinementType" );
        }
    }


    public int compare( Number x, Number y )
    {

        switch (type) {
            case INT:
                return Integer.compare( x.intValue(), y.intValue() );
            case LONG:
                return Long.compare( x.longValue(), y.longValue() );
            case DOUBLE:
                return Double.compare( x.doubleValue(), y.doubleValue() );
            default:
                return 0;
        }
    }


    @Override
    public NativeType nativeType()
    {
        return type;
    }

    public Number getValue( Value v )
    {
        switch (type) {
            case INT:
                return new Integer( v.intValue() );
            case LONG:
                return new Long( v.longValue() );
            case DOUBLE:
                return new Double( v.doubleValue() );
            default:
                return null;
        }
    }


    public abstract void checkValue( Value v ) throws TypeCheckingException;

}
