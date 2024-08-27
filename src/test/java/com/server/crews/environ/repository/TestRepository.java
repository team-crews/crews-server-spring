package com.server.crews.environ.repository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestRepository {
    private final EntityManager em;

    public void save(Object... objects) {
        for (Object o : objects) {
            em.persist(o);
        }
    }
}
