package com.am.mohamedraslan.hossamexams.JsonModel;

import java.util.ArrayList;

/**
 * Created by microprocess on 2018-10-16.
 */

public class Result_Pojo {
    String examID, uid,  examDate,  examName,  finalDegree, total,userName;
    ArrayList<WorngQestion> wrongQuestions;

    public Result_Pojo(String examID, String uid, String examDate, String examName, String finalDegree, String total, String userName, ArrayList<WorngQestion> wrongQuestions) {
        this.examID = examID;
        this.uid = uid;
        this.examDate = examDate;
        this.examName = examName;
        this.finalDegree = finalDegree;
        this.total = total;
        this.userName = userName;
        this.wrongQuestions = wrongQuestions;
    }

    public Result_Pojo() {
    }


    public String getUserName() {
        return userName;
    }

    public String getExamID() {
        return examID;
    }

    public String getUid() {
        return uid;
    }

    public String getExamDate() {
        return examDate;
    }

    public String getExamName() {
        return examName;
    }

    public String getFinalDegree() {
        return finalDegree;
    }

    public String getTotal() {
        return total;
    }

    public ArrayList<WorngQestion> getWrongQuestions() {
        return wrongQuestions;
    }

}
