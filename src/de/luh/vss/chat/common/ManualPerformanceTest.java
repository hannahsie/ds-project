package de.luh.vss.chat.common;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import com.sun.management.OperatingSystemMXBean;

public class ManualPerformanceTest {
    public static void main(String[] args) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        User admin = new User(new User.UserId(1), new InetSocketAddress("localhost", 8080), Arrays.asList("coding"), null);
        User member1 = new User(new User.UserId(2), new InetSocketAddress("localhost", 8081), Arrays.asList("gaming"), null);
        User member2 = new User(new User.UserId(3), new InetSocketAddress("localhost", 8082), Arrays.asList("music", "gaming"), null);
        Group group = new Group("DevGroup", admin, List.of("coding"));
        PeerDiscoveryService discoveryService = new PeerDiscoveryService();

        measurePerformance("Group Creation", () -> {
            new Group("TestGroup", admin, List.of("AI"));
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Add Member", () -> {
            group.addMember(member1);
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Add Member", () -> {
            group.addMember(member2);
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Send Group Message", () -> {
            Message message = new Message.ChatMessage(admin.getUserId(), "Hello Group!");
            group.handleGroupMessage(message, admin);
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Remove Member", () -> {
            group.removeMember(member1);
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Peer Discovery (User)", () -> {
            discoveryService.peer("gaming", member1);
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Add Friend", () -> {
            member2.addfriend(member1);
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Peer Discovery (Group)", () -> {
            discoveryService.peerGoup("coding", group);
        }, threadMXBean, osBean, memoryBean);
        
    }

    @SuppressWarnings("deprecation")
    private static void measurePerformance(String taskName, Runnable task, ThreadMXBean threadMXBean, OperatingSystemMXBean osBean, MemoryMXBean memoryBean) {
        long startTime = System.nanoTime();
        long startCpuTime = threadMXBean.getCurrentThreadCpuTime();
        double startLoad = osBean.getSystemCpuLoad() * 100;
        double startProcessLoad = osBean.getProcessCpuLoad() * 100;
        MemoryUsage beforeHeap = memoryBean.getHeapMemoryUsage();
        MemoryUsage beforeNonHeap = memoryBean.getNonHeapMemoryUsage();
        
        task.run();
        
        long endTime = System.nanoTime();
        long endCpuTime = threadMXBean.getCurrentThreadCpuTime();
        double endLoad = osBean.getSystemCpuLoad() * 100;
        double endProcessLoad = osBean.getProcessCpuLoad() * 100;
        MemoryUsage afterHeap = memoryBean.getHeapMemoryUsage();
        MemoryUsage afterNonHeap = memoryBean.getNonHeapMemoryUsage();
        
        System.out.println(taskName + " - Execution Time: " + (endTime - startTime) + " ns");
        System.out.println(taskName + " - CPU Time: " + (endCpuTime - startCpuTime) + " ns");
        System.out.println(taskName + " - System CPU Load: " + endLoad + "%");
        System.out.println(taskName + " - Process CPU Load: " + endProcessLoad + "%");
        System.out.println(taskName + " - Heap Memory Used: " + (afterHeap.getUsed() - beforeHeap.getUsed()) + " bytes");
        System.out.println(taskName + " - Non-Heap Memory Used: " + (afterNonHeap.getUsed() - beforeNonHeap.getUsed()) + " bytes");
    }
}
