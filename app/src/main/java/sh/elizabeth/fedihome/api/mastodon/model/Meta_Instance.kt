package sh.elizabeth.fedihome.api.mastodon.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InstanceResponse(
	val domain: String,
	val title: String,
	val version: String,
	val source_url: String,
	val description: String,
	val usage: Usage,
	val thumbnail: Thumbnail,
	val languages: List<String>,
	val configuration: Configuration,
	val registrations: Registrations,
	val contact: Contact,
	val rules: List<Rule>,
)

@Serializable
data class Usage(
	val users: UsageUsers,
)

@Serializable
data class UsageUsers(
	val active_month: Int,
)

@Serializable
data class Thumbnail(
	val url: String,
	val blurhash: String,
	val versions: ThumbnailVersions,
)

@Serializable
data class ThumbnailVersions(
	@SerialName("@1x") val x1: String,
	@SerialName("@2x") val x2: String,
)

@Serializable
data class Configuration(
	val urls: ConfigurationUrls,
	val accounts: ConfigurationAccounts,
	val statuses: ConfigurationStatuses,
	val media_attachments: ConfigurationMediaAttachments,
	val polls: ConfigurationPolls,
	val translation: ConfigurationTranslation,
)

@Serializable
data class ConfigurationUrls(
	val streaming: String,
	val status: String,
)

@Serializable
data class ConfigurationAccounts(
	val max_featured_tags: Int,
)

@Serializable
data class ConfigurationStatuses(
	val max_characters: Int,
	val max_media_attachments: Int,
	val characters_reserved_per_url: Int,
	val supported_mime_types: List<String>,
)

@Serializable
data class ConfigurationMediaAttachments(
	val supported_mime_types: List<String>,
	val image_size_limit: Int,
	val image_matrix_limit: Int,
	val video_size_limit: Int,
	val video_frame_rate_limit: Int,
	val video_matrix_limit: Int,
)

@Serializable
data class ConfigurationPolls(
	val max_options: Int,
	val max_characters_per_option: Int,
	val min_expiration: Int,
	val max_expiration: Int,
)

@Serializable
data class ConfigurationTranslation(
	val enabled: Boolean,
)

@Serializable
data class Registrations(
	val enabled: Boolean,
	val approval_required: Boolean,
	val message: String?,
	val url: String?,
)

@Serializable
data class Contact(
	val email: String,
	val account: ContactAccount,
)

@Serializable
data class ContactAccount(
	val id: String,
	val username: String,
)

@Serializable
data class Rule(
	val id: String,
	val text: String,
)
