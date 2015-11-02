package cl.saratscheff.sandiapp;

/**
 * Created by psara on 02-11-2015.
 */
public class MessageClass {
    private String author;
    private String content;
    public MessageClass() {
        // empty default constructor, necessary for Firebase to be able to deserialize blog posts
    }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
}
