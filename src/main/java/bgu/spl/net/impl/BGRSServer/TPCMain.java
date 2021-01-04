package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.BGRSMessageEncoderDecoder;
import bgu.spl.net.srv.BGRSProtocol;
import bgu.spl.net.srv.Database;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        Database database = Database.getInstance();
        database.initialize("Courses.txt");
        Integer port = Integer.parseInt(args[0]);
        Server.threadPerClient(
                port, //port
                () -> new BGRSProtocol<>(database), //protocol factory
                BGRSMessageEncoderDecoder::new //message encoder decoder factory
        ).serve();
    }
}
