package sh.elizabeth.fedihome.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

class InteractionButtonIcons(
	val selectedIcon: ImageVector? = null,
	val selectedIconDescription: String? = null,
	val unSelectedIcon: ImageVector,
	val unSelectedIconDescription: String
)

@Composable
fun InteractionButton(
	modifier: Modifier = Modifier,
	selected: Boolean = false,
	icons: InteractionButtonIcons? = null,
	count: Long = 0,
	enabled: Boolean = true,
	onClick: () -> Unit,
	onClickLabel: String,
	content: @Composable (RowScope.() -> Unit)? = {}
) {
	Row(
		// Fav button
		modifier = modifier
			.clip(RoundedCornerShape(4.dp))
			.clickable(
				enabled = enabled, onClickLabel = onClickLabel, onClick = onClick
			)
			.background(
				if (selected) MaterialTheme.colorScheme.primaryContainer
				else MaterialTheme.colorScheme.surface
			)
			.padding(vertical = 4.dp, horizontal = 8.dp),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(6.dp),
	) {
		if (icons != null) {
			if (selected && icons.selectedIcon != null && icons.selectedIconDescription != null) Icon(
				icons.selectedIcon,
				contentDescription = icons.selectedIconDescription,
				tint = MaterialTheme.colorScheme.onPrimaryContainer
			) else Icon(
				icons.unSelectedIcon,
				contentDescription = icons.unSelectedIconDescription,
				tint = MaterialTheme.colorScheme.onSurface,
			)
		}

		if (count > 0) Text(
			color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer
			else MaterialTheme.colorScheme.onSurface,
			text = count.toString(),
		)
		if (content != null) content()
	}
}