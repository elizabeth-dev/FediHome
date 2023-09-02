package sh.elizabeth.wastodon.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import sh.elizabeth.wastodon.data.database.entity.ProfileEntity

@Dao
interface ProfileDao {
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertOrReplace(vararg profiles: ProfileEntity): List<Long>

	@Query("SELECT * FROM ProfileEntity WHERE fullUsername = :fullUsername")
	suspend fun getByFullUsername(fullUsername: String): ProfileEntity?

	// TODO: Maybe make this Flow<ProfileEntity> ?
	@Query("SELECT * FROM ProfileEntity WHERE instance = :instance AND accountId = :accountId LIMIT 1")
	suspend fun getByInstanceAndAccountId(instance: String, accountId: String): ProfileEntity?
}
