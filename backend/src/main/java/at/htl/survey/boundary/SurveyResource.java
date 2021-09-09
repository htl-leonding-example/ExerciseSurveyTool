package at.htl.survey.boundary;

import at.htl.survey.action.SurveyController;
import at.htl.survey.model.Survey;
import at.htl.survey.undertowwebsockets.SurveySocketServer;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/survey")
public class SurveyResource {

    @Inject
    SurveyController surveyController;

    @Inject
    SurveySocketServer socketServer;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSurvey(Survey survey) {
        surveyController.setSurvey(survey);
        socketServer.broadcast(surveyController.getSurvey());

        return Response.ok().build();
    }

    @POST
    @Path("/vote/{answer}")
    public String vote(@PathParam("answer") String answer) {
        if (surveyController.vote(answer)) {
            socketServer.broadcast(surveyController.getSurvey());
            return "ok";
        }
        return "failed";
    }

    @GET
    public void refresh() {
        socketServer.broadcast(surveyController.getSurvey());
    }
}
