
interface SumInterface{
    RequestResponse: twice( int )( int )
}

// type commConfig : void{
//     location: string
//     protocol: string
//     interfaces: interfaces
// }

decl service twiceService( p: portInfo ){ 
    
    inputPort TwiceService ( p )

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
        interfaces << "SumInterface"{
            operations << "twice"{
                reqType="int"
                resType="int"
            }
        }
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
