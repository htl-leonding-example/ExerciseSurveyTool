package at.htl.survey.undertowwebsockets;

import at.htl.survey.action.SurveyController;
import at.htl.survey.model.Survey;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashSet;
import java.util.Set;

// tag::start[]
@ServerEndpoint("/survey/{surveyid}")
@ApplicationScoped
public class SurveySocketServer {

    private static final Logger LOG = Logger.getLogger(SurveySocketServer.class);

    Set<Session> sessions = new HashSet<>();

    @Inject
    SurveyController surveyController;
// end::start[]

    // tag::onopen[]
    @OnOpen
    public void onOpen(Session session, @PathParam("surveyid") String surveyid) {
        LOG.info("onOpen...");
        sessions.add(session);
        send(session, surveyController.getSurvey());
    }

    // end::onopen[]

    // tag::onclose[]
    @OnClose
    public void onClose(Session session, @PathParam("surveyid") String surveyid) {
        sessions.remove(session);
    }

    // end::onclose[]


    @OnError
    public void onError(Session session, @PathParam("surveyid") String surveyid, Throwable throwable) {
        sessions.remove(session);
        LOG.error("onError", throwable);
        broadcast(surveyController.getSurvey());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("surveyid") String surveyid) {

    }

    @Produces(MediaType.APPLICATION_JSON)
    public void broadcast(Survey survey) {
        sessions.forEach(s -> {
            send(s, survey);
        });
    }

    public void send(Session session, Survey survey) {
        ObjectMapper objectMapper = new ObjectMapper();
        String surv;
        try {
            surv = objectMapper.writeValueAsString(survey);
        } catch (JsonProcessingException e) {
            LOG.error(e);
            return;
        }

        session.getAsyncRemote().sendObject(surv, result -> {
            if (result.getException() != null) {
                System.out.println("Unable to send message: " + result.getException());
            }
        });
    }
}
