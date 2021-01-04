package bgu.spl.net.srv.Messages;

public interface Message<T> {
    Message execute(T arg);
    short  getOpCode();
    default void AddUserName(String userName){}
    default short getMessageOpcode(){return -1;} // for ACK or ERR only , to get the message Opcode, used it in the interface to avoid casting
}
