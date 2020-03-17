
interface SumInterface{
    RequestResponse: twice( int )( int )
}

decl service twiceService( portInfo p ){ 
    
    inputPort TwiceService ( p ) {
        Interfaces: SumInterface
    }

    main
    {
        twice( number )( result ) {
            result = number * 2
        }
    }
}

decl service twice_static(){ 
    embed twiceService ( {
        location = "socket://localhost:3000"
        protocol = "sodep"
    } )

    outputPort tw {
        Location: "socket://localhost:3000" 
        Protocol: sodep
        RequestResponse: twice
    }
    
    main {
        twice@tw(5)(res)
    }
}
