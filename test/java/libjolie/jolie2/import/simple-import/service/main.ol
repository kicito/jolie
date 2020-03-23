from "modules/Console.ol" import Console

decl service main (){
    embed Console ( { 
        fromClient << {
			location = "local://Console"
        }
        toClient << {
			location = "local://ConsoleIn"
        }
    })
    
    main {
        println@Console("Hello")()
    }
}
