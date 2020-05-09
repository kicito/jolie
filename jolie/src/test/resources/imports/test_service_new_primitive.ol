include "console.iol"
from .service_internal_local_input import someservice


service main (){

    inputPort IP {
        oneWay: notice( string )
        protocol: "sodep"
        location: "socket://localhost:3000"
    }

    embed someservice( {loc="socket://localhost:3000" proto="sodep"} ) in new OP

    main{
        println@Console( "hello!" )()
        twice@OP( 2 )( res )
        println@Console( "result is " + res )()
        println@Console( "waiting for embeder to call notice" )()
        notice( recv )
        println@Console( "receive " + recv )()
    }
}