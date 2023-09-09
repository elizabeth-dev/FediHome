package sh.elizabeth.wastodon.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sh.elizabeth.wastodon.data.database.entity.FullProfileEntity
import sh.elizabeth.wastodon.data.database.entity.ProfileEntity
import sh.elizabeth.wastodon.data.database.entity.ProfileExtraEntity

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

	@Query("$GET_PROFILE_QUERY WHERE fullUsername = :fullUsername")
	suspend fun getByFullUsername(fullUsername: String): FullProfileEntity?

	// TODO: Maybe make this Flow<ProfileEntity> ?
	@Query("$GET_PROFILE_QUERY WHERE instance = :instance AND profileId = :profileId LIMIT 1")
	suspend fun getByInstanceAndProfileId(instance: String, profileId: String): FullProfileEntity?

	@Query("$GET_PROFILE_QUERY WHERE profileId = :profileId LIMIT 1")
	fun getProfileFlow(profileId: String): Flow<FullProfileEntity?>
}
