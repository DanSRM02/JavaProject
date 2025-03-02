package com.oxi.software.repository.projection;

import java.util.Date;

public interface ReviewListProjection {
    Long getId();
    String getTitle();
    String getDescription();
    Date getCreatedAt();
    int getRating();

    // Proyección anidada para el usuario que realizó la reseña
    UserSummary getUser();

    interface UserSummary {
        Long getId();
        String getUsername();
    }
}
