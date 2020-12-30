package model.net;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.function.Consumer;

public class UDPHelper {
    Thread listenThread;
    DatagramSocket ds;
    DatagramPacket ps;
    volatile boolean listening;

    public static void Broadcast(int port, byte[] message) {
        try (DatagramSocket ds = new DatagramSocket()) {
            ds.setBroadcast(true);
            InetAddress addr = InetAddress.getByName("255.255.255.255");
            DatagramPacket ps = new DatagramPacket(message, message.length, addr, port);
            ds.send(ps);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (DatagramSocket ds = new DatagramSocket()) {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    try {
                        DatagramPacket sendPacket = new DatagramPacket(message, message.length, broadcast, port);
                        ds.send(sendPacket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void listen(int port, Consumer<byte[]> process) {
        listenThread = new Thread(() -> {
            try {
                ds = new DatagramSocket(port);
                while (listening) {
                    byte[] buf = new byte[1024];
                    ps = new DatagramPacket(buf, 0, buf.length);
                    ds.receive(ps);
                    System.out.printf("UDPHelper: New Receive %d%n", ps.getData().length);
                    System.out.println(Arrays.toString(ps.getData()));
                    process.accept(ps.getData());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                ds.close();
            }
        });
        listening = true;
        listenThread.setDaemon(true);
        listenThread.start();
    }

    public void close() {
        while (listenThread.isAlive()) {
            listening = false;
            if (ds != null) {
                ds.close();
            }
        }
    }
}
