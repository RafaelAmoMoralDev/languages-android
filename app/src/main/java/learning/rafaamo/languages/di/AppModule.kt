package learning.rafaamo.languages.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import learning.rafaamo.languages.BuildConfig
import learning.rafaamo.languages.common.App
import learning.rafaamo.languages.common.AppAuthentication
import learning.rafaamo.languages.data.datasource.local.AppDatabase
import learning.rafaamo.languages.data.datasource.local.ConversationDAO
import learning.rafaamo.languages.data.datasource.local.UserDAO
import learning.rafaamo.languages.data.datasource.remote.API
import learning.rafaamo.languages.data.datasource.remote.util.ApiHeader
import learning.rafaamo.languages.data.datasource.remote.util.CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

  @Singleton
  @Provides
  fun provideAPI(headers: ApiHeader): API {
    val okHttpClient = OkHttpClient()
      .newBuilder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS)
      .addInterceptor(headers)
      .build()

    val gsonBuilder = GsonBuilder().apply {
      setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    }

    return Retrofit.Builder()
      .baseUrl(BuildConfig.API_URL)
      .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
      .client(okHttpClient)
      .addCallAdapterFactory(CallAdapterFactory())
      .build()
      .create(API::class.java)
  }

  @Singleton
  @Provides
  fun provideAuthenticationManager(@ApplicationContext app: Context): AppAuthentication {
    return AppAuthentication(app as App)
  }

  @Singleton
  @Provides
  fun provideDb(app: Application): AppDatabase {
    return Room.databaseBuilder(app, AppDatabase::class.java, "languages.db")
      .fallbackToDestructiveMigration()
      .build()
  }

  @Singleton
  @Provides
  fun provideUserDao(db: AppDatabase): UserDAO {
    return db.userDAO()
  }

  @Singleton
  @Provides
  fun provideProductDao(db: AppDatabase): ConversationDAO {
    return db.conversationDAO()
  }

}