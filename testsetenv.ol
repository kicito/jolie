from runtime import Runtime
from console import Console
service main {
    embed Runtime as runtime
    embed Console as console
    
    main {
        setenv@runtime({ key= "OW_TEST" value="value"})()
        getenv@runtime("OW_TEST")(val)
        println@console("OW_TEST is " + val)()
    }
}