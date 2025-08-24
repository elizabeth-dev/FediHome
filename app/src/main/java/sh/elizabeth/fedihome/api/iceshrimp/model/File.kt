package sh.elizabeth.fedihome.api.iceshrimp.model

import kotlinx.serialization.Serializable
import sh.elizabeth.fedihome.model.Attachment
import sh.elizabeth.fedihome.model.AttachmentType
import sh.elizabeth.fedihome.util.InstantAsString

@Serializable
data class File(
	val id: String,
	val createdAt: InstantAsString,
	val name: String,
	val type: String,
	val md5: String,
	val size: Int,
	val isSensitive: Boolean,
	val blurhash: String?,
	val properties: FileProperties,
	val url: String?,
	val thumbnailUrl: String?,
	val comment: String?,
	val folderId: String?,
	val folder: FileFolder?,
	val userId: String?,
	val user: UserLite?,
)

fun File.toDomain() = Attachment(
	id = id,
	description = comment,
	type = when {
		type.startsWith("image") -> AttachmentType.IMAGE
		type.startsWith("video") -> AttachmentType.VIDEO
		type.startsWith("audio") -> AttachmentType.AUDIO
		else -> AttachmentType.UNKNOWN
	},
	url = url ?: "",
	blurhash = blurhash,
)

@Serializable
data class FileProperties(
	val width: Int?,
	val height: Int?,
	val orientation: Int?,
	val avgColor: String?,
)

@Serializable
data class FileFolder(
	val id: String,
	val createdAt: InstantAsString,
	val name: String,
	val foldersCount: Int,
	val filesCount: Int,
	val parentId: String?,
	val parent: FileFolder?,
)
