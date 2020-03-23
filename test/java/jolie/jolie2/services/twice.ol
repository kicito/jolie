

decl service twiceService( portLocation:string ){ 
    inputPort TwiceService (
        {
            location = portLocation
            // protocol = "sodep"
            interfaces << "SumInterface" {
                operations << "twice" {
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

    embed twiceService ( "local://TwService" )

    outputPort tw {
        Location: "local://TwService" 
        // Protocol: sodep
        RequestResponse: twice
    }
    
    main {
        twice@tw(5)(res)
    }
}
