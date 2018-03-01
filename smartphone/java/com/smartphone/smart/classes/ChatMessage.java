package com.cathedralsw.schoolparent.classes;

/**
 * Created by alexis on 11/10/17.
 */

public class ChatMessage {

    private String id;
    private User userFrom;
    private User userTo;
    private String message;
    private String timestamp;
    private String align;

    public ChatMessage(String id, User userFrom, User userTo, String message, String timestamp, String align) {
        this.id = id;
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.message = message;
        this.timestamp = timestamp;
        this.align = align;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public User getUserTo() {
        return userTo;
    }

    public void setUserTo(User userTo) {
        this.userTo = userTo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAlign() {
        return align;
    }

    public void setAlign(String align) {
        this.align = align;
    }
}
