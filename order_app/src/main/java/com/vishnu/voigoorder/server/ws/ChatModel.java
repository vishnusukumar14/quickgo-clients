package com.vishnu.voigoorder.server.ws;

public record ChatModel(String messageId, String content, String messageTime, boolean isSent) {
}
