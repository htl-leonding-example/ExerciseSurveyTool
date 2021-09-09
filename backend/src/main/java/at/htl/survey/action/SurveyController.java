package at.htl.survey.action;


import at.htl.survey.model.Survey;

import javax.enterprise.context.ApplicationScoped;

// tag::start[]
@ApplicationScoped
public class SurveyController {

    Survey survey = new Survey("");

    public boolean vote(String answer) {
        if (survey == null)
            return false;

        if (! survey.getResult().containsKey(answer)) {
            survey.getResult().put(answer, 0L);
        }

        survey.getResult().put(answer, 1+survey.getResult().get(answer));
        return true;
    }
// end::start[]

    public void setSurvey(Survey s) {
        this.survey = s;
    }

    public Survey getSurvey() {
        return survey;
    }

}
