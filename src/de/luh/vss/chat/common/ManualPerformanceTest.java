package de.luh.vss.chat.common;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

public class ManualPerformanceTest {
    public static void main(String[] args) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        User admin = new User(new User.UserId(1), new InetSocketAddress("localhost", 8080), Arrays.asList("coding"));
        User member = new User(new User.UserId(2), new InetSocketAddress("localhost", 8081), Arrays.asList("gaming"));
        User member2 = new User(new User.UserId(3), new InetSocketAddress("localhost", 8081), Arrays.asList("gaming", "coding"));
        Group group = new Group("DevGroup", admin, List.of("coding"));
        PeerDiscoveryService discoveryService = new PeerDiscoveryService();

        measurePerformance("Group Creation", () -> {
            new Group("TestGroup", admin, List.of("AI"));
        }, threadMXBean);

        measurePerformance("Add Member", () -> {
            group.addMember(member);
        }, threadMXBean);

        measurePerformance("Add Member", () -> {
            group.addMember(member2);
        }, threadMXBean);

        measurePerformance("Send Group Message", () -> {
            Message message = new Message.ChatMessage(admin.getUserId(), "Hello Group!");
            group.handleGroupMessage(message, admin);
        }, threadMXBean);


        measurePerformance("Remove Member", () -> {
            group.removeMember(member);
        }, threadMXBean);

        measurePerformance("Peer Discovery (User)", () -> {
            discoveryService.peer("gaming", member);
        }, threadMXBean);

        measurePerformance("Peer Discovery (Group)", () -> {
            discoveryService.peerGoup("coding", group);
        }, threadMXBean);

    }

    private static void measurePerformance(String taskName, Runnable task, ThreadMXBean threadMXBean) {
        long startTime = System.nanoTime();
        long startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        
        task.run();
        
        long endTime = System.nanoTime();
        long endCpuTime = threadMXBean.getCurrentThreadCpuTime();
        
        System.out.println(taskName + " - Execution Time: " + (endTime - startTime) + " ns");
        System.out.println(taskName + " - CPU Time: " + (endCpuTime - startCpuTime) + " ns");
    }
}
