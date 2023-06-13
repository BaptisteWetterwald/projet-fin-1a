package fr.ensisa.ensiblog.models.posts;

import java.util.Objects;

public class PostWithFunction {

    private Post post;
    private String function;

    public PostWithFunction() {
    }

    public PostWithFunction(Post post, String function) {
        this.post = post;
        this.function = function;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    @Override
    public String toString() {
        return "PostWithFunction{" + "post=" + post + ", function=" + function + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PostWithFunction)) {
            return false;
        }
        final PostWithFunction other = (PostWithFunction) obj;
        if (!Objects.equals(this.post, other.post)) {
            return false;
        }
        return Objects.equals(this.function, other.function);
    }

}
