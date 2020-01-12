import Date from "modules/date.ol"

main {
    today = void;
    today.day = 1;
    today.month = 1;
    today.year = 2112;
    t = today instanceof Date
}