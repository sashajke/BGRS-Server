package bgu.spl.net.srv.Messages;

import bgu.spl.net.srv.Database;

import java.util.List;

public class STUDENTSTAT implements Message<Database> {
    private final short opcode =8;
    private String userName;
    private String nameOfAdmin;
    public STUDENTSTAT(String userName){
        this.userName = userName;
    }
    @Override
    public Message execute(Database arg) {
        // TODO : need to create an ACK or ERR message and create the string represents the courses
        List<Integer> coursesList =  arg.getRegisteredCoursesByOrderOfInput(userName,nameOfAdmin);
        if(coursesList == null)
            return new ERR(opcode);
        String attachment = "Student: "+ userName + "\n";
        String courses = "[";
        for(int i=0;i<coursesList.size();i++){
            courses += coursesList.get(i) + ", ";
        }
        courses = courses.substring(0,courses.length()-1); // remove the last , char
        courses += "]";
        attachment += "Courses : "+ courses;
        return new ACK(opcode,attachment);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }

    @Override
    public void AddUserName(String userName) {
        nameOfAdmin = userName;
    }

    @Override
    public boolean needUserName() {
        return true;
    }
}
