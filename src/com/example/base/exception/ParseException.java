package com.example.base.exception;

public class ParseException extends BaseException {
	
    private static final long serialVersionUID = 1L;

    private int exceptionCode;

    public ParseException() {
    }

    public ParseException(String detailMessage) {
        super(detailMessage);
    }

    public ParseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public ParseException(Throwable throwable) {
        super(throwable);
    }

    /**
     * @param exceptionCode The http response status code, 0 if the http request error and has no response.
     */
    public ParseException(int exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    /**
     * @param exceptionCode The http response status code, 0 if the http request error and has no response.
     * @param detailMessage
     */
    public ParseException(int exceptionCode, String detailMessage) {
        super(detailMessage);
        this.exceptionCode = exceptionCode;
    }

    /**
     * @param exceptionCode The http response status code, 0 if the http request error and has no response.
     * @param detailMessage
     * @param throwable
     */
    public ParseException(int exceptionCode, String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        this.exceptionCode = exceptionCode;
    }

    /**
     * @param exceptionCode The http response status code, 0 if the http request error and has no response.
     * @param throwable
     */
    public ParseException(int exceptionCode, Throwable throwable) {
        super(throwable);
        this.exceptionCode = exceptionCode;
    }

    /**
     * @return The http response status code, 0 if the http request error and has no response.
     */
    public int getExceptionCode() {
        return exceptionCode;
    }
}
