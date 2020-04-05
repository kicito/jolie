package jolie.runtime.typing;

import jolie.lang.NativeType;
import jolie.runtime.Value;

public abstract class StringRefinementType extends RefinementType
{

    @Override
    public NativeType nativeType()
    {
        return NativeType.STRING;
    }

    public abstract void checkValue( Value v ) throws TypeCheckingException;

}
