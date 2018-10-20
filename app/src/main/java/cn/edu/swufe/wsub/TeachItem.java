package cn.edu.swufe.wsub;

public class TeachItem {
    private String course;
    private String t_name;

    public  TeachItem(){
        super();
        course = "";
        t_name = "";
    }

    public TeachItem(String course, String t_name){
        super();
        this.course = course;
        this.t_name = t_name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getT_name() {
        return t_name;
    }

    public void setT_name(String t_name) {
        this.t_name = t_name;
    }
}
