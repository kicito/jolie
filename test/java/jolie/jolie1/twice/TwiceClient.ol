include "console.iol"

outputPort TwiceService {
    Location: "socket://localhost:8000"
    Protocol: sodep
    RequestResponse: twice
}

main
{
    twice@TwiceService( 5 )( response );
    println@Console( response )()
}