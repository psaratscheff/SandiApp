package cl.saratscheff.sandiapp;

import java.util.Map;

/**
 * Created by psara on 02-11-2015.
 */
public class MessageClass {
    private String author;
    private String content;
    private long createdAt;
    public MessageClass() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public long getCreatedAt() { return createdAt; }
}
