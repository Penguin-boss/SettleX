package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        GroupEntity::class,
        MemberEntity::class,
        ExpenseEntity::class,
        ExpenseSplitEntity::class,
        SettlementEntity::class,
        ActivityEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SplitFlowDatabase : RoomDatabase() {
    abstract fun splitFlowDao(): SplitFlowDao

    companion object {
        @Volatile
        private var INSTANCE: SplitFlowDatabase? = null

        fun getDatabase(context: Context): SplitFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SplitFlowDatabase::class.java,
                    "splitflow_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
