package com.guenbon.siso.dto.reaction.response;

import com.guenbon.siso.support.constants.ReactionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactionDTO {
    private String targetId;
    private Reaction like;
    private Reaction dislike;

    @Getter
    @AllArgsConstructor
    public static class Reaction {
        private String reactionId;
        private ReactionStatus status;

        public static Reaction of(String reactionId, ReactionStatus status) {
            return new Reaction(reactionId, status);
        }

        public static Reaction none() {
            return new Reaction(null, ReactionStatus.NONE);
        }
    }

    public static ReactionDTO of(String targetId, Reaction like, Reaction dislike) {
        return new ReactionDTO(targetId, like, dislike);
    }
}
