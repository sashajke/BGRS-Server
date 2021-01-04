package bgu.spl.net.srv.Messages;

import bgu.spl.net.srv.Database;

import java.util.List;

public class KDAMCHECK implements Message<Database> {
    private final short opcode =6;
    private short courseNum;
    private String userName;
    public KDAMCHECK(short courseNum)
    {
        this.courseNum = courseNum;
    }
    @Override
    public Message execute(Database arg) {
        List<Integer> kdams = arg.getKdamCourses(courseNum,userName);
        if(kdams == null)
            return new ERR(opcode);
        String courses = "[";
        for(int i=0;i<kdams.size();i++){
            courses += kdams.get(i) + ",";
        }
        if(kdams.size() > 0) //  if we even added ,
            courses = courses.substring(0,courses.length()-1); // remove the last , char
        courses += "]";
        return new ACK(opcode,courses);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }
    @Override
    public void AddUserName(String userName)
    {
        this.userName = userName;
    }
}
