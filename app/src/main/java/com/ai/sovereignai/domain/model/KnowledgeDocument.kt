package com.ai.sovereignai.domain.model

data class KnowledgeDocument(
    val id: String,
    val  name: String,
    val content: String,
    val createdAt: Long,
    val updatedAt: Long,
    val sizeBytes: Int,
    val linkedPersonaIds: List<String> = emptyList()  // List of persona IDs linked to this document
){

    /**
     * Check whether the document is available for a specific persona
     */

    fun isAvailableForPersona(personaId: String): Boolean{
        // If the list is empty, the document is available to everyone (legacy/global)
        if(linkedPersonaIds.isEmpty()) return  true

        // If personaId is null (global settings), only unlinked documents are available
        if(personaId== null) return  linkedPersonaIds.isEmpty()

        return  linkedPersonaIds.contains(personaId)
    }
    /**
     * Check whether the document is available for any of the personas
     */
    fun isAvailableForAnyPersona(personaIds: List<String>): Boolean{
        if(linkedPersonaIds.isEmpty()) return  true
        return  personaIds.any{linkedPersonaIds.contains(it)}
    }
}