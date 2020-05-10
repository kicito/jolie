from .console import ConsoleService, ConsoleInterface, ConsoleInputInterface


service main(){

    //outputPort Console {
    //    interfaces: ConsoleInterface
    //}

	inputPort fromJavaService {
		interfaces: ConsoleInputInterface
		location: "local://asdasd"
	}

    embed ConsoleService("local://asdasd") in new Console

    main{
        println@Console("Hello")()
        registerForInput@Console()()
        in(recv)
        println@Console(recv)()
    }
}