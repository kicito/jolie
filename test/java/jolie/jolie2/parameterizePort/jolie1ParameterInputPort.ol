
include "console.iol"

type sumReqType: void{
    x : int
    y : int
}

interface SumInterface{
    OneWay: notice(string)
    RequestResponse: sum(sumReqType)(int)
}

inputPort myPort{ 
    location : "socket://localhost:3000"
    protocol : sodep
    interfaces :SumInterface
}

main{
    notice(req);
    println@Console("[notice]receive value " + req)();
    sum(req)(res){
        println@Console("[sum] receive value x : " + req.x + ", y : " + req.y)()
        res = req.x + req.y
    }
}