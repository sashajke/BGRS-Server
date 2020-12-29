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
        // TODO : need to create an ACK or ERR and create the string that represents the kdams
        List<Integer> kdams = arg.getKdamCourses(courseNum);
        return new ArrayList<>(kdams);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }
}
