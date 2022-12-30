package learning.rafaamo.languages.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import learning.rafaamo.languages.data.datasource.local.dto.UserDTO

@Dao
interface UserDAO {

    @Query("SELECT * FROM user WHERE id = :id")
    fun get(id: Long): Flow<UserDTO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userDTOS: List<UserDTO>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userDTO: UserDTO)

    @Query("UPDATE user SET liked = :liked, likes = :likes WHERE id = :id")
    suspend fun like(id: Long, liked: Boolean, likes: Int)

}