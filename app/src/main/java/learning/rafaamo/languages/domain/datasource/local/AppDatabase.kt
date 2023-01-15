package learning.rafaamo.languages.domain.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import learning.rafaamo.languages.domain.datasource.local.dto.ConversationDTO
import learning.rafaamo.languages.domain.datasource.local.dto.UserDTO

@Database(
  entities = [
    UserDTO::class,
    ConversationDTO::class
  ],
  version = 2
)
abstract class AppDatabase: RoomDatabase() {

  abstract fun userDAO(): UserDAO
  abstract fun conversationDAO(): ConversationDAO

}