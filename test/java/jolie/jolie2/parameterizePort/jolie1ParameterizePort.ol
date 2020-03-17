include "console.iol"

type sumReqType: void{
    x : int
    y : int
}

interface SumInterface{
    OneWay: notice(string)
    RequestResponse: sum(sumReqType)(int)
}

outputPort servicePort{ 
    location : "socket://localhost:3000"
    protocol : sodep
    interfaces :SumInterface
}

main{
    notice@servicePort("hello from parameterize port")
    sum@servicePort(void{x = 1 y = 2})(res)
    println@Console(res)()
}