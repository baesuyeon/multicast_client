import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


import java.awt.Color;
import java.awt.Font;

import java.util.Timer;
import java.util.TimerTask;

import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

public class MultiCast_Client {
	private final static String NORMAL_IP = "239.0.0.1"; 
	private final static String ADNETWORK_IP = "238.0.0.1"; 
	private final static String PORT = "2000";
	
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	
	private boolean adnetwork_mode = true;
	private JTextField timeCnt;
	private JTextField info;
	private Button mode;
	private int interval = 120; 
	int delay = 1000;
    int period = 1000;

	// ������
	private MultiCast_Client() throws Exception {
		final JFrame frame = new JFrame("Cliente UDP");
		frame.setLayout(new BorderLayout());
		frame.setLocation(100, 100);
		frame.setSize(1050, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		frame.add(mediaPlayerComponent, BorderLayout.CENTER);

		JPanel controles = new JPanel();
		controles.setLayout(new GridLayout());
		frame.add(controles, BorderLayout.SOUTH);
		
		Font f = new Font("Verdana",Font.BOLD,30);
		
		// Count �ʵ�
		timeCnt = new JTextField();
		timeCnt.setText("");
		timeCnt.setFont(f);
		timeCnt.setForeground(Color.red);
		timeCnt.setHorizontalAlignment(JTextField.CENTER);
		controles.add(timeCnt); 
		info = new JTextField();
		info.setText("CUE MODE");
		info.setFont(f);
		info.setForeground(Color.red);
		
		info.setHorizontalAlignment(JTextField.CENTER);
		controles.add(info); 
		
		// Mode ��ư
		mode = new Button("ť�� MODE OFF"); 
		mode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (adnetwork_mode) {
					System.out.println("�ֵ��Ʈ��ũ ��� -> �Ϲݸ��");
					adnetwork_mode = false;
					mode.setLabel("ť�� MODE ON");
					info.setText("NORMAL MODE");
					info.setForeground(Color.green);
				} else {
					System.out.println("�Ϲ� ��� -> �ֵ��Ʈ��ũ ���");
					adnetwork_mode = true;
					mode.setLabel("ť�� MODE OFF");
					info.setText("CUE MODE");
					info.setForeground(Color.red);
				}
			}
		});
		mode.setFont(f);
		controles.add(mode); 
		
		// �Ϲݿ��� ��û �� ����
		mediaPlayerComponent.mediaPlayer().media().play("udp://@"+NORMAL_IP+":"+PORT);
		sendGet("http://192.168.0.31:8086/api/get/1");
		// Ÿ�̸� �۵�
		time_set();
	}
	
	private void connect() {
		String dir;
		if(adnetwork_mode == false) { // �Ϲ� ���
			System.out.println("�Ϲ� ���");
			dir = "udp://@"+NORMAL_IP+":"+PORT;
		}
		else { // �ֵ��Ʈ��ũ ���
			System.out.println("�ֵ��Ʈ��ũ ���");
			dir = "udp://@"+ADNETWORK_IP+":"+PORT;
		}
		mediaPlayerComponent.mediaPlayer().media().play(dir);
	}
		
	private int setInterval() {
        return --interval;
    }
	
	private void sendGet(String targetUrl) throws Exception { 
		URL url = new URL(targetUrl); 
		HttpURLConnection con = (HttpURLConnection) url.openConnection(); 
		con.setRequestMethod("HEAD");
		con.setRequestMethod("GET");
		int responseCode = con.getResponseCode(); 
		System.out.println(responseCode); 
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())); 
		String inputLine; 
		StringBuffer response = new StringBuffer(); 
		while ((inputLine = in.readLine()) != null) { response.append(inputLine); } in.close(); 
		in.close();
		System.out.println("HTTP body : " + response.toString()); 
	}

	// Ÿ�̸�
	private void time_set() { 
		Timer time = new Timer();
		
		 time.scheduleAtFixedRate(new TimerTask() {
             public void run() {
            	 /*
            	 if(interval == 35) {
            		 // TODO 
            		 if(adnetwork_mode == true) {
                		 try {
     						sendGet("http://192.168.0.31:8086/api/get/2");
     					 } catch (Exception e) {
     						e.printStackTrace();
     					 }
                     }
            	 }
            	 */
            	 if(interval == 30){
            		 // �ֵ��Ʈ��ũ ����� ��� ����  (connect)
            		 if(adnetwork_mode == true) {
            			 connect();
            		 }
            	 }
            	 /*
            	 else if(interval == 5) {
            		 if(adnetwork_mode == true) {
                		 try {
                			 sendGet("http://192.168.0.31:8086/api/get/1");
     					 } catch (Exception e) {
     						e.printStackTrace();
     					 }
                     }
            	 }
            	 */
            	 else if (interval == 0) {
            		// �ֵ��Ʈ��ũ ����� ��� ����  (connect)
            		 if(adnetwork_mode == true) {
            			 adnetwork_mode = false;
            			 connect();
            		 }
                     
                     time.cancel();
                     time.purge();
                 } 
                 if(interval > 30 && interval <= 35 ) {
                	 // ī��Ʈ�ٿ� ����
                	 timeCnt.setText(interval - 30 + "");
                 }
                 else {
                	 timeCnt.setText("");
                 }
                 
                 setInterval();
                 System.out.println(interval);
                 
             }
         }, delay, period);
		
	}
	
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new MultiCast_Client();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}