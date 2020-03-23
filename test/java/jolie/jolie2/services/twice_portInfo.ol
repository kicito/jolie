
interface SumInterface{
    RequestResponse: twice( int )( int )
}

type twiceServiceParams: void{
    .loc : string
    .prot : string
}

decl service twiceService( p: twiceServiceParams ){ 
    inputPort TwiceService ( {
        location = p.loc
        protocol = p.prot
    } ) {
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
        loc = "socket://localhost:3000"
        prot = "sodep"
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
