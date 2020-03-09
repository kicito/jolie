include "console.iol"

init{
    a << { 
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
    }
}

main {
    println@Console(a instanceof portInfo)()
}