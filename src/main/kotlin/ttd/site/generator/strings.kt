package ttd.site.generator

fun String.replaceAll(find: String, replace: String): String {

    var inString = this
    var start: Int
    while (true) {
        start = inString.indexOf(find)
        if (start != -1) {
            val before = inString.substring(0, start)
            val after = inString.substring(start + find.length)
            inString = before + replace + after
        } else {
            break
        }
    }
    return inString
}