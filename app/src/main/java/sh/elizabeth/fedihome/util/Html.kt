@file:Suppress("INVISIBLE_REFERENCE")

package sh.elizabeth.fedihome.util

import android.text.Editable
import android.text.Html.TagHandler
import android.text.Spanned
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.AnnotationSpan
import androidx.compose.ui.text.LinkInteractionListener
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.util.fastForEach
import androidx.core.text.HtmlCompat
import androidx.core.text.parseAsHtml
import org.xml.sax.XMLReader
import java.lang.reflect.Method

// I blame Google for the mess you're about to see.

private const val ContentHandlerReplacementTag = "ContentHandlerReplacementTag"

// https://stackoverflow.com/questions/78937193/how-to-access-a-kotlin-private-top-level-property-in-the-unnamed-main-file
val INLINE_CONTENT_TAG: String =
	Class.forName("androidx.compose.foundation.text.InlineTextContentKt")
		.getDeclaredField("INLINE_CONTENT_TAG").apply { isAccessible = true }
		.get(null /* because a top-level property is static */) as String

val origTagHandler: TagHandler =
	Class.forName("androidx.compose.ui.text.Html_androidKt")
		.getDeclaredField("TagHandler").apply { isAccessible = true }
		.get(null /* because a top-level property is static */) as TagHandler

val toAnnotatedString: Method =
	Class.forName("androidx.compose.ui.text.Html_androidKt")
		.getDeclaredMethod(
			"toAnnotatedString",
			Spanned::class.java,
			TextLinkStyles::class.java,
			LinkInteractionListener::class.java
		).apply { isAccessible = true }

val annotationSpanConstructor =
	Class.forName("androidx.compose.ui.text.AnnotationSpan")
		.getDeclaredConstructor(String::class.java, String::class.java)
		.apply { isAccessible = true }

val AccessibleAnnotationSpan =
	Class<AnnotationSpan>.forName("androidx.compose.ui.text.AnnotationSpan")

const val EMOJI_TAG = "emoji"

private val EmojiTagHandler = object : TagHandler {
	override fun handleTag(
		opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?
	) {
		if (!tag.equals(EMOJI_TAG) || output == null) return origTagHandler.handleTag(
			opening, tag, output, xmlReader
		)

		if (opening) {
			output.setSpan(
				annotationSpanConstructor.newInstance(
					EMOJI_TAG, ""
				), output.length, output.length, Spanned.SPAN_MARK_MARK
			)
		} else {
			output.getSpans(0, output.length, AccessibleAnnotationSpan)
				.filter { output.getSpanFlags(it) == Spanned.SPAN_MARK_MARK }.fastForEach {
					val start = output.getSpanStart(it)
					val end = output.length
					output.removeSpan(it)
					if (start != end) {
						output.setSpan(
							annotationSpanConstructor.newInstance(
								INLINE_CONTENT_TAG,
								output.subSequence(start, end).toString().trim(':')
							), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
						)
					}
				}
		}

	}
}

fun AnnotatedString.Companion.fromHtml(
	htmlString: String,
	linkStyles: TextLinkStyles? = null,
	linkInteractionListener: LinkInteractionListener? = null,
): AnnotatedString {
	// Check ContentHandlerReplacementTag kdoc for more details
	val stringToParse = "<$ContentHandlerReplacementTag />$htmlString"
	val spanned = stringToParse.parseAsHtml(
		HtmlCompat.FROM_HTML_MODE_COMPACT, null, EmojiTagHandler
	)

	return toAnnotatedString(
		null, spanned, linkStyles, linkInteractionListener
	) as AnnotatedString
}