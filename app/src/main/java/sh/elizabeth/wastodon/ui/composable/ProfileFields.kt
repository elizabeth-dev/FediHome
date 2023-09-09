package sh.elizabeth.wastodon.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.elizabeth.wastodon.model.ProfileField
import sh.elizabeth.wastodon.ui.theme.WastodonTheme

@Composable
fun ProfileFields(fields: List<ProfileField>) {
	Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
		fields.forEach { field ->
			Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
				Text(
					text = field.name.uppercase(),
					style = MaterialTheme.typography.titleSmall,
					modifier = Modifier
						.weight(1f)
						.padding(end = 4.dp)
				)
				Text(
					text = field.value,
					style = MaterialTheme.typography.bodyLarge,
					maxLines = 1,
					modifier = Modifier.weight(2f),
					overflow = TextOverflow.Ellipsis
				)
			}
		}
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun ProfileFieldsPreview() {
	WastodonTheme {
		Surface(
			color = MaterialTheme.colorScheme.surface,
			contentColor = MaterialTheme.colorScheme.onSurface
		) {
			ProfileFields(
				fields = listOf(
					ProfileField("Birthday", "April 20"),
					ProfileField("Location", "The Moon"),
					ProfileField("Website", "https://example.com"),
					ProfileField("Foo", "Bar"),
				)

			)
		}
	}
}
