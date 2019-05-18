/**
  * @(#)client.FSClient.java  2008-10-5  
  * Copy Right Information	: Tarena
  * Project					: FileShare
  * JDK version used		: jdk1.6.4
  * Comments				: �ļ�����������ͻ��˽��档
  * Version					: 1.0
  * Sr	Date		Modified By		Why & What is modified
  * 1.	2008-10-5 	С��     		�½�
  **/
package zx.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.DefaultListCellRenderer;
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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import zwatch.kerberos.Client.Client;
import zx.data.FSMessage;
import zx.data.FileData;

import zx.tools.DateDeal;
import zx.tools.DirectoryChoose;
import zx.tools.DownSpeed;
import zx.tools.FileIconExtractor;


 /**
 * �ļ�����������ͻ��˽��档
 * 2008-10-5
 * @author		���ڿƼ�[Tarena Training Group]
 * @version	1.0
 * @since		JDK1.6(����) 
 */
@SuppressWarnings("serial")
public class FSClient extends JFrame implements ActionListener {

	/** �˵��� */
	private JMenuBar menuBar=new JMenuBar();
	/** ��������˲˵� */
	private JMenu menuOperation = new JMenu("����(O)");
	/** ��������˰�ť */
	private JMenuItem itemLink = new JMenuItem("����(L)",KeyEvent.VK_L);
	/** ֹͣ����˰�ť */
	private JMenuItem itemDisconnect = new JMenuItem("�Ͽ�(D)",KeyEvent.VK_D);
	/** ���÷���˰�ť */
	private JMenuItem itemSet = new JMenuItem("����(S)",KeyEvent.VK_S);
	/** ��������˲˵� */
	private JMenu menuHelp = new JMenu("����(H)");
	/** ���ڷ���˰�ť */
	private JMenuItem itemAbout = new JMenuItem("����(A)",KeyEvent.VK_A);
	/** ��ʾ�����û��б� */
	private JList list = null;
	/** list��DefaultListModelģ�� */
	private DefaultListModel model = new DefaultListModel();
	/** ��ʾ��־ */
	private JTextArea areaLog = new JTextArea();
	/** ��ʾ��������Pane */
	private JPanel lblProcess = new JPanel();
	/** �����ٶ� */
	private JLabel lblSpeed = new JLabel();
	/** ������ */
	private JProgressBar progressBar = new JProgressBar(0,100);
	/** �������˿� */
	private static Integer PORT = 3608;
	private static String User = "";
	private static String Password = null;

	/** ������ip */
	private static String SERVERIP = "127.0.0.1";
	/** �����Ŀ¼ */
	private static String SAVEDIRECTORY = System.getProperty("user.dir");
	/** ���öԻ��� */
	private SetDialog dialog = new SetDialog(false);
	/** �ͻ��˶��� */
	private Socket client = null;
	/** ��������� */
	private ObjectOutputStream oos = null;
	/** ���������� */
	private ObjectInputStream ois = null;
	/** �ļ������� */
	private DataOutputStream dos = null;
	/** �����߳� */
	private Thread thread = null;
	/** �������з� */
	private boolean isRun = false;
	/** �Ƿ�ʼ�����ļ� */
	private boolean isBeginSend = false;
	/** list���Ҽ��˵� */
	private JPopupMenu popupMenu = null;
	/** �޳����û� */
	private JMenuItem itemDown = new JMenuItem("����");
	/** �ļ���ʼ����ʱ�� */
	private long fileBeginTime = 0L;
	/** �ļ���������ʱ�� */
	private long fileEndTime = 0L;
	/** �������ļ�����(��λ:�ֽ�) */
	private long totalDownData = 0L;
	
	static String ticket_v;
	
	public FSClient() {
		setTitle("�ļ�����������ͻ���");
		setSize(640, 560);
		setMinimumSize(new Dimension(320,280));
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
		menuOperation.add(itemLink);
		menuOperation.add(itemDisconnect);
		menuOperation.addSeparator();
		menuOperation.add(itemSet);
		menuHelp.add(itemAbout);
		menuHelp.setMnemonic(KeyEvent.VK_H);
		itemAbout.addActionListener(this);
		itemLink.addActionListener(this);
		itemDisconnect.addActionListener(this);
		itemSet.addActionListener(this);
		
		menuBar.add(menuOperation);
		//menuBar.add(menuHelp);
		setJMenuBar(menuBar);
		itemChange(!isRun);
		popupMenu = new JPopupMenu();
		popupMenu.add(itemDown);
		itemDown.addActionListener(this);
	}
	
