package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.srv.Messages.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSMessageEncoderDecoder implements MessageEncoderDecoder<Message> {
    private ByteBuffer opCodeBuf = ByteBuffer.allocate(2);
    private boolean readOpCode = false;
    private MessageType type = null;
    private MessageStructure structure = null;
    private byte[] firstPart = new byte[1 << 10]; //bytes of part before first zero
    private int firstPartIndex = 0;
    private byte[] secondPart = new byte[1 << 10]; //bytes of part after first zero
    private int secondPartIndex = 0;
    @Override
    public Message decodeNextByte(byte nextByte) {
        if (!readOpCode) { //indicates that we are still reading the opcode
            opCodeBuf.put(nextByte);
            if (!opCodeBuf.hasRemaining()) { //we read 2 bytes and therefore can take the opcode
                opCodeBuf.flip();
                readOpCode = true;
                getType();
                opCodeBuf.clear();
            }
        }
        else {
            switch (structure)
            {

                // if the message consists of two bytes
                case TwoBytes:
                    return CreateTwoBytesMessage();

                // if the message consists of four bytes
                case FourBytes:
                    if(firstPartIndex >= firstPart.length) // if we reached the end we need to increase length
                        firstPart = Arrays.copyOf(firstPart,firstPartIndex*2);
                    firstPart[firstPartIndex++] = nextByte;
                    if(firstPartIndex == 2) // if the message is 4 bytes including opcode the actual message will be 2 bytes long
                        return CreateFourBytesMessage();

                // if the message consists of one zero
                case OneZero:
                    if(nextByte == '\0') // in this case if we reached zero we are at the end of the message
                        return CreateOneZeroMessage();
                    if(firstPartIndex >= firstPart.length) // if we reached the end we need to increase length
                        firstPart = Arrays.copyOf(firstPart,firstPartIndex*2);
                    firstPart[firstPartIndex++] = nextByte;

                // if the message consists of two zeros
                case TwoZeros:

                    if(nextByte == '\0')
                    {
                        if (secondPartIndex > 0) // we are in the second 0
                            return CreateTwoZeroMessage();
                        else // we reached the first zero so we need to start writing to the second part
                        {
                            if(secondPartIndex >= secondPart.length) // if we reached the end we need to increase length
                                secondPart = Arrays.copyOf(secondPart,secondPartIndex*2);
                            secondPart[secondPartIndex++] = nextByte;
                        }
                    }
                    else
                    {
                        if(secondPartIndex > 0) // if we started to write to the second part already
                        {
                            if(secondPartIndex >= secondPart.length) // if we reached the end we need to increase length
                                secondPart = Arrays.copyOf(secondPart,secondPartIndex*2);
                            secondPart[secondPartIndex++] = nextByte;
                        }
                        else // if we are in the first part
                        {
                            if(firstPartIndex >= firstPart.length) // if we reached the end we need to increase length
                                firstPart = Arrays.copyOf(firstPart,firstPartIndex*2);
                            firstPart[firstPartIndex++] = nextByte;
                        }
                    }
            }
        }
        return null;
    }

    @Override
    public byte[] encode(Message message) {
        short opcode = message.getOpCode();
        short messageOpCode = message.getOpCode();
        byte[] toReturn;
        ByteArrayOutputStream stream = new ByteArrayOutputStream(); // will use it to append all the byte buffers
        try {
            // add the opcode and the message opcode
            stream.write(shortToBytes(opcode)); // add opcode
            stream.write(shortToBytes(messageOpCode)); // add message opcode
            if (opcode == 12) // ACK message
            {
                String attachment = ((ACK) message).getAttachment(); // get the string of the message
                stream.write(attachment.getBytes()); // add the message
                byte[] temp = stream.toByteArray();
                toReturn = Arrays.copyOf(temp,temp.length+1); // add place to the \0 byte
                toReturn[toReturn.length-1] = '\0'; // add the zero byte to the end of the message
                return toReturn;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        toReturn = stream.toByteArray(); // if we got here we simply need to add the opcode and the message opcode because its ERR message
        return toReturn;
    }

    /**
     * a private function that sets the type of message and the structure of it according to the opcode
     */
    private void getType(){
        short opcode = bytesToShort(opCodeBuf.array());
        switch (opcode){
            case 1:
                structure = MessageStructure.TwoZeros;
                type = MessageType.AdminReg;
                break;
            case 2:
                structure = MessageStructure.TwoZeros;
                type = MessageType.StudentReg;
                break;
            case 3:
                structure = MessageStructure.TwoZeros;
                type = MessageType.Login;
                break;
            case 4:
                structure = MessageStructure.TwoBytes;
                type = MessageType.Logout;
                break;
            case 5:
                structure = MessageStructure.FourBytes;
                type = MessageType.CourseReg;
                break;
            case 6:
                structure = MessageStructure.FourBytes;
                type = MessageType.KdamCheck;
                break;
            case 7:
                structure = MessageStructure.FourBytes;
                type = MessageType.CourseStat;
                break;
            case 8:
                structure = MessageStructure.OneZero;
                type = MessageType.StudentStat;
                break;
            case 9:
                structure = MessageStructure.FourBytes;
                type = MessageType.IsRegistered;
                break;
            case 10:
                structure = MessageStructure.FourBytes;
                type = MessageType.Unregister;
                break;
            case 11:
                structure = MessageStructure.TwoBytes;
                type = MessageType.MyCourses;
                break;
        }
    }
    /**
     * private function that creates a message the consists of 2 bytes
     * @return
     */
    private Message CreateTwoBytesMessage(){
        if(type == MessageType.MyCourses)
            return new MYCOURSES();
        else
            return new LOGOUT();
    }

    /**
     * private function that creates a message the consists of 4 bytes
     * @return
     */
    private Message CreateFourBytesMessage(){
        short courseNum = bytesToShort(firstPart);
        switch (type){
            case CourseReg:
                return new COURSEREG(courseNum);
            case KdamCheck:
                return new KDAMCHECK(courseNum);
            case CourseStat:
                return new COURSESTAT(courseNum);
            case IsRegistered:
                return new ISREGISTERED(courseNum);
            default:
                return new UNREGISTER(courseNum);
        }
    }

    /**
     * private function that creates a message the consists of a series of bytes and ending with a zero byte
     * @return
     */
    private Message CreateOneZeroMessage(){
        String studentUserName = new String(firstPart, 0, firstPartIndex, StandardCharsets.UTF_8);
        return new STUDENTSTAT(studentUserName);
    }
    /**
     * private function that creates a message the consists of two parts with a zero byte in between and in the end
     * @return
     */
    private Message CreateTwoZeroMessage(){
        String userName = new String(firstPart, 0, firstPartIndex, StandardCharsets.UTF_8);
        String password = new String(secondPart, 0, secondPartIndex, StandardCharsets.UTF_8);
        switch (type){
            case AdminReg:
                return new ADMINREG(userName,password);
            case StudentReg:
                return new STUDENTREG(userName,password);
            default:
                return new LOGIN(userName,password);
        }
    }

    /**
     * Two private function that cast between short and byte array and the other way around
     * @param arr
     * @return
     */
    private short bytesToShort(byte[] arr)
    {
        byte[] opcode = opCodeBuf.array();
        short result = (short)((arr[0] & 0xff) << 8);
        result += (short)(arr[1] & 0xff);
        return result;
    }
    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}


/**
 * enum that describes the structure of the message
 */
enum MessageStructure{
    TwoBytes,
    FourBytes,
    OneZero,
    TwoZeros
}

/**
 * enum that describes the type of the message
 */
enum MessageType{
    AdminReg,
    StudentReg,
    Login,
    Logout,
    CourseReg,
    KdamCheck,
    CourseStat,
    StudentStat,
    IsRegistered,
    Unregister,
    MyCourses
}
