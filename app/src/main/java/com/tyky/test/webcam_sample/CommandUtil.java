package com.tyky.test.webcam_sample;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by xintianweng on 2015/11/6.
 */
public class CommandUtil {

    public static void execCmd(String cmd) {
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            cmd += "\n";
            dos.writeBytes(cmd);
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dos != null)
                    dos.close();
                if (dis != null)
                    dis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void reboot() {
        execCmd("reboot");
    }

    public static void shudown() {
        execCmd("reboot -p");
    }
    public static void grandCameraPermission() {
        execCmd("chmod 777 /dev/video1");
    }
}
