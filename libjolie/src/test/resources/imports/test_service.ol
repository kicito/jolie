include "console.iol"
from .packages.service import someservice, twiceIface

service main (){

    outputPort OP {
        interfaces: twiceIface
    }

    embed someservice("test") in OP

    main{
        twice@OP(2)(res)
        println@Console(res)()
    }
}