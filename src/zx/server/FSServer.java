/**
  * @(#)server.FSServer.java  2008-10-5  
  * Copy Right Information	: Tarena
  * Project					: FileShare
  * JDK version used		: jdk1.6.4
  * Comments				: ����������ˡ�
  * Version					: 1.0
  * Sr	Date		Modified By		Why & What is modified
  * 1.	2008-10-5 	С��     		�½�
  **/
package zx.server;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import zwatch.kerberos.V.V_check;
import zx.data.FSMessage;
import zx.data.FileData;

import zx.tools.DateDeal;
import zx.tools.DirectoryChoose;


 /**
 * ����������ˡ�
 * 2008-10-5
 * @author		���ڿƼ�[Tarena Training Group]
 * @version	1.0
 * @since		JDK1.6(����) 
 */
@SuppressWarnings("serial")
public class FSServer extends JFrame implements ActionListener,Runnable{
	
	/** �˵��� */
	private JMenuBar menuBar=new JMenuBar();
	/** ��������˲˵� */
	private JMenu menuOperation = new JMenu("����(O)");
	/** ��������˰�ť */
	private JMenuItem itemBegin = new JMenuItem("����(B)",KeyEvent.VK_B);
	/** ֹͣ����˰�ť */
	private JMenuItem itemEnd = new JMenuItem("ֹͣ(E)",KeyEvent.VK_E);
	/** ���÷���˰�ť */
	private JMenuItem itemSet = new JMenuItem("����(S)",KeyEvent.VK_S);
	/** ��������˲˵� */
	private JMenu menuHelp = new JMenu("����(H)");
	/** ���ڷ���˰�ť */
	private JMenuItem itemAbout = new JMenuItem("����(A)",KeyEvent.VK_A);
	/** list��DefaultListModel */
	DefaultListModel model = new DefaultListModel();
	/** ��ʾ�����û��б� */
	private JList list = new JList(model);
	/** ��ʾ��־ */
	private JTextArea areaLog = new JTextArea();
	/** �����������Ķ˿� */
	private static Integer PORT = 3608;
	/** �������ӵ������ */
	private static Integer ALLOWNUM = 5;
	/** �����Ŀ¼�����Ŀ¼��|�ֿ� */
	private static String SHAREDIRECTORY = "";
	/** ���öԻ��� */
	private SetDialog dialog = new SetDialog(false);
	/** �����ServerSocket */
	private ServerSocket server = null;
	/** ������Ƿ����� */
	private boolean isRun = false;
	/** ���������ʱ���߳� */
	private Thread thread = null;
	/** list���Ҽ��˵� */
	private JPopupMenu popupMenu = null;
	/** �޳����û� */
	private JMenuItem itemDelete = new JMenuItem("��������");
	
