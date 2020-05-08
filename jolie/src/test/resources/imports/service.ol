include "console.iol"

interface twiceIface{
    requestResponse: twice(int)(int)
}

service someservice (someparam : string)  {
    
    inputPort IP {
        interfaces : twiceIface
        location: "local"
    }

    outputPort ServiceOp {
        oneWay: notice(string)
        protocol: sodep
        location: someparam
    }

    main {
        println@Console("hello from someservice")()
        println@Console("receive " + someparam + " as parameter value")()
        twice(a)(a*2)
        notice@ServiceOp("hello from serviceNode")
    }
}