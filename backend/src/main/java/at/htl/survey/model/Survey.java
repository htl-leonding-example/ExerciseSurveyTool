package at.htl.survey.model;

import java.util.HashMap;

// tag::survey[]
public class Survey {

    private String text;
    private HashMap<String, Long> result;

    public Survey () {}

    public Survey(String text) {
        this.text = text;
        result = new HashMap<>();
    }
// end::survey[]

    public HashMap<String, Long> getResult() {
        return result;
    }

    public void setResult(HashMap<String, Long> result) {
        this.result = result;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
