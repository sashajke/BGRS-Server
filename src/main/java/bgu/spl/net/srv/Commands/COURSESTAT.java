package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.CourseStat;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class COURSESTAT implements Command<Database> {
    private final short opcode =7;
    private short courseNum;
    public COURSESTAT(short courseNum){
        this.courseNum = courseNum;
    }
    @Override
    public Serializable execute(Database arg) {
        CourseStat data = arg.getCourseState(courseNum);
        if(data == null)
            return new ERR(opcode);
        String attachment = "Course :(" +courseNum+") " + data.getCourseName() + "\n Seats Available: "+ data.getNumOfRegistered()+"/"+ data.getNumOfMaxStudents() + "\n Students Registered :";
        String registered = "[";
        for(int i=0;i<data.getRegisteredStudents().size();i++){
            registered += data.getRegisteredStudents().get(i) + ", ";
        }
        registered = registered.substring(0,registered.length()-1); // remove the last , char
        registered += "]";
        attachment += registered;
        return new ACK(opcode,attachment);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }
}
