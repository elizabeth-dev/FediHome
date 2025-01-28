package sh.elizabeth.fedihome.ui.composable

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import sh.elizabeth.fedihome.model.Emoji

val emojiRegex = Regex(":(\\w+):")

@Composable
fun TextWithEmoji(
	text: String,
	modifier: Modifier = Modifier,
	emojiSize: TextUnit = 24.sp,
	emojis: Map<String, Emoji> = emptyMap(),
	overflow: TextOverflow = TextOverflow.Clip,
	softWrap: Boolean = true,
	maxLines: Int = Int.MAX_VALUE,
	minLines: Int = 1,
	style: TextStyle = LocalTextStyle.current,
	color: Color = Color.Unspecified,
) {
	val _emojiSize = with(LocalDensity.current) {
		emojiSize.toDp()
	}

	Text(
		text = buildAnnotatedString {
			var carriageReturn = 0
			val emojiResults = emojiRegex.findAll(text).toList()
			emojiResults.forEach {
				append(text.substring(carriageReturn, it.range.first))
				carriageReturn = it.range.last + 1

				appendInlineContent(
					it.groupValues[1], it.value
				)
			}

			if (carriageReturn < text.length) append(text.substring(carriageReturn, text.length))
		},
		modifier = modifier,
		overflow = overflow,
		softWrap = softWrap,
		maxLines = maxLines,
		minLines = minLines,
		style = style,
		color = color,
		inlineContent = emojis.map { (key, emoji) ->
			key to InlineTextContent(
				placeholder = Placeholder(
					width = emojiSize,
					height = emojiSize,
					placeholderVerticalAlign = PlaceholderVerticalAlign.Center
				),
			) {
				AsyncImage(
					model = emoji.url,
					contentDescription = key,
					modifier = Modifier
						.size(_emojiSize)
				)
			}
		}.toMap(),
	)
}