	public FSServer() {
		setTitle("�ļ���������������[FileShareServer]");
		setSize(620,420);
		setMinimumSize(new Dimension(300,210));
		//setResizable(false);
		Toolkit tk=Toolkit.getDefaultToolkit();
		setLocation((tk.getScreenSize().width-getSize().width)/2,(tk.getScreenSize().height-getSize().height)/2);
		
		initMenu();
		init();
		addWindowListener(new MyWindowAdapter());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * ��ʼ���˵���
	 */
	private void initMenu(){
		menuOperation.setMnemonic(KeyEvent.VK_O);
		menuOperation.add(itemBegin);
		menuOperation.add(itemEnd);
		menuOperation.addSeparator();
		menuOperation.add(itemSet);
		menuHelp.add(itemAbout);
		menuHelp.setMnemonic(KeyEvent.VK_H);
		itemAbout.addActionListener(this);
		itemBegin.addActionListener(this);
		itemEnd.addActionListener(this);
		itemSet.addActionListener(this);
		
		menuBar.add(menuOperation);
		//menuBar.add(menuHelp);
		setJMenuBar(menuBar);
		itemChange(!isRun);
		popupMenu = new JPopupMenu("�����˵�");
		popupMenu.add(itemDelete);
		itemDelete.addActionListener(this);
	}
	
	/**
	 * ��ʼ�����档
	 */
	private void init(){
		list.addMouseListener(new ListMouseAdapter());
		areaLog.setLineWrap(true);
		areaLog.setEditable(false);
		JScrollPane sp1 = new JScrollPane(list);
		sp1.setPreferredSize(new Dimension(160,380));
		sp1.setBorder(new TitledBorder(new LineBorder(Color.LIGHT_GRAY),"���߿ͻ���"));
		JScrollPane sp2 = new JScrollPane(areaLog);
		sp2.setBorder(new TitledBorder(new LineBorder(Color.LIGHT_GRAY),"������־"));
		add(sp1,BorderLayout.WEST);
		add(sp2);
		
		
	}
	
	/**
	 * ActionListener�¼���
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==itemSet){
			dialog.setVisible(true);
			return;
		}
		if(e.getSource()==itemBegin){
			if(SHAREDIRECTORY.equals("")){
				JOptionPane.showMessageDialog(null, "�������ù���Ŀ¼!");
				return;
			}
			startServer();
			return;
		}
		if(e.getSource()==itemEnd){
			stopServer();
			return;
		}
		if(e.getSource()==itemAbout)
		{}
		//new About(this,"���ڱ��ļ����������",true);
		if(e.getSource()==itemDelete){
			Object[] objects = list.getSelectedValues();
			for(Object obj:objects){
				if(obj instanceof ClientThread){
					ClientThread client = (ClientThread)obj;
					int n = JOptionPane.showConfirmDialog(list, "ȷ��ɾ�����û�"+client.getClientInfo()+"?","ȷ������?",JOptionPane.OK_CANCEL_OPTION);
					if(n==JOptionPane.OK_OPTION)
						client.letClientQuit("����Ա����������Ϣ������!");
				}
			}
		}
	}
	
	@Override
	/**
	 * ���������̡߳�
	 */
	public void run() {
		try {
			while(isRun){
				Socket client = server.accept();
				new ClientThread(client).start();
			}
		} catch (IOException e) {
			if(isRun && server!=null){
				writeLog("������"+getServerInfo()+"��������:"+e.getMessage());
				stopServer();
			}
		}
	}
	/**
	 * ����/���ò˵�ѡ�񷽷���
	 * @param b bΪtrueʱ�����������ò˵����ã�ֹͣ�˵������ã����򣬷�֮��
	 */
	private void itemChange(boolean b){
		itemBegin.setEnabled(b);
		itemEnd.setEnabled(!b);
		itemSet.setEnabled(b);
	}
	
	/**
	 * ��¼��־���ܡ�
	 * @param msg ��־������
	 */
	private void writeLog(String msg){
		areaLog.append(DateDeal.getCurrentTime()+","+msg+"\n");
		//�������Զ��¹�
		areaLog.setCaretPosition(areaLog.getDocument().getLength());
	}
	
	/**
	 * ��ȡ��������IP��Ϣ��
	 * @return ��������IP��Ϣ��
	 */
	private String getServerInfo(){
		if(server!=null){
			InetAddress address = server.getInetAddress();
			return "["+address.getHostName()+"("+address.getHostAddress()+":"+server.getLocalPort()+")]";
		}else
			return "[IP��Ϣδ֪]";
	}
	
	/**
	 * ������������
	 */
	private void startServer(){
		try {
			itemChange(isRun);
			isRun = true;
			writeLog("������"+getServerInfo()+"������...");
			server = new ServerSocket(PORT);
			thread = new Thread(this);
			thread.start();
			writeLog("������"+getServerInfo()+"�����ɹ�!�ȴ��ͻ�������.");
		} catch (IOException e) {
			writeLog("������"+getServerInfo()+"����ʧ��!ԭ������:"+e.getMessage());
			stopServer();
		}
	}
	
	/**
	 * ֹͣ��������
	 */
	private void stopServer(){
		try {
			itemChange(isRun);
			isRun = false;
			writeLog("������"+getServerInfo()+"�ر���...");
			for(int i=0;i<model.size();i++){
				Object obj = model.get(i);
				if(obj instanceof ClientThread){
					ClientThread client = (ClientThread)obj;
					client.letClientQuit("����˹ر�!");
				}
			}
			model.clear();
			if(server!=null)
				server.close();server = null;
			writeLog("������"+getServerInfo()+"�رճɹ�.");
			if(thread!=null)
				thread.interrupt();thread = null;
		} catch (IOException e) {
			writeLog("������"+getServerInfo()+"�ر�ʧ��!ԭ������:"+e.getMessage());
		}
	}
	
	/**
	 * �ͻ����߳��ࡣ
	 * 2008-10-5
	 * @author		���ڿƼ�[Tarena Training Group]
	 * @version	1.0
	 * @since		JDK1.6(����) 
	 */
	private class ClientThread extends Thread{
		private Socket client = null;
		private ObjectOutputStream oos = null;
		private ObjectInputStream ois = null;
		public ClientThread(Socket client) {
			this.client = client;
			model.addElement(this);
			try {
				writeLog("�ͻ���"+getClientInfo()+"����");
				ois = new ObjectInputStream(new BufferedInputStream(new DataInputStream(client.getInputStream())));
				oos = new ObjectOutputStream(new BufferedOutputStream(new DataOutputStream(client.getOutputStream())));
				if(model.size()>ALLOWNUM)
					letClientQuit("�������������:"+ALLOWNUM+".���Ժ�����!");
			} catch (IOException e) {
				writeLog("�ͻ���"+getClientInfo()+"��������:"+e.getMessage());
				closeClient();
			}
		}
		/**
		 * ������Ϣ
		 * ��Ϣ���ͣ�
		 * 	10���ͻ��˷��������ȡĿ¼
		 * 	11������˿�ʼ����Ŀ¼
		 * 	12������˸��ݿͻ���������Ŀ¼
		 * 	13������˴���Ŀ¼����
		 * 	14���ͻ��˷����������ع����ļ�,���͸ù����ļ��ľ���·��
		 * 	15���ͻ��˷��͵��������󣬲�����Ӧ
		 * 	20������˸��ݿͻ��������Ϳͻ��˴���Ŀ¼�����͸�Ŀ¼�����·��
		 * 	30������20��������·������Ŀ¼�ɹ�,�ƺ��޷�����
		 * 	40������20��������·������Ŀ¼ʧ��,�ƺ��޷�����
		 * 	21����ʼ�������ݣ�������ļ������·��
		 * 	31������21��������·�������ļ��ɹ�
		 * 	41������21��������·�������ļ�ʧ��
		 * 	22����ʼ��������
		 * 	32���������ݳɹ����˴���������
		 * 	42����������ʧ�ܣ��˴���������
		 * 	23�����ݴ������
		 * 	33���ر����ɹ����˴���������
		 * 	43���ر���ʧ�ܣ��˴���������
		 *  90���ͻ��˶Ͽ�����
		 * 	91������˶Ͽ�����
		 */
		public void run() {
			try {
				while(isRun && ois!=null && oos!=null){
					Object obj = ois.readObject();
					if(obj instanceof FSMessage){
						//Verification on here
						FSMessage message = (FSMessage)obj;
						if(!V_check.check(client, message.getTicket(), message.getAuth())){
							letClientQuit("Ʊ�ݴ���");
							break;
						}else{
							int type = message.getType();
							switch (type) {
							case 10:
								dealDirectory(message.getObject());
								break;
							case 14:
								dealSendData(message.getObject());
								break;
							case 90:
								closeClient();
								break;
							}
						}
					}else{ writeLog("�ͻ���"+getClientInfo()+"���ʹ�������-->"+obj);}
				}
			} catch (IOException e) {
				writeLog("�ͻ���"+getClientInfo()+"��������:"+e.getMessage());
				closeClient();
			} catch (ClassNotFoundException e) {
				writeLog("�ͻ���"+getClientInfo()+"��������:"+e.getMessage());
				closeClient();
			}			
		}
		
		/**
		 * ����ͻ��˻�ȡĿ¼������
		 * @param obj ����ȡ��Ŀ¼��
		 */
		private void dealSendData(Object obj){			
			new WriteThreadForSendData(obj).start();
		}
		
		/**
		 * ����ͻ��˻�ȡĿ¼������
		 * @param obj ����ȡ��Ŀ¼��
		 */
		private void dealDirectory(Object obj){			
			new WriteThreadForDirectory(obj).start();
		}
		
		/**
		 * ��ȡ�ͻ���IP��Ϣ
		 * @return �ͻ���IP��Ϣ��
		 */
		private String getClientInfo(){
			if(client!=null){
				InetAddress address = client.getInetAddress();
				return "["+address.getHostName()+"("+address.getHostAddress()+":"+client.getPort()+")]";
			}else
				return "[IP��Ϣδ֪]";
		}
		
		/**
		 * ʹ�ÿͻ����˳���
		 */
		public void letClientQuit(String msg){
			FSMessage message = new FSMessage(91,msg);
			//new WriteThread(message).start();
			//��ֹ��ָ���쳣!������õ����̹߳رյ�����˵�����ʱ�ܿ��ܷ�����ָ���쳣��
			try {
				oos.writeObject(message);
				oos.flush();
			} catch (IOException e) {}
			closeClient();
		}
		
		/**
		 * �رյ��ͻ��˵����ӡ�
		 */
		private void closeClient(){
			model.removeElement(this);
			writeLog("�ͻ���"+getClientInfo()+"����!");
			try {
				if(oos!=null)
					oos.close();oos = null;
				if(ois!=null)
					ois.close();ois = null;
				if(client!=null)
					client.close();client = null;
			} catch (IOException e) {}
		}
		
		@Override
		public String toString() {
			return getClientInfo();
		}
		/*
		 * ��д�̡߳�
		 * 2008-10-5
		 * @author		���ڿƼ�[Tarena Training Group]
		 * @version	1.0
		 * @since		JDK1.6(����) 
		 */		/*
		private class WriteThread extends Thread{
			FSMessage message = null;
			public WriteThread(FSMessage message) {
				this.message = message;
			}
			@Override
			public void run() {
				try {
					if(oos!=null){
						oos.writeObject(message);
						oos.flush();
					}
				} catch (IOException e) {}
			}
		}*/
		/**
		 * ����Ŀ¼�̡߳�
		 * 2008-10-6
		 * @author		���ڿƼ�[Tarena Training Group]
		 * @version	1.0
		 * @since		JDK1.6(����) 
		 */
		private class WriteThreadForDirectory extends Thread{
			String path = null;
			public WriteThreadForDirectory(Object obj) {
				path = obj.toString();
			}
			@Override
			public void run() {
				try {
					if(oos!=null){
						oos.writeObject(new FSMessage(11,null));
						oos.flush();
						//���䶥������Ŀ¼.
						writeLog("�ͻ���"+getClientInfo()+"��ȡĿ¼:"+path);
						if(path.equals("/")){
							//System.out.println("come ==>"+path);
							StringTokenizer tokenizer = new StringTokenizer(SHAREDIRECTORY,"|");
							while(tokenizer.hasMoreTokens()){
								String xpath = tokenizer.nextToken();
								File file = new File(xpath);
								if(file!=null && file.exists() && file.canRead()){
									FSMessage message = new FSMessage(12,new FileData(file));
									oos.writeObject(message);
									oos.flush();
								}
							}
						}else{
							//System.out.println("come -->"+path);
							//����ȷ���ͻ��˴��������Ŀ¼���Ѿ��������Ŀ¼
							if(FileData.ContainByShareDirectory(path, SHAREDIRECTORY, "|")!=null){
							//if(path.indexOf(xpath)!=-1){
								File xfile = new File(path);
								if(xfile.exists() && xfile.canRead()){
									//������������Ŀ¼�ǹ���Ŀ¼�е�����һ��,����һ��Ŀ¼Ϊ/
									if(SHAREDIRECTORY.indexOf(path)!=-1)
										oos.writeObject(new FSMessage(12,new FileData("/","..")));
									//���ǹ���Ŀ¼�е�����һ��,��������һ��Ŀ¼Ϊ��path
									else
										oos.writeObject(new FSMessage(12,new FileData(xfile.getParent(),"..")));
									oos.flush();
									//�ȴ���Ŀ¼
									for(File file:xfile.listFiles())
										if(file!=null && file.canRead() && file.exists() && file.isDirectory()){
											FSMessage message = new FSMessage(12,new FileData(file));
											oos.writeObject(message);
											oos.flush();
										}
									//�ڴ����ļ�
									for(File file:xfile.listFiles())
										if(file!=null && file.canRead() && file.exists() && file.isFile()){
											FSMessage message = new FSMessage(12,new FileData(file));
											oos.writeObject(message);
											oos.flush();
										}
								}
							}
						}
						oos.writeObject(new FSMessage(13,null));
						oos.flush();
					}
				} catch (IOException e) {}
			}
		}
		
		/**
		 * ���������̡߳�
		 * 2008-10-6
		 * @author		���ڿƼ�[Tarena Training Group]
		 * @version	1.0
		 * @since		JDK1.6(����) 
		 */
		private class WriteThreadForSendData extends Thread{
			private String path = null;
			public WriteThreadForSendData(Object obj) {
				path = obj.toString();
			}
			@Override
			public void run() {
				send(new File(path));
			}
			
			synchronized private void send(File file){
				try {
					String relativePath = FileData.ContainByShareDirectory(file.getAbsolutePath(), SHAREDIRECTORY, "|");
					if(relativePath!=null && file!=null && file.exists() && file.canRead() && !file.getName().equals("")){//�ļ�����Ϊ��
						//writeLog("�ͻ���"+getClientInfo()+"׼�������ļ�:"+path+"��");
						if(file.isDirectory()){
							oos.writeObject(new FSMessage(20,relativePath));
							oos.flush();
							for(File xfile:file.listFiles())
								send(xfile);
						}
						if(file.isFile()){
							oos.writeObject(new FSMessage(21,relativePath));
							oos.flush();
							writeLog("��ʼ��ͻ���"+getClientInfo()+"�����ļ�:"+file.getAbsolutePath()+"...");
							oos.writeObject(new FSMessage(22,null));
							oos.flush();
							DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
							oos.writeLong(file.length());
							oos.flush();
							int readlen = 0;
							do{
								byte[] data = new byte[1024];
								readlen = dis.read(data);
								if(readlen==-1)
									break;
								//oos.writeObject(new FSMessage(22,data));
								oos.write(data,0,readlen);
								oos.flush();
								data = null;
							}while(true);
							dis.close();
							dis = null;
							oos.writeObject(new FSMessage(23,file.getName()));
							oos.flush();
						}
						writeLog("�ͻ���"+getClientInfo()+"�ɹ������ļ�:"+file.getAbsolutePath()+"��");
					}else{//file��Ϊ��end
						oos.writeObject(new FSMessage(15,"�������"+path+"���ɴ�..."));
						oos.flush();
					}
				} catch (IOException e) {
					System.out.println(""+e.getMessage());
					//e.printStackTrace();
				}
			}//send����end
		}
	}
	
	/**
	 * ����ر�ʱ�����¼���
	 */
	private class MyWindowAdapter extends WindowAdapter{
		public void windowClosing(WindowEvent e) {
			//super.windowClosing(e);
			stopServer();
		}
	}
	
	/**
	 * ����¼���
	 * 2008-10-5
	 * @author		���ڿƼ�[Tarena Training Group]
	 * @version	1.0
	 * @since		JDK1.6(����) 
	 */
	private class ListMouseAdapter extends MouseAdapter{
		 @Override
		public void mouseClicked(MouseEvent e) {
			if(e.getSource()==list){
				if(e.getButton()==MouseEvent.BUTTON3){
/*					list.clearSelection();
					int index = list.locationToIndex(e.getPoint());
					list.setSelectedIndex(index);*/
					int index = list.locationToIndex(e.getPoint());
					int[] src = list.getSelectedIndices();
					int dest[] = new int[src.length+1];
					System.arraycopy(src, 0, dest, 0, src.length);
					dest[dest.length-1] = index;
					list.setSelectedIndices(dest);
					if(list.getSelectedIndex()!=-1)
						popupMenu.show(list, e.getX(), e.getY());
				}
			}
		}
	}
	/**
	 * ���öԻ���
	 * 2008-10-5
	 * @author		���ڿƼ�[Tarena Training Group]
	 * @version	1.0
	 * @since		JDK1.6(����) 
	 */
	private class SetDialog extends JDialog implements ActionListener{
		private JPanel paneSet = new JPanel();
		
		private JLabel lblPort = new JLabel("�������˿�");
		private JTextField txtPort = new JTextField(PORT+"");
		private JLabel lblAllowNum = new JLabel("���������");
		private JTextField txtAllowNum = new JTextField(ALLOWNUM+"");
		private JLabel lblShareDirectory = new JLabel("����Ŀ¼");
		private JTextArea txtShareDirectory = new JTextArea(SHAREDIRECTORY);
		private JButton btnOK = new JButton("ȷ��");
		private JButton btnCancle = new JButton("ȡ��");
		private JButton btnAdd = new JButton("���");
		private JButton btnClear = new JButton("���");
		
		public SetDialog(boolean show) {
			super(FSServer.this,true);
			setTitle("ϵͳ����");
			setSize(300,220);
			setResizable(false);
			Toolkit tk=Toolkit.getDefaultToolkit();
			setLocation((tk.getScreenSize().width-getSize().width)/2,(tk.getScreenSize().height-getSize().height)/2);
			
			init();
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setVisible(show);
		}
		
		private void initSet(){
			try {
				PORT = Integer.parseInt(txtPort.getText());
				ALLOWNUM = Integer.parseInt(txtAllowNum.getText());
				SHAREDIRECTORY = txtShareDirectory.getText();
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "��������ȷ��ֵ!");
			}
		}
		
		private void init(){
			btnOK.addActionListener(this);
			btnOK.setPreferredSize(new Dimension(80,25));
			btnCancle.addActionListener(this);
			btnCancle.setPreferredSize(new Dimension(80,25));
			btnAdd.addActionListener(this);
			btnAdd.setPreferredSize(new Dimension(45,25));
			btnAdd.setMargin(new Insets(0,0,0,0));
			btnClear.addActionListener(this);
			btnClear.setPreferredSize(new Dimension(45,25));
			btnClear.setMargin(new Insets(0,0,0,0));
			lblPort.setPreferredSize(new Dimension(60,25));
			txtPort.setPreferredSize(new Dimension(200,25));
			lblAllowNum.setPreferredSize(new Dimension(60,25));
			txtAllowNum.setPreferredSize(new Dimension(200,25));
			lblShareDirectory.setPreferredSize(new Dimension(60,60));
			lblShareDirectory.setToolTipText("��Ӷ������Ŀ¼,��|�ָ�");
			txtShareDirectory.setPreferredSize(new Dimension(150,60));
			txtShareDirectory.setToolTipText("��Ӷ������Ŀ¼,��|�ָ�");
			txtShareDirectory.setEditable(false);
			txtShareDirectory.setLineWrap(true);
			paneSet.setLayout(new FlowLayout(FlowLayout.CENTER,5,5));
			paneSet.add(lblPort);
			paneSet.add(txtPort);
			paneSet.add(lblAllowNum);
			paneSet.add(txtAllowNum);
			paneSet.add(lblShareDirectory);
			JScrollPane sp = new JScrollPane(txtShareDirectory);
			sp.setPreferredSize(new Dimension(150,60));
			paneSet.add(sp);
			JPanel paneBtn = new JPanel();
			paneBtn.setPreferredSize(new Dimension(46,60));
			paneBtn.setLayout(new FlowLayout(FlowLayout.CENTER,2,3));
			paneBtn.add(btnAdd);
			paneBtn.add(btnClear);
			paneSet.add(paneBtn);
			paneSet.setBorder(new TitledBorder(new LineBorder(Color.LIGHT_GRAY),"����[->]"));
			add(paneSet);
			JPanel pane = new JPanel();
			pane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			pane.add(btnOK);
			pane.add(btnCancle);
			pane.setPreferredSize(new Dimension(200,35));
			add(pane,BorderLayout.SOUTH);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==btnOK)
				initSet();
			if(e.getSource()==btnAdd){
				DirectoryChoose chooser = new DirectoryChoose(this,"ѡ��Ҫ�����Ŀ¼");
				File file = chooser.getSelectedFile();
				if(file!=null){
					String path = file.getAbsolutePath();
					String xpath = txtShareDirectory.getText();
					if(xpath.indexOf(path)==-1)
						txtShareDirectory.append(path+"|");
				}
				return;
			}
			if(e.getSource()==btnClear){
				txtShareDirectory.setText("");
				return;
			}
			dispose();
		}
	}
}
