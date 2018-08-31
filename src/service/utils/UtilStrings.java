package service.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class UtilStrings {



    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static final int STATUS_OK = 200;
    public static final int STATUS_METHOD_NOT_ALLOWED = 405;
    public static final int BAD_REQUEST = 400;
    public static final String NO_DATA_TRANSACTION = "Bad request. Create request like: sender=<UUID>&receiver=<UUID>&summ=<int>";
    public static final String NOT_ENOUGH_MONEY = "Sender has not enough money on his account";
    public static final String CREATE_NONAME = "No name of new account. Create request like: name=<String>&fund=int";
    public static final String CREATE_BAD_DATA = "Bad request. Create request like: name=<String>&fund=int";


    public static final int NO_RESPONSE_LENGTH = -1;

    public static final String METHOD_GET = "GET";

    public static final String BAD_REQUEST_TEXT = "Bad request";
}
