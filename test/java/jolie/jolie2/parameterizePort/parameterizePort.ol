

// var o << { 
//     location = 3000 
//     protocol = "http" 
//     interfaces << "SumInterface" {
//         ow << "sum" {
//             reqType = "int"
//         }
//         // rr << "sumRR"{
//         //     reqType = "int"
//         //     resType = "int"
//         // }
//     }
// }

include "console.iol"
// include "string_utils.iol"

type sumReqType: void{
    x : int
    y : int
}

outputPort servicePort({ 
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
    notice@servicePort("hello from parameterize port")
    sum@servicePort({x = 1 y = 2})(res)
    println@Console(res)()
}