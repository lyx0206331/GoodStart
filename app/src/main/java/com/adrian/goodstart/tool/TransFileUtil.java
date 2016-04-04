package com.adrian.goodstart.tool;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by adrian on 16-3-13.
 */
public class TransFileUtil {

    private String ip = "";   //192.168.31.163, 192.168.199.198
//private String ip = "192.168.0.114";
    private int port = 6789;

    private File[] files;
    private Socket socket;
    private Callback callback;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    // 根据图片名称上传照相机中单个照片
    private void upload(String name) {
        DataOutputStream dos;
        FileInputStream fis;
        try {
            ///sdcard/DCIM/Camera/照相机拍摄后图片所存路径
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/Camera/" + name.trim());
            if (file.length() == 0) {
                return;
            } else {
                socket = new Socket(ip, port);
                dos = new DataOutputStream(socket.getOutputStream());
                fis = new FileInputStream(file);
                dos.writeUTF(name.substring(0,name.indexOf(".")));
                dos.flush();
                byte[] sendBytes = new byte[1024 * 8];
                int length;
                while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                    dos.write(sendBytes, 0, length);
                    dos.flush();// 发送给服务器
                }
                dos.close();//在发送消息完之后一定关闭，否则服务端无法继续接收信息后处理，手机卡机
                /*reader = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                result = Boolean.parseBoolean(reader.readLine().toString());
                System.out.println("上传结果" + result);//运行时总是提示socket关闭，不能接收服务端返回的消息
                reader.close();*/
                fis.close();
                socket.close();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }catch (SocketTimeoutException e) {
            e.printStackTrace();
//            Toast.makeText(UploadPhotoActivity.this, "超时，上传失败",
//                    Toast.LENGTH_LONG).show();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    //根据文件夹路径上传所有的图片到服务器
    //此dirpath是图片绝对路径
    public void seriesUpload(final Context ctx, final String dirpath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataOutputStream dos;
                FileInputStream fis;
                try {
//                    File root = new File(dirpath);
//                    files = root.listFiles(new JpgFileFilter());
                    files = getFiles(dirpath);
                    LogUtil.e("TAG", "文件长度 ： " + files.length);
                    if (files.length == 0) {
                        return;
                    } else {
                        int len = files.length > 10 ? 10 : files.length;
                        for (int i = 0; i < len; i++) {
                            File file = files[i];
                            String name = file.getName();
                            LogUtil.e("TAG", "send file : " + name + "/" + file.lastModified());
                            socket = new Socket(ip, port);
                            dos = new DataOutputStream(socket.getOutputStream());
                            fis = new FileInputStream(file);
                            dos.writeUTF(name.substring(0, name.indexOf(".")) + "_" + len + name.substring(name.indexOf(".")));//截取图片名称
                            dos.flush();
                            byte[] sendBytes = new byte[1024 * 8];
                            int length;
                            while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                                dos.write(sendBytes, 0, length);
                                dos.flush();// 发送给服务器
                                if (callback != null) {
//                                    callback.updateProgress(100);
                                }
                            }
                            dos.close();//在发送消息完之后一定关闭，否则服务端无法继续接收信息后处理，手机卡机
                            fis.close();
                            socket.close();
                        }

                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (SocketTimeoutException e) {
                    e.printStackTrace();
//            Toast.makeText(UploadPhotoActivity.this, "超时，上传失败",
//                    Toast.LENGTH_LONG).show();
                }catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 获取按时间排序的jpg图片
     * @param dirPath
     * @return
     */
    public File[] getFiles (String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return null;
        }
        File[] files = null;
        File root = new File(dirPath);
        if (!root.exists() || !root.isDirectory()) {
            return null;
        }
        files = root.listFiles(new JpgFileFilter(".jpg"));
        Arrays.sort(files, new SortByLastModified(1));
        return files;
    }

    /**
     * 过滤jpg图片文件
     */
    class JpgFileFilter implements FileFilter {

        private String type;

        public JpgFileFilter(String type) {
            this.type = type;
        }

        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return false;
            } else {
                String name = pathname.getName();
                if (name.endsWith(type)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    /**
     * 按时间排序
     */
    class SortByLastModified implements Comparator<File> {

        private int type;   //排序方式。0为从小到大，非0为从大到小

        public SortByLastModified(int type) {
            this.type = type;
        }

        @Override
        public int compare(File lhs, File rhs) {
            long diff;
            if (type == 0) {
                diff = lhs.lastModified() - rhs.lastModified();
            } else {
                diff = rhs.lastModified() - lhs.lastModified();
            }
            if (diff > 0) {
                return 1;
            } else if (diff < 0) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public interface Callback {
        void exception(int errorCode);
        void updateProgress(int progress);
    }
}
