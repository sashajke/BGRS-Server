package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class COURSESTAT implements Command<Database> {
    private Integer courseNum;
    public COURSESTAT(Integer courseNum){
        this.courseNum = courseNum;
    }
    @Override
    public Serializable execute(Database arg) {
        return arg.getCourseState(courseNum);
    }
}
