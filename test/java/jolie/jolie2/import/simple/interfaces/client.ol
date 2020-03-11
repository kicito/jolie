from "modules/twiceInterface.ol" import TwiceInterface

include "console.iol"

outputPort TwiceService {
    Location: "socket://localhost:8000"
    Protocol: sodep
    Interfaces: TwiceInterface
}

main
{
    twice@TwiceService( 5 )( response );
    println@Console( response )()
}