	/**
	 * ��ʼ�����档
	 */
	private void init(){
		list = new JList(model);
		list.setCellRenderer(new Renderer());
		list.setFixedCellHeight(60);
		list.setFixedCellWidth(60);
		list.setVisibleRowCount(0);
		list.setDragEnabled(true);
		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.addMouseListener(new ListMouseAdapter());
		JScrollPane sp = new JScrollPane(list);
		sp.setBorder(new TitledBorder(new LineBorder(Color.LIGHT_GRAY),"����Ŀ¼"));
		add(sp);
		areaLog.setEditable(false);
		areaLog.setLineWrap(true);
		
		JScrollPane sp2 = new JScrollPane(areaLog);
		sp2.setBorder(new TitledBorder(new LineBorder(Color.LIGHT_GRAY),"��־"));
		JPanel paneInfo = new JPanel();
		paneInfo.setPreferredSize(new Dimension(625,30));
		paneInfo.setLayout(new FlowLayout(FlowLayout.RIGHT));
		lblProcess.setPreferredSize(new Dimension(300,20));
		lblProcess.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		lblProcess.setLayout(new BorderLayout());
		progressBar.setBorderPainted(false);
		lblProcess.add(progressBar);
		lblSpeed.setPreferredSize(new Dimension(60,20));
		lblSpeed.setHorizontalAlignment(SwingConstants.CENTER);
		lblSpeed.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		paneInfo.add(lblProcess);
		paneInfo.add(lblSpeed);
		JPanel pane = new JPanel();
		pane.setPreferredSize(new Dimension(625,150));
		pane.setLayout(new BorderLayout());
		pane.add(sp2);
		pane.add(paneInfo,BorderLayout.SOUTH);
		add(pane,BorderLayout.SOUTH);
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
	 * ActionListener�¼���
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==itemSet){
			dialog.setVisible(true);
			return;
		}
		if(e.getSource()==itemLink){
			AuthByKerberos();
			linkServer();
			return;
		}
		if(e.getSource()==itemDisconnect){
			disconectServer();
			return;
		}
		if(e.getSource()==itemDown){
			downData();
			return;
		}
		if(e.getSource()==itemAbout)
		{}
		//new About(this,"���ڱ��ļ����������",true);
		
	}

 	private void AuthByKerberos() {
		Client client=new Client(User, Password);
		client.run();
	}

	 /**
	 * ����/���ò˵�ѡ�񷽷���
	 * @param b bΪtrueʱ�����������ò˵����ã�ֹͣ�˵������ã����򣬷�֮��
	 */
	private void itemChange(boolean b){
		itemLink.setEnabled(b);
		itemDisconnect.setEnabled(!b);
		itemSet.setEnabled(b);
	}
	
	private void setSpeed(long fileLength){
		//System.out.println("�������:"+totalDownData*100/fileLength+"%");
		progressBar.setStringPainted(true);
		progressBar.setValue((int)(totalDownData*100/fileLength));
		progressBar.setString(totalDownData*100/fileLength+"%");
		//String speed = DownSpeed.getSpeed(totalDownData, fileBeginTime, fileEndTime);
		//if((fileEndTime-fileBeginTime)/1000>0)
			lblSpeed.setText(DownSpeed.getSpeed(totalDownData,fileBeginTime,fileEndTime));
		//System.out.println("�����ٶ�:"+speed);
	}
	
	/**
	 * ��ȡ��������Ϣ��
	 * @return ��������Ϣ��
	 */
	private String getServerInfo(){
		return "["+SERVERIP+":"+PORT+"]";
	}
	/**
	 * ��ȡ�ͻ���IP��Ϣ
	 * @return �ͻ���IP��Ϣ��
	 */
	private String getClientInfo(){
		if(client!=null){
			InetAddress address = client.getInetAddress();
			return "["+address.getHostName()+"("+address.getHostAddress()+":"+client.getLocalPort()+")]";
		}else
			return "[IP��Ϣδ֪]";
	}
	
	/**
	 * ���ӷ�������
	 */
	private void linkServer(){
		if(!isRun)
			itemChange(isRun);
		isRun = true;
		thread = new LinkServer();
		thread.start();
	}
	
	/**
	 * �Ͽ���������
	 */
	private void disconectServer(){
		new WriteThread(new FSMessage(90,null)).start();
		closeClient();
		if(thread!=null)
			thread.interrupt();thread = null;
		list.setEnabled(true);
	}
	
	/**
	 * �Ͽ����ӡ�
	 */
	private void closeClient(){
		closeFile();
		if(isRun)
			itemChange(isRun);
		isRun = false;
		writeLog("�ͷ�����"+getServerInfo()+"�Ͽ�����");
		try {
			if(oos!=null)
				oos.close();oos = null;
			if(ois!=null)
				ois.close();ois = null;
			if(client!=null)
				client.close();client = null;
		} catch (IOException e) {
			System.out.println("�ر�ʱ����:"+e.getMessage());
		}
		model.removeAllElements();
	}
	
	/**
	 * �������ݣ����ö��̵߳ķ�ʽ���غʹ������ݡ�
	 */
	private void downData(){
		try {
			Object[] objects = list.getSelectedValues();
			for(Object obj:objects){
				if(obj instanceof FileData){
					FileData data = (FileData)obj;
					oos.writeObject(new FSMessage(14,data.getPath()));
					oos.flush();
				}
			}
			list.setEnabled(!isRun);
		} catch (IOException e) {}
	}
	
	/**
	 *  �����߳��ࡣ
	 * 2008-10-5
	 * @author		���ڿƼ�[Tarena Training Group]
	 * @version	1.0
	 * @since		JDK1.6(����) 
	 */
	private class LinkServer extends Thread{
		
		public LinkServer() {
			try {
				client = new Socket(SERVERIP,PORT);
				writeLog(getClientInfo()+"�������"+getServerInfo()+"������...");
				oos = new ObjectOutputStream(new BufferedOutputStream(new DataOutputStream(client.getOutputStream())));
				new WriteThread(new FSMessage(10,"/")).start();
				ois = new ObjectInputStream(new BufferedInputStream(new DataInputStream(client.getInputStream())));
				writeLog("�ͷ�����"+getServerInfo()+"���ӳɹ�.");
			} catch (UnknownHostException e) {
				writeLog("��������:"+e.getMessage());
				closeClient();
			} catch (IOException e) {
				writeLog("��������:"+e.getMessage());
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
		 *  15���ͻ��˷��͵��������󣬲�����Ӧ
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
		@Override
		public void run() {
			try {
				while(isRun && ois!=null && oos!=null){
					//System.out.println("come read");
					if(!isBeginSend){//δ�����ļ�ʱ
						Object obj = ois.readObject();
						if(obj instanceof FSMessage){
							FSMessage message = (FSMessage)obj;
							int type = message.getType();
							//System.out.println(type);
							switch (type) {
							case 11:
								dealSendBegin();
								break;
							case 12:
								dealDirectory(message.getObject());
								break;
							case 13:
								dealSendEnd();
								break;
							case 15:
								dealDownInfo(message.getObject());
								break;
							case 20:
								createDirectory(message.getObject());
								break;
							case 21:
								createFile(message.getObject());
								break;
							case 22:
								isBeginSend = true;
								break;
							case 23:
								getComplete(message.getObject());
								break;
							case 16:
								break;
							case 91:
								writeLog(message.getObject().toString());
								closeClient();
								break;
							}
						}else
							writeLog("������"+getServerInfo()+"���ʹ�������-->"+obj);
						obj = null;
					}else{//��ʼ�����ļ�
						fileBeginTime = System.currentTimeMillis();
						long fileLength = ois.readLong();
						while(isRun && oos!=null && ois!=null){
							int readlen = 0;
							byte data[] = new byte[1024];
							readlen = ois.read(data);
							//System.out.println("-->n:"+readlen);
							if(readlen==-1)
								break;
							else{
								dos.write(data,0,readlen);
								dos.flush();
								fileEndTime = System.currentTimeMillis();
								totalDownData += readlen;
							}
							//new WriteThread(new FSMessage(32,null));
							setSpeed(fileLength);
						}
						closeFile();
						isBeginSend = false;
					}//else�����ļ�����					
				}
				
			} catch (IOException e) {
				if(isRun && client!=null){
					writeLog("�ͷ�����"+getServerInfo()+"���ӷ�������:"+e.getMessage());
					closeClient();
				}
			} catch (ClassNotFoundException e) {
				if(isRun && client!=null){
					writeLog("�ͷ�����"+getServerInfo()+"���ӷ�������:"+e.getMessage());
					closeClient();
				}
			}
		}
	}	
	
	/**
	 * �ļ����سɹ���
	 */
	private void getComplete(Object obj){
		writeLog("�ļ�["+obj.toString()+"]�������!��ʱ:"+(fileEndTime-fileBeginTime)/1000+"s");
		//closeFile();
	}
	
	/**
	 * �ر��ļ�����
	 */
	private void closeFile(){
		list.setEnabled(isRun);
		totalDownData = 0;
		fileEndTime = System.currentTimeMillis();
		progressBar.setStringPainted(false);
		progressBar.setValue(0);
		lblSpeed.setText("");
		try {
			if(dos!=null){
				dos.flush();
				dos.close();
				dos = null;
			}
			//new WriteThread(new FSMessage(33,null));
		} catch (IOException e) {
			//new WriteThread(new FSMessage(43,null));
		}
	}
	
	/**
	 * �����ļ���
	 * @param obj �����ļ������·����
	 */
	private void createFile(Object obj){
		try {
			String path = obj.toString();
			path = SAVEDIRECTORY+path;
			System.out.println("==>"+path);
			//writeLog("�����ļ�"+path+"...");
			File file = new File(path);
			if(file.exists()){
				writeLog("����ͬ���ļ�"+file.getName()+"���滻֮...");
				file.delete();
			}
			else{
				File parentFile = file.getParentFile();
				System.out.println("��Ŀ¼:"+parentFile.getAbsolutePath());
				if(!parentFile.exists())
					parentFile.mkdirs();
				//file.createNewFile();
			}
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			writeLog("�ļ�"+path+"�����ɹ�!");
			//new WriteThread(new FSMessage(31,null));
		} catch (IOException e) {
			//new WriteThread(new FSMessage(41,null));
			System.out.println("�����ļ�ʱ����:"+e.getMessage());
		}
	}
	
	/**
	 * ����Ŀ¼��
	 * @param obj ����Ŀ¼�����·��·��.
	 */
	private void createDirectory(Object obj){
		try {
			String path = obj.toString();
			path = SAVEDIRECTORY+path;
			System.out.println("==>"+path);
			writeLog("����Ŀ¼"+path+"...");
			File file = new File(path);
			if(!file.exists())
				file.mkdirs();
			writeLog("����Ŀ¼"+path+"�ɹ�!");
			//new WriteThread(new FSMessage(30,null));
		} catch (RuntimeException e) {
			//new WriteThread(new FSMessage(40,null));
		}
	}
	
	/**
	 * ����ͻ������ش������Ϣ.
	 * @param obj
	 */
	private void dealDownInfo(Object obj){
		writeLog(obj.toString());
		list.setEnabled(isRun);
	}
	
	/**
	 * ����Ŀ¼��ʼ��
	 */
	private void dealSendBegin(){
		//System.out.println("come send begin");
		model.removeAllElements();
		list.setEnabled(false);
	}
	/**
	 * ����Ŀ¼������
	 */
	private void dealSendEnd(){
		//System.out.println("come send end");
		list.setEnabled(true);
	}
	/**
	 * ���ݷ���˷��͵�Ŀ¼��ʾ���б��ϡ�
	 * @param obj �õ���Ŀ¼FileData����
	 */
	private void dealDirectory(Object obj){
		if(obj instanceof FileData)
			model.addElement((FileData)obj);
	}
	/**
	 * ��д�̡߳�
	 * 2008-10-5
	 * @author		���ڿƼ�[Tarena Training Group]
	 * @version	1.0
	 * @since		JDK1.6(����) 
	 */
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
	}
	
	/**
	 * list��ListCellRenderer��
	 * 2008-10-5
	 * @author		���ڿƼ�[Tarena Training Group]
	 * @version	1.0
	 * @since		JDK1.6(����) 
	 */
	private class Renderer extends DefaultListCellRenderer{
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if(value instanceof FileData){
				FileData data = (FileData)value;
				setVerticalAlignment(SwingConstants.CENTER);
				setVerticalTextPosition(SwingConstants.BOTTOM);
				setHorizontalAlignment(SwingConstants.CENTER);
				setHorizontalTextPosition(SwingConstants.CENTER);
				if(data.isFile())
					setIcon(FileIconExtractor.getFileBigIcon(data.getSuffixName()));
				else
					setIcon(FileIconExtractor.getDirectoryBigIcon());
				setToolTipText(data.getFullName().equals("..")?"��һ��Ŀ¼":data.getFullName());
			}
			if(isSelected)
				setBorder(new EmptyBorder(1,1,1,1));
			return component;
		}
	}
	
	/**
	 * ����ر�ʱ�����¼���
	 */
	private class MyWindowAdapter extends WindowAdapter{
		public void windowClosing(WindowEvent e) {
			//super.windowClosing(e);
			disconectServer();
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
			if(e.getSource()==list && list.isEnabled()){
				if(e.getButton()==MouseEvent.BUTTON3){
					list.clearSelection();
					int index = list.locationToIndex(e.getPoint());
					list.setSelectedIndex(index);
					Object obj = list.getSelectedValue();
					FileData data = null;
					if(obj instanceof FileData)
						data = (FileData)obj;
					if(list.getSelectedIndex()!=-1 && data!=null && !data.getFileName().equals(".."))
						popupMenu.show(list, e.getX(), e.getY());
/*					int index = list.locationToIndex(e.getPoint());
					int[] src = list.getSelectedIndices();
					int dest[] = new int[src.length+1];
					System.arraycopy(src, 0, dest, 0, src.length);
					dest[dest.length-1] = index;
					list.setSelectedIndices(dest);*/
				}
				//Ŀ¼���ļ�˫���¼�
				if(e.getClickCount()==2){
					Object object = list.getSelectedValue();
					if(object instanceof FileData){
						FileData data = (FileData)object;
						if(!data.isFile())
							new WriteThread(new FSMessage(10,data.getPath())).start();
						else
							//�ļ�ֱ������
							downData();
					}
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
		
		private JLabel lblServerIP = new JLabel("��������ַ");
		private JTextField txtServerIP = new JTextField(SERVERIP+"");
		private JLabel lblPort = new JLabel("�������˿�");
		private JTextField txtPort = new JTextField(PORT+"");
		private JLabel lblSaveDirectory = new JLabel("����Ŀ¼");
		private JTextField txtSaveDirectory = new JTextField(SAVEDIRECTORY);
		private JButton btnOK = new JButton("ȷ��");
		private JButton btnCancle = new JButton("ȡ��");
		private JButton btnChange = new JButton("�޸�");

		//change
		private JLabel lblUser = new JLabel("�û�");
		private JLabel lblPassword = new JLabel("����");

		private JTextField txtUser = new JTextField(User);
		private JTextField txtPassword = new JTextField("******");

		public SetDialog(boolean show) {
			super(FSClient.this,true);
			setTitle("ϵͳ����");
			setSize(300,200);
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
				SERVERIP = txtServerIP.getText();
				SAVEDIRECTORY = txtSaveDirectory.getText();
				/*
				Set User And Password
				 */
				User= txtUser.getText();
				Password = txtPassword.getText();


			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "��������ȷ��ֵ!");
			}
		}
		
		private void init(){
			btnOK.addActionListener(this);
			btnOK.setPreferredSize(new Dimension(80,25));
			btnCancle.addActionListener(this);
			btnCancle.setPreferredSize(new Dimension(80,25));
			btnChange.addActionListener(this);
			btnChange.setPreferredSize(new Dimension(45,25));
			btnChange.setMargin(new Insets(0,0,0,0));
			lblPort.setPreferredSize(new Dimension(60,25));
			txtPort.setPreferredSize(new Dimension(200,25));

			//change
			lblUser.setPreferredSize(new Dimension(60,25));
			txtUser.setPreferredSize(new Dimension(200,25));
			lblPassword.setPreferredSize(new Dimension(60,25));
			txtPassword.setPreferredSize(new Dimension(200,25));


			lblServerIP.setPreferredSize(new Dimension(60,25));
			txtServerIP.setPreferredSize(new Dimension(200,25));
			lblSaveDirectory.setPreferredSize(new Dimension(60,25));
			txtSaveDirectory.setPreferredSize(new Dimension(150,25));
			txtSaveDirectory.setEditable(false);
			paneSet.setLayout(new FlowLayout(FlowLayout.CENTER,7,5));
			paneSet.add(lblServerIP);
			paneSet.add(txtServerIP);
			paneSet.add(lblPort);
			paneSet.add(txtPort);
			//change
			paneSet.add(lblUser);
			paneSet.add(txtUser);
			paneSet.add(lblPassword);
			paneSet.add(txtPassword);

			paneSet.add(lblSaveDirectory);
			paneSet.add(txtSaveDirectory);
			paneSet.add(btnChange);
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
			if(e.getSource()==btnChange){
				DirectoryChoose chooser = new DirectoryChoose(this,"ѡ��Ҫ���浽��Ŀ¼");
				File file = chooser.getSelectedFile();
				if(file!=null)
					txtSaveDirectory.setText(file.getAbsolutePath());
				return;
			}
			
			dispose();
		}
	}

}
