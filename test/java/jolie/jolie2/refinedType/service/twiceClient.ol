from "./twiceService" import twiceIface, twiceService

decl service twiceClient{
    
    // parameterize port
    outputPort op ({
        location = "socket://localhost:8080"
        protocol = "http"
        interfaces << "twiceIface"
    })

    embed twiceService({
        location = "socket://localhost:8080"
        protocol = "http"
        interfaces << "twiceIface"
    })

    // static port declaration
    // outputPort op {
    //     location: "socket://localhost:3000"
    //     protocol: sodep
    //     interfaces: twiceIface
    // }

    // embed using service's default value
    // embed twiceService()

    main{
        twice@op(2)(res)
    }
}