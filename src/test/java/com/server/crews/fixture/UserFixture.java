package com.server.crews.fixture;

import com.server.crews.auth.domain.Administrator;

public class UserFixture {
    public static final String TEST_PASSWORD = "test password";
    public static final String TEST_EMAIL = "test@gmail.com";
    public static final String TEST_CLUB_NAME = "TEST_CLUB_NAME";

    public static Administrator TEST_ADMIN() {
        return new Administrator(TEST_CLUB_NAME, TEST_PASSWORD);
    }
}
