package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MYCOURSES implements Command<Database> {
    private final short opcode =11;
    private String userName;
    @Override
    public Serializable execute(Database arg) {
        // TODO : need to create an ACK or ERR message and create the string that represents the courses
        List<Integer> courses =  arg.getRegisteredCourses(userName);
        return new ArrayList<>(courses);
    }

    @Override
    public short getOpCode() {
        return opcode;
    }

    @Override
    public void AddUserName(String userName) {
        this.userName = userName;
    }
    @Override
    public boolean needUserName() {
        return true;
    }
}
