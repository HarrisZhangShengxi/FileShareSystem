package server;

import java.awt.Label;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import thread.DownThread;
import thread.ServerThread;


/**
 * 与客户端连接的查询server
 * 
 */
public class ConnectServer {

	private static ServerSocket serverSocket;

	private static ServerSocket downSocket;

	private static final int port = 8624;

	private static final int down_port = 8625;

	private Label label;

	public ConnectServer() throws IOException {
		serverSocket = new ServerSocket(port);
		downSocket = new ServerSocket(down_port);
		System.out.println("服务器连接启动.");
	}

	public void setLabel(Label label) {
		this.label = label;
	}

	/**
	 * 开始服务
	 * 
	 * @throws IOException
	 */
	public void service() throws IOException {

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("查询服务器正在等待连接...");
					Socket socket;
					try {
						socket = serverSocket.accept();
						label.setText("The user connection is successful.");
						new Thread(new ServerThread(socket)).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					System.out.println("下载服务器正在等待连接...");
					Socket socket;
					try {
						socket = downSocket.accept();
						new DownThread(socket).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

}
