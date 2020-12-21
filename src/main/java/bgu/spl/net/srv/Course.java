package bgu.spl.net.srv;

import java.util.List;

public class Course {
    private Integer courseNum;
    private String courseName;
    private List<Integer> kdamCoursesList;
    private Integer numOfMaxStudents;
    public Course(Integer courseNum,String courseName,List<Integer> kdamCoursesList,Integer numOfMaxStudents){
        this.setCourseNum(courseNum);
        this.setCourseName(courseName);
        this.setKdamCoursesList(kdamCoursesList);
        this.setNumOfMaxStudents(numOfMaxStudents);
    }

    public Integer getCourseNum() {
        return courseNum;
    }

    public void setCourseNum(Integer courseNum) {
        this.courseNum = courseNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<Integer> getKdamCoursesList() {
        return kdamCoursesList;
    }

    public void setKdamCoursesList(List<Integer> kdamCoursesList) {
        this.kdamCoursesList = kdamCoursesList;
    }

    public Integer getNumOfMaxStudents() {
        return numOfMaxStudents;
    }

    public void setNumOfMaxStudents(Integer numOfMaxStudents) {
        this.numOfMaxStudents = numOfMaxStudents;
    }

}
