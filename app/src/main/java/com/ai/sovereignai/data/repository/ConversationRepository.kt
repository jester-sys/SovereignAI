package com.ai.sovereignai.data.repository

import javax.inject.Inject

class ConversationRepository @Inject constructor(
    private  val conversationDao: ConversationDao,
    private  messageDao: MessageDao
) {

}