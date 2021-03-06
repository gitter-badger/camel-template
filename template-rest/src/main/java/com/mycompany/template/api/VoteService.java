package com.mycompany.template.api;

import com.mycompany.template.beans.VoteMessage;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by IntelliJ IDEA.
 * User: azee
 * Date: 2/20/13
 * Time: 6:08 PM
 */
@Component
@Path("/vote")
public class VoteService {

    @Produce(uri = "direct:vote")
    private ProducerTemplate voteQueue;

    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Path("/{pollId}/{competitorId}")
    public Response pushVote(@PathParam("competitorId") String competitorId, @PathParam("pollId") String pollId) throws Exception {
        VoteMessage voteMessage = new VoteMessage();
        voteMessage.setCompetitorId(competitorId);
        voteMessage.setPollId(pollId);
        voteQueue.sendBody(voteMessage);
        return Response.ok().build();
    }

}
