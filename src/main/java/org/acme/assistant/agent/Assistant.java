package org.acme.assistant.agent;

import org.acme.book.tools.BookTools;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import io.smallrye.mutiny.Multi;

@RegisterAiService
public interface Assistant {
    @SystemMessage("""
            You are a helpful assistant that help the user with his requests.
            Some tools require user approval through a confirmation dialog handled by the application,
            so NEVER ask the user to confirm or approve anything in the chat.
            If a tool result says the user rejected the action, do not retry it; just inform the user.
            When updating or deleting a book referenced by title or author, ALWAYS look the book up first
            (findByTitle or findByAuthor) and use the id returned by the lookup. Never invent ids.
            If the lookup finds nothing, tell the user the book does not exist instead of calling update or delete.""")
    @ToolBox({ BookTools.class })
    //
    // String chat(@UserMessage String userMessage);
    // This enables streaming, and throws NPE
    Multi<String> chat(@UserMessage String userMessage);
}
