from "modules/date.ol" import Date 
include "console.iol"

main{
    with(today){
        .day = 1
        .month = 2
        .year = 3
    }

    t = today instanceof Date;
    println@Console(t)()

}