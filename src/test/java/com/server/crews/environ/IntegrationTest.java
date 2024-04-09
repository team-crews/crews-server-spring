package com.server.crews.environ;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class IntegrationTest {
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private IntegrationTestEnviron integrationTestEnviron;

    @BeforeEach
    void setUp() {
        databaseCleaner.clear();
    }
}
