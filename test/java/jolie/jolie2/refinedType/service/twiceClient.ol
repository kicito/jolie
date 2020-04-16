from "./twiceService.ol" import twiceIface, twiceService

// interface twiceIface{
//     RequestResponse: twice(int)(int)
// }

// type twiceParam: void{
//     .location: string(
//         // transformer:
//         default("socket://localhost:3000")
//     )
//     .protocol: string(default("sodep"))
//     .interfaces: string(pattern("^twiceIface$"), default("twiceIface"))
// }

// decl service twiceService(param : twiceParam){
//     inputPort myPort(param)

//     main{
//         twice(req)(res){
//             res = req * 2
//         }
//     }
// }

decl service twiceClient{
    
    // parameterize port
    // outputPort op ({
    //     location = "socket://localhost:8080"
    //     protocol = "http"
    //     interfaces << "twiceIface"
    // })

    // embed twiceService({
    //     location = "socket://localhost:8080"
    //     protocol = "http"
    //     interfaces << "twiceIface"
    // })

    // static port declaration
    outputPort op {
        location: "socket://localhost:3000"
        protocol: sodep
        interfaces: twiceIface
    }

    // embed using service's default value
    embed twiceService()

    main{
        twice@op(2)(res)
    }
}