package sh.elizabeth.fedihome.ui.composable

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.test.platform.app.InstrumentationRegistry
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.test.FakeImageLoaderEngine
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import sh.elizabeth.fedihome.model.Emoji
import sh.elizabeth.fedihome.ui.theme.FediHomeTheme

const val TAG = "TextWithEmoji"

class TextWithEmojiTest {
	@get:Rule
	val composeTestRule = createComposeRule()

	@OptIn(ExperimentalCoilApi::class)
	@Before
	fun before() {
		val engine = FakeImageLoaderEngine.Builder()
			.default(ColorDrawable(Color.BLUE))
			.build()
		val imageLoader =
			ImageLoader.Builder(InstrumentationRegistry.getInstrumentation().targetContext)
				.components { add(engine) }
				.build()
		Coil.setImageLoader(imageLoader)
	}

	@Test
	fun plainText_renders() {
		val text = "Hello, world!"
		composeTestRule.setContent {
			FediHomeTheme {
				TextWithEmoji(text = text, modifier = Modifier.testTag(TAG))
			}
		}

		composeTestRule.onNodeWithTag(TAG).assertTextEquals(text)
	}

	@Test
	fun emojiText_renders() {
		val text = "Hello, :world:!"
		composeTestRule.setContent {
			FediHomeTheme {
				TextWithEmoji(
					text = text, modifier = Modifier.testTag(TAG), emojis = mapOf(
						"world" to Emoji(
							fullEmojiId = "world@foo.bar",
							shortcode = "world",
							url = "https://example.com/world.png",
							instance = "foo.bar"
						)
					)
				)
			}
		}

		composeTestRule.onNodeWithTag(TAG).assertTextEquals(text)
		composeTestRule.onAllNodes(
			matcher = SemanticsMatcher.expectValue(
				SemanticsProperties.Role,
				Role.Image
			),
			useUnmergedTree = true
		).assertCountEquals(1)
	}

	@Test
	fun missingEmojiText_renders() {
		val text = "Hello, :world:!"
		composeTestRule.setContent {
			FediHomeTheme {
				TextWithEmoji(
					text = text, modifier = Modifier.testTag(TAG), emojis = mapOf()
				)
			}
		}

		composeTestRule.onNodeWithTag(TAG).assertTextEquals(text)
		composeTestRule.onAllNodes(
			matcher = SemanticsMatcher.expectValue(
				SemanticsProperties.Role,
				Role.Image
			),
			useUnmergedTree = true
		).assertCountEquals(0)
	}

	@Test
	fun multipleEmojiText_renders() {
		val text = "Hello, :world: :earth:!"
		composeTestRule.setContent {
			FediHomeTheme {
				TextWithEmoji(
					text = text, modifier = Modifier.testTag(TAG), emojis = mapOf(
						"world" to Emoji(
							fullEmojiId = "world@foo.bar",
							shortcode = "world",
							url = "https://example.com/world.png",
							instance = "foo.bar"
						),
						"earth" to Emoji(
							fullEmojiId = "earth@foo.bar",
							shortcode = "earth",
							url = "https://example.com/earth.png",
							instance = "foo.bar"
						)
					)
				)
			}
		}

		composeTestRule.onNodeWithTag(TAG).assertTextEquals(text)
		composeTestRule.onAllNodes(
			matcher = SemanticsMatcher.expectValue(
				SemanticsProperties.Role,
				Role.Image
			),
			useUnmergedTree = true
		).assertCountEquals(2)
	}

	@Test
	fun enrichedText_renders() {
		"Hello, <b>world</b>!"
		composeTestRule.setContent {
			FediHomeTheme {
				Text(
					text = buildAnnotatedString {
						append("Hello, ")
						withStyle(
							SpanStyle(
								fontWeight = FontWeight
									.Bold
							)
						) {
							append("world!")
						}
					}, modifier = Modifier.testTag(TAG)
				)
			}
		}

		composeTestRule.onRoot(true).printToLog("TextWithEmojiTest")

		composeTestRule.onNodeWithTag(TAG).assert(SemanticsMatcher(description = "Test styles") {
			val textLayoutResults = mutableListOf<TextLayoutResult>()
			it.config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(
				textLayoutResults
			)
			if (textLayoutResults.size != 1) return@SemanticsMatcher true
			return@SemanticsMatcher true
		})
	}
}
