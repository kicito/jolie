import CustomType from "./modules/types.ol"
include "console.iol"

main{
    t = true instanceof CustomType
    println@Console(t)()
}