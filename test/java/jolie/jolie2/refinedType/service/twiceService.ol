

interface twiceIface{
    RequestResponse: twice(int)(int)
}

type twiceParam: void{
    .location: string(
        // transformer:
        default("socket://localhost:3000")
    )
    .protocol: string({ default = "sodep" })
    .interfaces: string({ pattern = "^twiceIface$" default = "foo" })
}

decl service twiceService(param : twiceParam){
    inputPort myPort(param)

    main{
        twice(req)(res){
            res = req * 2
        }
    }
}