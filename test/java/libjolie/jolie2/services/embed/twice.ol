decl service twiceService( string portLocation ){ 
    inputPort TwiceService (
        {
            location = portLocation
            protocol = sodep
            interfaces << "SumInterface"{
                operations << "twice"{
                    reqType = "int"
                    resType = "int"
                }
            }
        }
    )

    main
    {
        twice( number )( result ) {
            result = number * 2
        }
    }
}


decl service twice(){ 

    embed twiceService ( "socket://localhost:3000" )

    outputPort tw {
        Location: "socket://localhost:3000" 
        Protocol: sodep
        RequestResponse: twice
    }
    
    main {
        twice@tw(5)(res)
    }
}
