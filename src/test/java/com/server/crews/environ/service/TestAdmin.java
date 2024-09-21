package com.server.crews.environ.service;

import com.server.crews.auth.domain.Administrator;

public class TestAdmin {
    private final ServiceTestEnviron environ;
    private Administrator administrator;

    public TestAdmin(ServiceTestEnviron environ) {
        this.environ = environ;
    }

    public TestAdmin create(String clubName, String password) {
        String encodedPassword = environ.passwordEncoder().encode(password);
        Administrator administrator = new Administrator(clubName, encodedPassword);
        this.administrator = environ.administratorRepository().save(administrator);
        return this;
    }

    public Administrator administrator() {
        return administrator;
    }
}
