package org.acme;

import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class AssistantResource {

    @Inject
    Assistant assistant;

    // @POST
    // public String chat(String request) {
    // return assistant.chat(request);
    // }

    @POST
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public Multi<String> chat(String request) {
        return assistant.chat(request);
    }

}
