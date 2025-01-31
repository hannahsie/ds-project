package de.luh.vss.chat.common;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import com.sun.management.OperatingSystemMXBean;

public class ManualPerformanceTest {
    public static void main(String[] args) throws Exception {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        User admin = new User(new User.UserId(1), new InetSocketAddress("localhost", 8080));
        User member1 = new User(new User.UserId(2), new InetSocketAddress("localhost", 8081));
        User member2 = new User(new User.UserId(3), new InetSocketAddress("localhost", 8082));
        Group group1 = new Group("DevGroup", admin);
        Group group2 = new Group("testGroup", admin);
        List<User> users = new ArrayList<>();
        List<Group> groups = new ArrayList<>();        
        PeerDiscoveryService discoveryService = new PeerDiscoveryService();

        measurePerformance("Group Creation", () -> {
            try {
                new Group("TestGroup", admin);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Add Member", () -> {
            group1.addMember(member1);
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Add Member", () -> {
            group1.addMember(member2);
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Send Group Message", () -> {
            group1.sendGroupMessage(admin, "Hello Group");
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Remove Member", () -> {
            group1.removeMember(member1);
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Add Interest", () -> {
            member2.addInterest("gaming");
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Add Interest", () -> {
            member2.addInterest("coding");
        }, threadMXBean, osBean, memoryBean);

        admin.addInterest("coding");
        member1.addInterest("gaming");
        users.add(admin);
        users.add(member1);
        users.add(member2);

        measurePerformance("Peer Discovery (User)", () -> {
            discoveryService.peer("gaming", users);
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Add Friend", () -> {
            member1.addFriend(member2);
        }, threadMXBean, osBean, memoryBean);

        measurePerformance("Add Interst", () -> {
            group1.addGroupInterest("coding");
        }, threadMXBean, osBean, memoryBean);

        group2.addGroupInterest("gaming");
        groups.add(group1);
        groups.add(group2);

        measurePerformance("Peer Discovery (Group)", () -> {
            discoveryService.peerGroup("coding", groups);
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
