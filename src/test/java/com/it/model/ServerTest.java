package com.it.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

public class ServerTest {
    @Test
    public void equals() {
        // Given
        Server server1 = new Server("host", 1001);
        server1.setRunning(true);
        Server server2 = new Server("host", 1001);
        server1.setRunning(false);

        // When
        boolean equals = server1.equals(server2);

        // Then
		assertThat(equals, is(true));
    }

    @Test
    public void notEquals() {
        // Given
        Server server1 = new Server("host", 1002);
        Server server2 = new Server("host", 1001);

        // When
        boolean equals = server1.equals(server2);

        // Then
		assertThat(equals, is(false));
    }
    
    @Test
    public void comparePort() {
        // Given
        Server server1 = new Server("host", 1001);
        Server server2 = new Server("host", 1002);

        // When
        int compare1 = server1.compareTo(server2);
        int compare2 = server2.compareTo(server1);

        // Then
		assertThat(compare1, lessThan(0));
		assertThat(compare2, greaterThan(0));
    }

    @Test
    public void compareHost() {
        // Given
        Server server1 = new Server("host1", 1001);
        Server server2 = new Server("host2", 1001);

        // When
        int compare1 = server1.compareTo(server2);
        int compare2 = server2.compareTo(server1);

        // Then
		assertThat(compare1, lessThan(0));
		assertThat(compare2, greaterThan(0));
    }
}
