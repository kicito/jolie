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

    protected RefinementType rType = null;
    protected boolean checked = false;

    public void extend( RefinementType rType )
    {
        if (this.rType != null && this.rType.equals(rType)){
            return;
        }
        this.rType = rType;
    }

    public void check( Value v ) throws TypeCheckingException
    {
        if ( this.rType != null && !checked ) {
            checked = true;
            this.rType.check( v );
        }
        checkValue( v );
    }

    public abstract void checkValue( Value v ) throws TypeCheckingException;

    public abstract NativeType nativeType();

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rType == null) ? 0 : rType.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RefinementType other = (RefinementType) obj;
        if (rType == null) {
            if (other.rType != null)
                return false;
        } else if (!rType.equals(other.rType))
            return false;
        return true;
    }

    
}
