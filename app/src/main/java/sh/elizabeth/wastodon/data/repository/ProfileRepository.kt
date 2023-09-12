package sh.elizabeth.wastodon.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import sh.elizabeth.wastodon.data.database.entity.ProfileEmojiCrossRef
import sh.elizabeth.wastodon.data.datasource.EmojiLocalDataSource
import sh.elizabeth.wastodon.data.datasource.ProfileLocalDataSource
import sh.elizabeth.wastodon.data.datasource.ProfileRemoteDataSource
import sh.elizabeth.wastodon.data.model.toDomain
import sh.elizabeth.wastodon.model.Profile
import javax.inject.Inject

class ProfileRepository @Inject constructor(
	private val profileLocalDataSource: ProfileLocalDataSource,
	private val profileRemoteDataSource: ProfileRemoteDataSource,
	private val emojiLocalDataSource: EmojiLocalDataSource,
) {
	suspend fun insertOrReplace(vararg profiles: Profile) {
		profileLocalDataSource.insertOrReplace(*profiles)
	}

	suspend fun insertOrReplaceMain(vararg profiles: Profile) {
		profileLocalDataSource.insertOrReplaceMain(*profiles)
	}

	suspend fun insertOrReplaceEmojiCrossRef(vararg refs: ProfileEmojiCrossRef) {
		profileLocalDataSource.insertOrReplaceEmojiCrossRef(*refs)
	}

	suspend fun getByInstanceAndProfileId(instance: String, profileId: String): Profile? =
		profileLocalDataSource.getByInstanceAndProfileId(instance, profileId)

	fun getProfileFlow(profileId: String) = profileLocalDataSource.getProfileFlow(profileId)

	suspend fun fetchProfile(instance: String, profileId: String) {
		val profileRes =
			profileRemoteDataSource.fetchProfile(instance, profileId).toDomain(instance)
		val emojiRefs = profileRes.emojis.values.map {
			ProfileEmojiCrossRef(profileId = profileRes.id, fullEmojiId = it.fullEmojiId)
		}

		coroutineScope {
			val emojiRef =
				async { emojiLocalDataSource.insertOrReplace(*profileRes.emojis.values.toTypedArray()) }
			val profileRef = async { profileLocalDataSource.insertOrReplace(profileRes) }

			awaitAll(emojiRef, profileRef)

			profileLocalDataSource.insertOrReplaceEmojiCrossRef(*emojiRefs.toTypedArray())
		}
	}
}
