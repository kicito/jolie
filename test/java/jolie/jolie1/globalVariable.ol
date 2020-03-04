include "console.iol"

var a << {aaa=11 b=12}

main {
    println@Console(global.a.aaa)()
}