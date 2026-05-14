package com.ai.sovereignai.data.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey


/**
 * Memory entry stored in database
 */

@Entity(
    tableName = "memories",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversation_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["message_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("conversation_id"),
        Index("message_id"),
        Index("created_at"),
        Index("persona_id")
    ]
)
data class MemoryEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "conversation_id")
    val conversationId: String,

    @ColumnInfo(name = "message_id")
    val messageId: String,

    @ColumnInfo(name = "fact")
    val fact: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "isarchived")
    val isArchived: Boolean = false,

    @ColumnInfo(name = "embedding")
    val embedding: String? = null, // Stored as comma-separated floats

    @ColumnInfo(name = "persona_id")
    val personaId: String? = null


)
/**
 * Extension functions for conversion
 */
fun MemoryEntity.toDomain(): MemoryEntity{
    return MemoryEntry(

    )
}