package cn.edu.swufe.wsub;

public class SubItem {
    private String teacher;
    private String title;
    private int status;

    public SubItem(){
        super();
        teacher = "";
        title = "";
        status = 0;
    }

    public SubItem(String teacher, String title, int status){
        super();
        this.teacher = teacher;
        this.title = title;
        this.status = status;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
