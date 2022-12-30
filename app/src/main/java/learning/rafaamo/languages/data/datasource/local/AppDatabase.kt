package learning.rafaamo.languages.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import learning.rafaamo.languages.data.datasource.local.dto.ConversationDTO
import learning.rafaamo.languages.data.datasource.local.dto.UserDTO

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