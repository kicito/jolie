include "console.iol"

type sumReqType: void{
    x : int
    y : int
}

outputPort servicePort({ 
    location = "socket://localhost:3000"
    protocol = "sodep"
    interfaces << "SumInterface"{
        operations[0] << "notice" {
            reqType = "string"
        }
        operations[1] << "sum" {
            reqType = "sumReqType"
            resType = "int"
        }
    }
})

main{
    notice@servicePort("hello from parameterize port")
    sum@servicePort(void{x = 1 y = 2})(res)
    println@Console(res)()
}