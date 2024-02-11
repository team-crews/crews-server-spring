package com.server.crews.environ;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RequiredArgsConstructor
public abstract class IntegrationTest {
    private final DatabaseCleaner databaseCleaner;
    private final IntegrationTestEnviron integrationTestEnviron;

    @BeforeEach
    void setUp() {
        databaseCleaner.truncate();
    }
}
