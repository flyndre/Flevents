package de.flyndre.fleventsbackend.controllerServices;

import de.flyndre.fleventsbackend.Models.Post;
import de.flyndre.fleventsbackend.Models.PostComment;
import de.flyndre.fleventsbackend.services.EventService;
import de.flyndre.fleventsbackend.services.FleventsAccountService;
import de.flyndre.fleventsbackend.services.PostService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

/**
 * Author: Lukas Burkhardt
 * Version:
 * This Class is the service for the PostController class.
 * It provides methods regarding posts. The methods of the PostController are mapped on them.
 */

@Service
public class PostControllerService {
    private final EventService eventService;
    private final PostService postService;
    private final FleventsAccountService accountService;

    public PostControllerService(EventService eventService, PostService postService, FleventsAccountService accountService) {
        this.eventService = eventService;
        this.postService = postService;
        this.accountService = accountService;
    }

    /**
     * Returns a list with all posts of the specified event.
     * @param eventId the id of the event to get the posts from
     * @return List<Post> with the posts of the event
     */
    public List<Post> getPosts(String eventId){
        return postService.getPosts(eventService.getEventById(eventId));
    }


    /**
     * Returns a single, specified post from the specified event.
     * @param postId the id of the post
     * @return the specified post
     */
    public Post getPost(String postId){
        return postService.getPostById(postId);
    }


    /**
     * Creates a post in the specified id by the specified account.
     * @param eventId the id of the event to create the post in
     * @param accountId the id of the account which is the author of the post
     * @param post the post to be created
     * @return the created post
     */
    public Post createPost(String eventId, String accountId, Post post){
        post.setEvent(eventService.getEventById(eventId));
        post.setAuthor(accountService.getAccountById(accountId));
        post.setCreationDate(Timestamp.from(Instant.now()));
        post.setUuid(null);
        return postService.createPost(post);

    }

    /**
     * Overwrites the specified post with the given post.
     * @param postId the id of the post to be overwritten
     * @param post the post to overwrite the old one with
     * @return the updated post
     */
    public Post updatePost(String postId, Post post){
        return postService.updatePost(postId,post);
    }

    /**
     * Creates a comment under the specified post by the specified account in the specified event.
     * @param eventId the id of the event with the post to create the comment under
     * @param postId the id of the post in the event to create the comment under
     * @param accountId the id of the account which is the author of the comment
     * @param comment the comment to be created
     * @return the post with the new comment
     */
    public Post createComment(String postId,String eventId, String accountId, PostComment comment){
        comment.setPost(postService.getPostById(postId));
        comment.setAuthor(accountService.getAccountById(accountId));
        comment.setCreationDate(Timestamp.from(Instant.now()));
        comment.setUuid(null);
        return postService.createComment(postService.getPostById(postId),comment);
    }
}
