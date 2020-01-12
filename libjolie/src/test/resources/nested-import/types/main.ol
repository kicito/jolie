import Date from "modules/date.ol"

main{
    with(today){
        .day = 1
        .month = 2
        .year = 3
    }

    t = today instanceof Date
}