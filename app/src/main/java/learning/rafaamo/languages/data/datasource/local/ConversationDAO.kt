package learning.rafaamo.languages.data.datasource.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import learning.rafaamo.languages.data.datasource.local.dto.ConversationDTO
import learning.rafaamo.languages.data.datasource.local.dto.ConversationWithUser
import learning.rafaamo.languages.ui.view.content.conversation.bottom_sheet.Conversation

@Dao
interface ConversationDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conversations: List<ConversationDTO>)

    @Query("SELECT * FROM conversation WHERE id IN (:ids)")
    fun get(ids: List<Long>): Flow<List<ConversationWithUser>>

    @Query("SELECT * FROM conversation WHERE datetime BETWEEN :start and :end")
    fun getDateRangeConversations(start: Long, end: Long): Flow<List<ConversationWithUser>>

    @Query("UPDATE conversation SET location = :location, datetime = :datetime WHERE id = :id")
    fun update(id: Long, location: String, datetime: Long)

}