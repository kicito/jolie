
package jolie.runtime.typing;

import jolie.lang.Constants;

/**
 *
 * @author Fabrizio Montesi
 */
public class RefinementTypeException extends Exception
{
	public final static long serialVersionUID = Constants.serialVersionUID();

	public RefinementTypeException()
	{
		super();
	}
	
	public RefinementTypeException( String message )
	{
		super( message );
	}
}

