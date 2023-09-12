package sh.elizabeth.wastodon.ui.composable

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckBox
import androidx.compose.material.icons.rounded.CheckBoxOutlineBlank
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import sh.elizabeth.wastodon.model.Poll
import sh.elizabeth.wastodon.model.PollChoice
import sh.elizabeth.wastodon.ui.theme.WastodonTheme
import java.time.Instant
import kotlin.math.roundToInt

private fun calcButtonIcon(multiple: Boolean, isVoted: Boolean) = if (multiple) {
	if (isVoted) Pair(Icons.Rounded.CheckBox, "Voted")
	else Pair(Icons.Rounded.CheckBoxOutlineBlank, "Not voted")
} else {
	if (isVoted) Pair(Icons.Rounded.RadioButtonChecked, "Voted")
	else Pair(Icons.Rounded.RadioButtonUnchecked, "Not voted")
}

// FIXME: Add handler for loading state
@Composable
fun PollDisplay(modifier: Modifier = Modifier, poll: Poll, onVote: (choices: List<Int>) -> Unit) {
	val selectedChoices = remember { mutableStateListOf<Int>() }

	val totalVotes = poll.choices.fold(0) { acc, choice -> acc + choice.votes }
	val disabledPoll = poll.voted || poll.expiresAt?.isBefore(Instant.now()) == false

	Column(
		modifier.fillMaxWidth(),
	) {
		poll.choices.forEachIndexed { index, choice ->
			if (disabledPoll) ResultPollChoiceButton(
				choice, poll.multiple, totalVotes
			) else PollChoiceButton(
				choice, poll.multiple, selectedChoices.contains(index)
			) {
				if (poll.multiple) {
					if (selectedChoices.contains(index)) selectedChoices.remove(index)
					else selectedChoices.add(index)
				} else {
					selectedChoices.clear()
					selectedChoices.add(index)
				}
			}
		}

		Row(
			modifier = Modifier.padding(start = 8.dp),
			horizontalArrangement = Arrangement.spacedBy(8.dp)
		) {
			Text(
				text = "$totalVotes votes", style = MaterialTheme.typography.labelLarge.copy(
					color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
				)
			)

			if (poll.expiresAt != null) {
				Text(
					text = "•", style = MaterialTheme.typography.labelLarge.copy(
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
					)
				) // TODO: Maybe find a better solution for this?
				Text(
					text = if (poll.expiresAt.isBefore(Instant.now())) "Ended" else "Ends at ${poll.expiresAt}",
					style = MaterialTheme.typography.labelLarge.copy(
						color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
					)
				)
			}
		}

		if (!disabledPoll) TextButton(
			onClick = { onVote(selectedChoices) }, enabled = selectedChoices.size > 0
		) {
			Text(text = "Vote")
		}
	}
}

// TODO: Maybe unify these two composables?
@Composable
fun ResultPollChoiceButton(choice: PollChoice, multiple: Boolean, totalVotes: Int) {
	val (icon, description) = calcButtonIcon(multiple, choice.isVoted)

	Button(
		modifier = Modifier.fillMaxWidth(),
		onClick = {},
		enabled = false,
		shape = MaterialTheme.shapes.small,
		colors = ButtonDefaults.buttonColors(disabledContainerColor = MaterialTheme.colorScheme.primary.copy(
			alpha = 0.05f
		), disabledContentColor = MaterialTheme.colorScheme.onSurface.let {
			if (choice.isVoted) it else it.copy(alpha = 0.6f)
		}),
		contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
	) {
		Icon(
			icon,
			contentDescription = description,
			modifier = Modifier.padding(end = ButtonDefaults.IconSpacing)
		)
		Text(
			text = choice.text,
			overflow = TextOverflow.Ellipsis,
			modifier = Modifier.weight(1f),
		)
		Text(
			text = "${
				if (totalVotes == 0) 0 else choice.votes.toFloat()
					.div(totalVotes)
					.times(100)
					.roundToInt()
			}%",
		)
	}
}

@Composable
fun PollChoiceButton(choice: PollChoice, multiple: Boolean, isVoted: Boolean, onClick: () -> Unit) {
	val (icon, description) = calcButtonIcon(multiple, isVoted)

	Button(
		modifier = Modifier.fillMaxWidth(),
		onClick = onClick,
		shape = MaterialTheme.shapes.small,
		colors = ButtonDefaults.buttonColors(
			containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
			contentColor = MaterialTheme.colorScheme.onSurface
		),
		contentPadding = ButtonDefaults.TextButtonWithIconContentPadding
	) {
		Icon(
			icon,
			contentDescription = description,
			modifier = Modifier.padding(end = ButtonDefaults.IconSpacing)
		)
		Text(text = choice.text)
		Spacer(Modifier.weight(1f))
	}
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Preview(showBackground = true)
@Composable
fun PollDisplayPreview() {
	WastodonTheme {
		Surface(
			color = MaterialTheme.colorScheme.surface,
			contentColor = MaterialTheme.colorScheme.onSurface,
		) {
			PollDisplay(poll = Poll(
				voted = false, expiresAt = Instant.EPOCH, multiple = false, choices = listOf(
					PollChoice(
						text = "foo", votes = 0, isVoted = true
					), PollChoice(
						text = "bar", votes = 0, isVoted = false
					)
				)
			), onVote = {})
		}
	}
}
