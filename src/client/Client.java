/**
 * @author mauriverti
 */

package client;

public class Client {
    public static Integer portDestino = 8910;
//    public static final String ipDestino = "10.81.119.117";        // unioeste server
    public static final String ipDestino = "192.168.122.1";        // virbr0
//    public static final String ipDestino = "192.168.25.200";     // casa
//    public static final String ipDestino = "10.19.10.129";          // MV

    public static void main(String[] args) {
        new ThreadClient().start();
    }
}