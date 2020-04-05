package jolie.runtime.typing;

import jolie.runtime.expression.Expression;

public abstract class RefinementTypeFactory
{
    public abstract RefinementType create( String name, Expression[] arguments ) throws RefinementTypeException;
}
