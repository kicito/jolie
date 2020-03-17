
interface SumInterface{
    RequestResponse: twice( int )( int )
}

type twiceServiceParams: void{
    .loc : string
    .prot : string
}

decl service twiceService( twiceServiceParams p ){ 
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

decl service twice_portInfo(){ 
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
