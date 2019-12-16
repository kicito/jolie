import Date from "modules/Date.ol"
include "console.iol"

// type Date : void {
//     day: int
//     month: int
//     year: int
// }

// type holiday:Date

main {

    with(today){
        .day = 1
        .month = 2
        .year = 3
    }

    println@Console(today instanceof Date)()

}