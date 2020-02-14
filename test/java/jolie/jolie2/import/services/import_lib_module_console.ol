import Console,IReceiver from "console.ol"

decl service import_lib_module_console(){

	inputPort ConsoleInput {
		location: "local"
		interfaces: IReceiver
	}

    embed Console ("Console", ConsoleInput)
    
    main {
        println@Console("Hello")()
    }
}
