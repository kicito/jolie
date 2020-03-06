include "console.iol"


type sumReqType: void{
    x : int
    y : int
}

interface myIface {
    oneWay: notice(string)
    requestResponse: sum(sumReqType)(int)
}

inputPort sum {
    location: "socket://localhost:3000"
    protocol: http
    interfaces: myIface
}

main{
    notice(req)
    println@Console("[notice]receive value " + req)()
    sum(req)(res){
        println@Console("[sum] receive value x = " + req.x + ", y = " + req.y)()
        res = req.x + req.y
    }
}