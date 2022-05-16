from ..test-unit import TestUnitInterface

from reflection import Reflection
from math import Math


service Test {
	
	embed Reflection as Reflection
	embed Math as Math

	inputPort TestUnitInput {
		location: "local"
		interfaces: TestUnitInterface
	}
	
	main{
		test()(){
			invoke@Reflection( {
				.operation = "abs",
				.outputPort = "Math",
				.data = -5
			} )( result );
			if ( result != 5 ) {
				throw( TestFailed, "invoke@Reflection result (" + result + ") does not match expected value (5)" )
			}
		}
	}

}