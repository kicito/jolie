
include "console.iol"
// include "string_utils.iol"

type sumReqType: void{
    x : int
    y : int
}

inputPort myPort({ 
    location = "socket://localhost:3000"
    protocol = "http"
    interfaces << "SumInterface"{
        operations[0] << "notice"{
            reqType = "string"
        }
        operations[1] << "sum"{
            reqType = "sumReqType"
            resType = "int"
        }
    }
})

main{
    notice(req)
    println@Console("[notice]receive value " + req)()
    sum(req)(res){
        println@Console("[sum] receive value x = " + req.x + ", y = " + req.y)()
        res = req.x + req.y
    }
}