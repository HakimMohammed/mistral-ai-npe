package org.acme;

import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class WeatherTools {
    @Tool("Get Weather Information")
    public String getWeather(String location) {
        return "The weather in " + location + " is sunny.";
    }
}
