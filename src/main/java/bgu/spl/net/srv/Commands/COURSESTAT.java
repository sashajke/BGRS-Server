package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
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
        // TODO : need to add ACK or ERR here and create the string the represents the course state
        return arg.getCourseState(courseNum);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }
}
