package bgu.spl.net.srv;


import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.impl.rci.RemoteCommandInvocationProtocol;

public class BGRSserverMain {
    public static void main(String[] args) {
        Database database = Database.getInstance();
        database.initialize("Courses.txt");
        Integer port = Integer.parseInt(args[0]);
        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                port, //port
                () ->  new BGRSProtocol<>(database), //protocol factory
                () -> new BGRSMessageEncoderDecoder() //message encoder decoder factory
        ).serve();
    }
}
