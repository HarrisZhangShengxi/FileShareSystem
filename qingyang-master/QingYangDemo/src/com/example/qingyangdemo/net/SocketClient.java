package com.example.qingyangdemo.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.example.qingyangdemo.base.AppException;
import com.example.qingyangdemo.base.BaseApplication;
import com.example.qingyangdemo.bean.Disk;
import com.example.qingyangdemo.bean.PcFile;

/**
 * socket访问pc公共类
 * 
 */
public class SocketClient {

	// 连接超时时间
	private static final int TIMEOUT_CONNECTION = 2000;

	// 重新操作次数
	private static final int RETRY_TIME = 3;

	/**
	 * 获取socket
	 * 
	 * @return
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws Exception
	 */
	private static Socket getSocket(BaseApplication application)
			throws UnknownHostException, IOException {
		Socket socket = new Socket(application.getIpAdress(), URL.PC_PORT);//获取ip地址和端口号
		socket.setKeepAlive(true);//检测连接是否正常，否则进行重连
		socket.setSoTimeout(TIMEOUT_CONNECTION);//设置超时时间
		return socket;
	}

	/**
	 * socket连接方法
	 * 
	 * @return
	 */
	private static String socketConnect(BaseApplication application,
			String method) throws AppException {

		DataInputStream dis = null;

		DataOutputStream dos = null;

		Socket socket = null;

		String result = "";

		int time = 0;

		do {
			try {
				socket = getSocket(application);

				dos = new DataOutputStream(socket.getOutputStream());//发送请求信息到服务器端

				dos.writeUTF(method);

				dis = new DataInputStream(socket.getInputStream());//获取从服务器端接收到的数据信息

				result = dis.readUTF();
				break;

			} catch (IOException e) {
				time++;//计算重连次数
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);//暂停1秒然后继续尝试连接

						continue;
					} catch (InterruptedException e1) {
					}
				}
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				try {
					if (dis != null) {
						dis.close();
					}
					if (dos != null) {
						dos.close();
					}
					if (socket != null) {
						socket.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw AppException.network(e);
				}

			}
		} while (time < RETRY_TIME);//重连次数超过初始次数，则跳出循环，连接失败

		return result;
	}

	// 获取盘符信息
	public static List<Disk> getDiskInfo(BaseApplication application)
			throws AppException {
		return Disk.parse(socketConnect(application, URL.GET_DISK));
	}

	// 获取盘符下的文件信息
	public static List<PcFile> getFileInfo(BaseApplication application,
			String diskPath) throws AppException {
		return PcFile.parse(socketConnect(application, diskPath));
	}

}
