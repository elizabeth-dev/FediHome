package sh.elizabeth.wastodon.ui.composable

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.vanniktech.blurhash.BlurHash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.model.ProfileField
import sh.elizabeth.wastodon.ui.theme.WastodonTheme
import kotlin.math.roundToInt

const val HEADER_RATIO = 2.25f
private val AVATAR_SIZE = 72.dp

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileHeader(profile: Profile) {
	val resources = LocalContext.current.resources
	val avatarSizeInPx =
		resources.displayMetrics.densityDpi.div(160f).times(AVATAR_SIZE.value).roundToInt()

	var avatarBlurHash by remember { mutableStateOf<Bitmap?>(null) }
	LaunchedEffect(profile.avatarBlur) {
		CoroutineScope(Dispatchers.IO).launch {
			avatarBlurHash = BlurHash.decode(
				blurHash = profile.avatarBlur ?: "",
				height = avatarSizeInPx,
				width = avatarSizeInPx,
			)
		}
	}

	Surface(
		color = MaterialTheme.colorScheme.surface,
		contentColor = MaterialTheme.colorScheme.onSurface
	) {
		BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
			val _maxWidth = maxWidth
			val headerWidthInPx =
				resources.displayMetrics.densityDpi.div(160f).times(maxWidth.value).roundToInt()
			var headerBlurHash by remember { mutableStateOf<Bitmap?>(null) }

			LaunchedEffect(profile.headerBlur) { // TODO: Maybe move this to viewModel everywhere a blurhash is calculated
				CoroutineScope(Dispatchers.IO).launch {
					headerBlurHash = BlurHash.decode(
						blurHash = profile.headerBlur ?: "",
						height = headerWidthInPx.div(HEADER_RATIO).roundToInt(),
						width = headerWidthInPx,
					)
				}
			}

			// Avatar
			GlideImage(
				model = profile.avatarUrl,
				contentDescription = null,
				modifier = Modifier
					.size(AVATAR_SIZE)
					.zIndex(1f)
					.offset(
						y = maxWidth
							.div(HEADER_RATIO)
							.minus(AVATAR_SIZE.div(2)), x = 16.dp
					),
			) { _it ->
				let {
					if (avatarBlurHash != null) _it.placeholder(avatarBlurHash?.toDrawable(resources = resources))
					else _it
				}.transform(RoundedCorners(21))
			}

			Column(Modifier.fillMaxWidth()) {// Header
				if (profile.headerUrl != null) GlideImage(
					model = profile.headerUrl,
					contentDescription = "",
					modifier = Modifier
						.width(_maxWidth)
						.aspectRatio(HEADER_RATIO),
				) { _it ->
					let {
						if (headerBlurHash != null) _it.placeholder(
							headerBlurHash?.toDrawable(
								resources = resources
							)
						)
						else _it
					}.centerCrop()
				} else BoxWithConstraints( // FIXME: temporal placeholder
					modifier = Modifier
						.width(_maxWidth)
						.aspectRatio(HEADER_RATIO)
						.background(MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.25f))
				) {}

				// Profile data
				Column(
					modifier = Modifier
						.fillMaxWidth()
						.padding(
							top = AVATAR_SIZE
								.div(2)
								.plus(8.dp), start = 16.dp, end = 16.dp, bottom = 8.dp
						),
					verticalArrangement = Arrangement.spacedBy(12.dp),
				) {
					Column {
						Text(
							text = profile.name ?: "",
							style = MaterialTheme.typography.headlineSmall
						)
						Text(
							"@${profile.fullUsername}",
							style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
							maxLines = 1,
							color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
						)
					}
					if (!profile.description.isNullOrBlank()) Text(
						profile.description,
						style = MaterialTheme.typography.bodyLarge,
					)

					if (profile.fields.isNotEmpty()) {
						Divider(thickness = 1.dp)
						ProfileFields(profile.fields)
					}

					Row(
						Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(16.dp)
					) {
						Row(verticalAlignment = Alignment.Bottom) {
							Text(
								text = profile.postCount?.toString() ?: "0",
								style = MaterialTheme.typography.titleMedium,
							)
							Text(text = " posts", style = MaterialTheme.typography.bodyLarge)
						}
						Row(verticalAlignment = Alignment.Bottom) {
							Text(
								text = profile.following?.toString() ?: "0",
								style = MaterialTheme.typography.titleMedium,
							)
							Text(text = " following", style = MaterialTheme.typography.bodyLarge)
						}
						Row(verticalAlignment = Alignment.Bottom) {
							Text(
								text = profile.followers?.toString() ?: "0",
								style = MaterialTheme.typography.titleMedium,
							)
							Text(text = " followers", style = MaterialTheme.typography.bodyLarge)
						}
					}
				}
			}

		}
	}

}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun ProfileHeaderPreview() {
	WastodonTheme {
		ProfileHeader(
			profile = Profile(
				id = "foo",
				username = "elizabeth",
				name = "Elizabeth",
				avatarUrl = null,
				avatarBlur = null,
				instance = "blahaj.zone",
				fullUsername = "bar@blahaj.zone",
				headerUrl = null,
				headerBlur = null,
				following = null,
				followers = null,
				postCount = null,
				createdAt = null,
				fields = listOf(
					ProfileField("Birthday", "April 20"),
					ProfileField("Location", "The Moon"),
					ProfileField("Website", "https://example.com"),
					ProfileField("Foo", "Bar"),
				),
				description = "Lorem Ipsum Dolor Sit Amet",
			)

		)
	}
}
