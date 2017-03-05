package cn.gridx.java.lang.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;

/**
 * Created by tao on 11/18/16.
 */
public class ExecCommand {
    public static void main(String[] args) throws IOException, InterruptedException {
        exec();

    }

    public static void exec() throws IOException, InterruptedException {
        ProcessBuilder ps = new ProcessBuilder("hostname");

        //From the DOC:  Initially, this property is false, meaning that the
        //standard output and error output of a subprocess are sent to two
        //separate streams
        ps.redirectErrorStream(true);

        Process pr = ps.start();

        BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        pr.waitFor();
        System.out.println("ok!");

        in.close();
        System.exit(0);
    }
}
