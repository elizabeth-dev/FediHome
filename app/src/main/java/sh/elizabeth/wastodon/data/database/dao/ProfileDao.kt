package sh.elizabeth.wastodon.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import sh.elizabeth.wastodon.data.database.entity.ProfileEntity

@Dao
interface ProfileDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplace(vararg profiles: ProfileEntity): List<Long>

	@Query("SELECT * FROM ProfileEntity WHERE fullUsername = :fullUsername")
	suspend fun getByFullUsername(fullUsername: String): ProfileEntity?

	// TODO: Maybe make this Flow<ProfileEntity> ?
	@Query("SELECT * FROM ProfileEntity WHERE instance = :instance AND profileId = :profileId LIMIT 1")
	suspend fun getByInstanceAndProfileId(instance: String, profileId: String): ProfileEntity?

	@Query("SELECT * FROM ProfileEntity WHERE profileId = :profileId LIMIT 1")
	fun getProfileFlow(profileId: String): Flow<ProfileEntity>
}
