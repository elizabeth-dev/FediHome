package sh.elizabeth.fedihome.util

private val emojiRegex = Regex("""\p{Extended_Pictographic}""")

fun String.containsEmoji(): Boolean = emojiRegex.containsMatchIn(this)