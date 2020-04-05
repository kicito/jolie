
// twiceService.ol
interface twiceIface{
    RequestResponse: twice(int)(int)
}

type twiceParam: void{
    .location: string(
        default("socket://localhost:3000")
    )
    .protocol: string(default("sodep"))
    .interfaces: string(pattern("^twiceIface$")){} // receiving interface name has to be exact "twiceIface"
}

decl service twiceService(param : twiceParam){
    inputPort myPort(param)

    main{
        twice(req)(res){
            res = req * 2
        }
    }
}

decl service serviceTypeDemo{
    
    // parameterize port
    outputPort op ({
        location = "socket://localhost:3000"
        protocol = "sodep"
        interfaces << "twiceIface" 
    })

    embed twiceService({
        interfaces << "twiceIface" 
    })

    main{
        twice@op(2)(res)
    }
}