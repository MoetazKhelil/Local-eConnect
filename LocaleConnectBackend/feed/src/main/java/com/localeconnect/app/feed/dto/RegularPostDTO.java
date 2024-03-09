package com.localeconnect.app.feed.dto;

import com.localeconnect.app.feed.model.Comment;
import com.localeconnect.app.feed.type.PostType;
import java.time.LocalDateTime;
import java.util.List;

public class RegularPostDTO extends PostDTO {

    public RegularPostDTO() {
        super.setPostType(PostType.REGULAR);
    }
    @Override
    public void setPostType(PostType postType) {
        super.setPostType(PostType.REGULAR);
    }
}
