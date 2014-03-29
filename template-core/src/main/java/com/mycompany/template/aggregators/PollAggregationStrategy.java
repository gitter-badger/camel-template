package com.mycompany.template.aggregators;

import com.mycompany.template.beans.Competitor;
import com.mycompany.template.beans.Poll;
import com.mycompany.template.beans.StopPollMessage;
import com.mycompany.template.beans.Vote;
import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

/**
 * Created by azee on 3/29/14.
 */
public class PollAggregationStrategy implements AggregationStrategy {

    private boolean isPollFinished;

    @Override
    public Exchange aggregate(Exchange oldState, Exchange message) {

        //Will init state only when Poll arrives int the message
        if (oldState == null) {
            return initNewState(message);
        }

        updateVote(oldState, message);

        if (message.getIn().getBody() instanceof StopPollMessage){
            isPollFinished = true;
        }

        //oldState.getIn().setBody(oldState);
        return oldState;
    }

    /**
     * Will init state only by a Poll message
     * @param message
     * @return
     */
    private Exchange initNewState(Exchange message){
        Object data = message.getIn().getBody();
        if (data instanceof Poll){
            return message;
        }
        return null;
    }

    /**
     * Will update state only by Vote message
     * @param oldState
     * @param message
     * @return
     */
    private Exchange updateVote(Exchange oldState, Exchange message){
        Object data = message.getIn().getBody();
        if (data instanceof Vote){
            Poll updatedPoll = updateVote(oldState.getIn().getBody(Poll.class), (Vote) data);
            oldState.getIn().setBody(updatedPoll);
        }
        return oldState;
    }

    /**
     * Look for a competitor and update state
     * @param poll
     * @param vote
     * @return
     */
    private Poll updateVote(Poll poll, Vote vote){
        if (vote.getCompetitorId() == null){
            return poll;
        }

        for (Competitor competitor : poll.getCompetitors()){
            if (competitor.getId().equals(vote.getCompetitorId())){
                competitor.setVotes(competitor.getVotes() + 1);
            }
        }
        return poll;
    }

    public boolean isCompleted() {
        return isPollFinished;
    }
}
