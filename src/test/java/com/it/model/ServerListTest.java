package com.it.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ServerListTest {
    @Test
    public void randomRunningServer() {
        // Given
        int count = 3;
        List<Server> servers = Lists.newArrayList();
        ServerList serverList = new ServerList();
        for (int i = 0; i < count; i++) {
            servers.add(new Server("host", i));
            serverList.addServer(servers.get(i));
            serverList.setStatus("host", i, true);
        }
        
        // When
        Set<Server> randomServers = Sets.newHashSet();
        for (int i = 0; i < count * 100; i++) {
            Server randomRunningServer = serverList.nextRunningServer();
            randomServers.add(randomRunningServer);
        }

        // Then
        assertThat(servers.size(), is(3));
    }
    
    @Test
    public void setStatusWhenNotExists() {
        // Given
        ServerList serverList = new ServerList();
        serverList.addServer(new Server("host", 1));
        
        // When
        boolean status = serverList.setStatus("host", 2, true);
        
        // Then
        assertThat(status, is(false));
        assertThat(serverList.getRunningServers().size(), is(0));
    }

    @Test
    public void setStatusToRunning() {
        // Given
        ServerList serverList = new ServerList();
        serverList.addServer(new Server("host", 1));
        
        // When
        boolean status = serverList.setStatus("host", 1, true);
        
        // Then
        assertThat(status, is(true));
        assertThat(serverList.getRunningServers().size(), is(1));
    }
    
    @Test
    public void setStatusToNotRunning() {
        // Given
        ServerList serverList = new ServerList();
        serverList.addServer(new Server("host", 1));
        serverList.setStatus("host", 1, true);
        
        // When
        boolean status = serverList.setStatus("host", 1, false);
        
        // Then
        assertThat(status, is(true));
        assertThat(serverList.getRunningServers().size(), is(0));
    }

    @Test
    public void findServer() {
        // Given
        ServerList serverList = new ServerList();
        serverList.addServer(new Server("host", 1));
        
        // When
        Server findServer = serverList.findServer("host", 1);
        Server findServer2 = serverList.findServer("host", 2);

        // Then
        assertThat(findServer, notNullValue());
        assertThat(findServer2, nullValue());
    }

    @Test
    public void contains() {
        // Given
        Server server = new Server("host", 1);
        ServerList serverList = new ServerList();
        serverList.addServer(server);
        
        // When
        boolean contains1 = serverList.contains(server);
        Server testServer = new Server("host", 2);
        boolean contains2 = serverList.contains(testServer);

        // Then
        assertThat(contains1, is(true));
        assertThat(contains2, is(false));
    }
    
    @Test
    public void diff() {
        // Given
        ServerList serverList1 = new ServerList();
        serverList1.addServer(new Server("host", 1));
        serverList1.addServer(new Server("host", 2));
        serverList1.addServer(new Server("host", 3));

        ServerList serverList2 = new ServerList();
        serverList2.addServer(new Server("host", 3));
        serverList2.addServer(new Server("host", 4));
        
        ServerList serverList3 = new ServerList();
        
        // When
        ServerList diff1 = serverList1.diff(serverList2);
        ServerList diff2 = serverList2.diff(serverList1);
        ServerList diff3 = serverList3.diff(serverList1);
        ServerList diff4 = serverList1.diff(serverList3);
        ServerList diff5 = serverList1.diff(null);
        
        // Then
        assertThat(diff1.getServers().size(), is(2));
        assertThat(diff2.getServers().size(), is(1));
        assertThat(diff3.getServers().size(), is(0));
        assertThat(diff4.getServers().size(), is(3));
        assertThat(diff5.getServers().size(), is(3));
    }
}