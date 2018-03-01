package com.cathedralsw.schoolteacher.classes;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by alexis on 5/02/18.
 */

public class StudentScore extends Student {

    Float score;
    String comments;


    public StudentScore(JSONObject object) {
        super();

        try{
            JSONObject student = (JSONObject) object.getJSONObject("student");
            setFirstName(student.getString("first_name"));
            setLastName(student.getString("last_name"));
            setId(student.getInt("id"));
            if ( object.has("score")){
                JSONObject scoreJSON = (JSONObject) object.getJSONObject("score");
                if (scoreJSON.has("score") && !scoreJSON.getString("score").equals("null"))
                    score = Float.parseFloat(scoreJSON.getString("score"));
                if (scoreJSON.has("comments") && !scoreJSON.getString("comments").equals("null"))
                    comments = scoreJSON.getString("comments");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
