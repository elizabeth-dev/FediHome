package sh.elizabeth.wastodon.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import sh.elizabeth.wastodon.data.database.entity.ProfileEmojiCrossRef
import sh.elizabeth.wastodon.data.datasource.EmojiLocalDataSource
import sh.elizabeth.wastodon.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.wastodon.data.datasource.ProfileLocalDataSource
import sh.elizabeth.wastodon.data.datasource.ProfileRemoteDataSource
import sh.elizabeth.wastodon.model.Profile
import sh.elizabeth.wastodon.util.SupportedInstances
import javax.inject.Inject

class ProfileRepository @Inject constructor(
	private val profileLocalDataSource: ProfileLocalDataSource,
	private val emojiLocalDataSource: EmojiLocalDataSource,
	private val profileRemoteDataSource: ProfileRemoteDataSource,
	private val internalDataLocalDataSource: InternalDataLocalDataSource,
) {
	private suspend fun getInstanceAndTypeAndToken(activeAccount: String): Triple<String, SupportedInstances, String> =
		activeAccount.let {
			val internalData = internalDataLocalDataSource.internalData.first()
			val instance = it.split(':').first()
			Triple(instance, internalData.serverTypes[instance]!!, internalData.accessTokens[it]!!)
		}

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

	suspend fun fetchProfile(
		activeAccount: String,
		profileId: String,
	) {
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(activeAccount)

		val profileRes =
			profileRemoteDataSource.fetchProfile(
				instance,
				instanceType,
				token,
				profileId.split('@').first()
			)
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
