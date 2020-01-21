import Birthday from "modules/Birthday.ol"
import Holiday from "modules/Holiday.ol"

main {
    with( bDay ){
        person_name = "Jhon";
        date.day = 20;
        date.month = 12;
        date.year = 1999
    }

    isTrue = bDay instanceof Birthday

    with( hDay ){
        .holiday_name = "My day";
        .date.day = 20;
        .date.month = 12;
        .date.year = 1999
    }

    isTrue2 = hDay instanceof Holiday
}