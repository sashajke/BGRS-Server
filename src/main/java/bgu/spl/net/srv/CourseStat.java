package bgu.spl.net.srv;

import java.util.Collections;
import java.util.List;

public class CourseStat {
    private Integer numOfRegistered;
    private Integer numOfMaxStudents;
    private String courseName;
    private List<String> registeredStudents;
    public CourseStat(Integer numOfMaxStudents,String courseName,List<String> registeredStudents){
        this.numOfRegistered = registeredStudents.size();
        this.numOfMaxStudents = numOfMaxStudents;
        this.courseName = courseName;
        this.registeredStudents = registeredStudents;
        Collections.sort(this.registeredStudents); // sort alphabetically
    }

    public Integer getNumOfRegistered() {
        return numOfRegistered;
    }

    public Integer getNumOfMaxStudents() {
        return numOfMaxStudents;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<String> getRegisteredStudents() {
        return registeredStudents;
    }
}
