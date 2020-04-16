import twiceIface from "./twiceIface.ol"
import twiceService from "./server.ol"

decl service serviceTypeDemo {
    
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