include "console.iol"
import Birthday from "modules/Birthday.ol"
import Holiday from "modules/Holiday.ol"

main {
    bDay = void;
    bDay.person_name = "Jhon";
    bDay.date.day = 20;
    bDay.date.month = 12;
    bDay.date.year = 1999;

    isTrue = bDay instanceof Birthday
    println@Console( "bDay = " + isTrue )()

    hDay = void;
    hDay.holiday_name = "My day";
    hDay.date.day = 20;
    hDay.date.month = 12;
    hDay.date.year = 1999;

    isTrue2 = hDay instanceof Holiday
    println@Console( "hDay = " + isTrue2 )()
    
}