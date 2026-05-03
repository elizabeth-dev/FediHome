package sh.elizabeth.fedihome.ui.compositionlocals

import androidx.compose.runtime.compositionLocalOf

val localOnVotePoll =
	compositionLocalOf<(postId: String, pollId: String?, choices: List<Int>) -> Unit>(
		defaultFactory = { { _, _, _ -> } })
val localOnRemoveFavorite = compositionLocalOf<(postId: String) -> Unit>(
	defaultFactory = { {} })
val localOnAddFavorite = compositionLocalOf<(postId: String) -> Unit>(
	defaultFactory = { {} })
val localOnRemoveReaction = compositionLocalOf<(postId: String, reaction: String) -> Unit>(
	defaultFactory = { { _, _ -> } })
val localOnAddReaction = compositionLocalOf<(postId: String, reaction: String) -> Unit>(
	defaultFactory = { { _, _ -> } })
val localOnAddBoost = compositionLocalOf<(postId: String) -> Unit>(
	defaultFactory = { {} })
val localOnRemoveBoost = compositionLocalOf<(postId: String) -> Unit>(
	defaultFactory = { {} })
