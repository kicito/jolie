import Date from "modules/Date.ol"
// type Date : void{
//     .day: int
//     .month: int
//     .year: int
// }
include "console.iol"

main {
    today = void;
    today.day = 1;
    today.month = 1;
    today.year = 2112;

    println@Console(isTrue = today instanceof Date)()

}