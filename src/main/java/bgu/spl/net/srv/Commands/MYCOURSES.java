package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MYCOURSES implements Command<Database> {
    private String userName;
    public MYCOURSES(String userName){
        this.userName = userName;
    }
    @Override
    public Serializable execute(Database arg) {
        List<Integer> courses =  arg.getRegisteredCourses(userName);
        return new ArrayList<>(courses);
    }
}
