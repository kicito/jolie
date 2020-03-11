from "modules/Console.ol" import Console

decl service main (){
    embed Console ("Console")
    
    main {
        println@Console("Hello")()
    }
}
