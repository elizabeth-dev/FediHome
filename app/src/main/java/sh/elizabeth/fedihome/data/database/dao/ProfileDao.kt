package sh.elizabeth.fedihome.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import sh.elizabeth.fedihome.data.database.entity.EnrichedFullProfile
import sh.elizabeth.fedihome.data.database.entity.ProfileEmojiCrossRef
import sh.elizabeth.fedihome.data.database.entity.ProfileEntity
import sh.elizabeth.fedihome.data.database.entity.ProfileExtraEntity

const val GET_PROFILE_QUERY = """
	SELECT * FROM ProfileEntity
	LEFT JOIN ProfileExtraEntity ON ProfileExtraEntity.profileRef = ProfileEntity.profileId
"""

@Dao
interface ProfileDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE) // TODO: This increments the rowId on each insert
	suspend fun insertOrReplaceMain(vararg profiles: ProfileEntity): List<Long>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplaceExtra(vararg profiles: ProfileExtraEntity): List<Long>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplaceEmojiCrossRef(vararg refs: ProfileEmojiCrossRef): List<Long>

	// TODO: Maybe make this Flow<ProfileEntity> ?
	@Transaction
	@Query("$GET_PROFILE_QUERY WHERE profileId = :profileId LIMIT 1")
	suspend fun getById(profileId: String): EnrichedFullProfile?

	@Transaction
	@Query("$GET_PROFILE_QUERY WHERE profileId IN (:profileIds)")
	suspend fun getMultipleByIds(profileIds: List<String>): List<EnrichedFullProfile>

	@Transaction
	@Query("$GET_PROFILE_QUERY WHERE profileId IN (:profileIds)")
	fun getMultipleByIdsFlow(profileIds: List<String>): Flow<List<EnrichedFullProfile>>

	@Transaction
	@Query("$GET_PROFILE_QUERY WHERE profileId = :profileId LIMIT 1")
	fun getProfileFlow(profileId: String): Flow<EnrichedFullProfile?>
}
