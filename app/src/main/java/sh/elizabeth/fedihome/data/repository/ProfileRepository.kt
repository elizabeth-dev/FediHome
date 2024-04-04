package sh.elizabeth.fedihome.data.repository

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import sh.elizabeth.fedihome.ProfileEmojiCrossRef
import sh.elizabeth.fedihome.data.datasource.EmojiLocalDataSource
import sh.elizabeth.fedihome.data.datasource.InternalDataLocalDataSource
import sh.elizabeth.fedihome.data.datasource.ProfileLocalDataSource
import sh.elizabeth.fedihome.data.datasource.ProfileRemoteDataSource
import sh.elizabeth.fedihome.model.Profile
import sh.elizabeth.fedihome.util.SupportedInstances
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
			val instance = it.split('@')[1]
			Triple(
				instance,
				internalData.instances[instance]?.instanceType!!,
				internalData.accounts[it]?.accessToken!!
			)
		}

	fun insertOrReplace(vararg profiles: Profile) {
		profileLocalDataSource.insertOrReplace(*profiles)
	}

	fun insertOrReplaceEmojiCrossRef(vararg refs: ProfileEmojiCrossRef) {
		profileLocalDataSource.insertOrReplaceEmojiCrossRef(*refs)
	}

	fun getMultipleByIdsFlow(fullUsernames: List<String>): Flow<List<Profile>> =
		profileLocalDataSource.getMultipleById(fullUsernames)

	fun getProfileFlow(profileId: String) =
		profileLocalDataSource.getById(profileId)

	suspend fun fetchProfile(
		activeAccount: String,
		profileId: String,
	) {
		val (instance, instanceType, token) = getInstanceAndTypeAndToken(
			activeAccount
		)

		val profileRes = profileRemoteDataSource.fetchProfile(
			instance, instanceType, token, profileId.split('@').first()
		)
		val emojiRefs = profileRes.emojis.values.map {
			ProfileEmojiCrossRef(
				profileId = profileRes.id, emojiId = it.fullEmojiId
			)
		}

		coroutineScope {
			val emojiRef =
				async { emojiLocalDataSource.insertOrReplace(*profileRes.emojis.values.toTypedArray()) }
			val profileRef =
				async { profileLocalDataSource.insertOrReplace(profileRes) }

			awaitAll(emojiRef, profileRef)

			profileLocalDataSource.insertOrReplaceEmojiCrossRef(*emojiRefs.toTypedArray())
		}
	}
}
