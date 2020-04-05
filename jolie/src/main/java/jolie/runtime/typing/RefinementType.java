package jolie.runtime.typing;

import jolie.lang.NativeType;
import jolie.runtime.Value;

public abstract class RefinementType
{

    public static RefinementTypeFactory getFactory( NativeType type ) throws RefinementTypeException
    {
        switch (type) {
            case INT:
            case DOUBLE:
            case LONG:
                return new NumberRefinementTypeFactory( type );
            case STRING:
                return new StringRefinementTypeFactory();
            case ANY:
            case BOOL:
            case RAW:
            case VOID:
            default:
                throw new RefinementTypeException( "unable to create refinement of type " + type );
        }
    }

    protected RefinementType rType;

    public void extend( RefinementType rType )
    {
        this.rType = rType;
    }

    public void check( Value v ) throws TypeCheckingException
    {
        if ( this.rType != null ) {
            this.rType.check( v );
        }
        checkValue( v );
    }

    public abstract void checkValue( Value v ) throws TypeCheckingException;

    public abstract NativeType nativeType();

}
