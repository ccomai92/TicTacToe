package Server;

public interface MessageControl {
    public static int error = -1;
    public static int close = 0;

    public static int signInReq = 1;
    public static int signUpReq = 2;
    public static int createRoomReq = 3;
    public static int joinRoomReq = 4;
    public static int refreshReq = 5;
    public static int roomListReq = 6;
    public static int deregisterReq = 7;

    public static int signInSuccess = 101;
    public static int signInFail = 102;

    public static int signUpSuccess = 103;
    public static int signUpFail = 104;

    public static int createRoomSuccess = 105;
    public static int createRoomFail = 106;

    public static int joinSuccess = 107;
    public static int joinFail = 108;

    public static int deregisterSuceess = 109;
    public static int deregisterFail = 110;


}
