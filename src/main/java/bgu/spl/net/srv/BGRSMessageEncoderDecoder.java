package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.rci.Command;
import bgu.spl.net.srv.Commands.*;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSMessageEncoderDecoder implements MessageEncoderDecoder<Command> {
    private ByteBuffer opCodeBuf = ByteBuffer.allocate(2);
    private boolean readOpCode = false;
    private MessageType type = null;
    private MessageStructure structure = null;
    private byte[] firstPart = new byte[1 << 10]; //start with 1k
    private int firstPartIndex = 0;
    private byte[] secondPart = new byte[1 << 10]; //start with 1k
    private int secondPartIndex = 0;
    @Override
    public Command decodeNextByte(byte nextByte) {
        if (!readOpCode) { //indicates that we are still reading the opcode
            opCodeBuf.put(nextByte);
            if (!opCodeBuf.hasRemaining()) { //we read 2 bytes and therefore can take the opcode
                opCodeBuf.flip();
                readOpCode = true;
                getType();
                opCodeBuf.clear();
            }
        } else {
            if(structure == MessageStructure.TwoBytes){
                return CreateTwoBytesMessage();
            }
            else if(structure == MessageStructure.FourBytes)
            {
                if(firstPartIndex >= firstPart.length) // if we reached the end we need to increase length
                    firstPart = Arrays.copyOf(firstPart,firstPartIndex*2);
                firstPart[firstPartIndex++] = nextByte;
                if(firstPartIndex == 2) // if the message is 4 bytes including opcode the actual message will be 2 bytes long
                    return CreateFourBytesMessage();
            }
            else if(structure == MessageStructure.OneZero){
                if(nextByte == '\0') // in this case if we reached zero we are at the end of the message
                    return CreateOneZeroMessage();
                if(firstPartIndex >= firstPart.length) // if we reached the end we need to increase length
                    firstPart = Arrays.copyOf(firstPart,firstPartIndex*2);
                firstPart[firstPartIndex++] = nextByte;
            }
            else
            {
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
    public byte[] encode(Command message) {
        return new byte[0];
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
    private Command CreateTwoBytesMessage(){
        if(type == MessageType.MyCourses)
            return new MYCOURSES();
        else
            return new LOGOUT();
    }

    /**
     * private function that creates a message the consists of 4 bytes
     * @return
     */
    private Command CreateFourBytesMessage(){
        short courseNum = bytesToShort(firstPart);
        if(type == MessageType.CourseReg)
            return new COURSEREG(courseNum);
        else if(type == MessageType.KdamCheck)
            return new KDAMCHECK(courseNum);
        else if(type == MessageType.CourseStat)
            return new COURSESTAT(courseNum);
        else if(type == MessageType.IsRegistered)
            return new ISREGISTERED(courseNum);
        else
            return new UNREGISTER(courseNum);
    }

    /**
     * private function that creates a message the consists of a series of bytes and ending with a zero byte
     * @return
     */
    private Command CreateOneZeroMessage(){
        String studentUserName = new String(firstPart, 0, firstPartIndex, StandardCharsets.UTF_8);
        return new STUDENTSTAT(studentUserName);
    }
    /**
     * private function that creates a message the consists of two parts with a zero byte in between and in the end
     * @return
     */
    private Command CreateTwoZeroMessage(){
        String userName = new String(firstPart, 0, firstPartIndex, StandardCharsets.UTF_8);
        String password = new String(secondPart, 0, secondPartIndex, StandardCharsets.UTF_8);
        if(type == MessageType.AdminReg)
            return new ADMINREG(userName,password);
        else if(type == MessageType.StudentReg)
            return new STUDENTREG(userName,password);
        else
            return new LOGIN(userName,password);
    }
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
