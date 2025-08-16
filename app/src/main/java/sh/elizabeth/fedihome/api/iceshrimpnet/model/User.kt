package sh.elizabeth.fedihome.api.iceshrimpnet.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
	val id: String,
	val username: String,
	val host: String? = null,
	val displayName: String? = null,
	val avatarUrl: String? = null,
	val avatarAlt: String? = null,
	val bannerUrl: String? = null,
	val bannerAlt: String? = null,
	val instanceName: String? = null,
	val instanceIconUrl: String? = null,
	val instanceColor: String? = null,
	val isBot: Boolean,
	val isCat: Boolean,
	val speakAsCat: Boolean,
	val emojis: List<Emoji> = emptyList(),
	val movedTo: String? = null
)