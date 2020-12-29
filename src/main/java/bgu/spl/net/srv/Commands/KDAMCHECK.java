package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class KDAMCHECK implements Command<Database> {
    private final short opcode =6;
    private short courseNum;
    public KDAMCHECK(short courseNum)
    {
        this.courseNum = courseNum;
    }
    @Override
    public Serializable execute(Database arg) {
        List<Integer> kdams = arg.getKdamCourses(courseNum);
        if(kdams == null)
            return new ERR(opcode);
        String courses = "[";
        for(int i=0;i<kdams.size();i++){
            courses += kdams.get(i) + ", ";
        }
        courses = courses.substring(0,courses.length()-1); // remove the last , char
        courses += "]";
        return new ACK(opcode,courses);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }
}
