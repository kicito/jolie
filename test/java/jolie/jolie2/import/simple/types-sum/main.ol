from "./modules/types.ol" import CustomType 
include "console.iol"

main{
    t = true instanceof CustomType
    println@Console(t)()
}