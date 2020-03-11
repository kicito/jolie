from "modules/date.ol" import Date
include "console.iol"
main {
    today = void;
    today.day = 1;
    today.month = 1;
    today.year = 2112;
    t = today instanceof Date
    println@Console(t)()
}