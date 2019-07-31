package khanh.aloha.caudodialy;

public class Question {
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String answerNr;
    private String chu_thich;
    private String isImageQuestion;

    public Question() {

    }

    public Question(String question, String option1, String option2, String option3, String answerNr, String chu_thich, String isImageQuestion) {
        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.answerNr = answerNr;
        this.chu_thich = chu_thich;
        this.isImageQuestion = isImageQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getAnswerNr() {
        return answerNr;
    }

    public void setAnswerNr(String answerNr) {
        this.answerNr = answerNr;
    }

    public String getChu_thich() {
        return chu_thich;
    }

    public void setChu_thich(String chu_thich) {
        this.chu_thich = chu_thich;
    }

    public String getIsImageQuestion() {
        return isImageQuestion;
    }

    public void setIsImageQuestion(String isImageQuestion) {
        this.isImageQuestion = isImageQuestion;
    }
}
