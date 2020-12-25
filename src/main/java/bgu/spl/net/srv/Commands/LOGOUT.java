package bgu.spl.net.srv.Commands;

import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Database;

import java.io.Serializable;

public class LOGOUT implements Command<Database> {
    @Override
    public Serializable execute(Database arg) {
        return null;
    }
}
