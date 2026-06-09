package org.acme;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import io.smallrye.mutiny.Multi;

@RegisterAiService
public interface Assistant {
    @SystemMessage("You are a helpful assistant that help the user with his requests.")
    @ToolBox(WeatherTools.class)
    // 
    // String chat(@UserMessage String userMessage);
    // This enables streaming, and throws NPE
    Multi<String> chat(@UserMessage String userMessage);
}
