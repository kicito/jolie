include "console.iol"
from .service import someservice


service main (){

    inputPort IP {
        oneWay: notice(string)
        protocol: "sodep"
        location: "socket://localhost:3000"
    }

    embed someservice("socket://localhost:3000") in new OP

    main{
        println@Console("hello!")()
        twice@OP(2)(res)
        println@Console( "result is " + res)()
        println@Console( "waiting for embeder to call notice")()
        notice(recv)
        println@Console( "receive " + recv)()
    }
}