package model;

public enum AnswerCode {
    EXIT(600),
    MESSAGE(601),

    NEED_REGISTER_OR_LOGIN(101),
    YOU_REGISTER_OR_LOGIN_YET(102),
    CLIENT_ONLINE_YET(103),
    DONT_HAVE_REGISTER_CLIENT(104),
    AGENT_ONLINE_YET(105),
    DONT_HAVE_REGISTER_AGENT(106),
    NAME_ALREADY_USED(107),

    UNKNOWN_MISTAKE(900),
    UNKNOWN_COMMAND(901),
    UNKNOWN_TYPE_USER(902),
    INVALID_CHARACTERS(903),

    DONT_HAVE_CHAT(200),
    LEAVE_CHAT(201),


    NEW_AGENT(800),
    NEW_CLIENT(801),
    AGENT_LEAVE(802),
    CLIENT_LEAVE(803),
    AGENT_LEAVE_WAIT_NEW(804),
    NO_AGENT_WAIT(805),
    FIRST_AGENT_ANSWER_YOU(806),
    CAN_NOT_LEAVE_AGENT(807),

    DONT_HAVE_CLIENT(300);


    private final int Value;

    private AnswerCode(int value) {
        Value = value;
    }

    public static AnswerCode getEnumByInt(int code) {
        for (AnswerCode answerCode : AnswerCode.values())
            if (code == answerCode.Value) return answerCode;
        return null;
    }
}