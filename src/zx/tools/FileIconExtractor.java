package zx.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import sun.awt.shell.ShellFolder;

public class FileIconExtractor{

	
	/**
	 * ��ȡ�ļ���Сͼ�ꡣ
	 * @param suffixName �ļ��ĺ�׺����
	 * @return ���ظ������ļ���Сͼ�ꡣ
	 */
	public static Icon getFileSmallIcon(String suffixName){
		try {
			File file = File.createTempFile("icon", "." + suffixName);
			return getSmallIcon(file);
		} catch (IOException e) {
			System.out.println("�����ļ�ʱ��������:"+e.getMessage());
			return null;
		}
	}
	/**
	 * ��ȡ�ļ��Ĵ�ͼ�ꡣ
	 * @param suffixName �ļ��ĺ�׺����
	 * @return ���ظ������ļ��Ĵ�ͼ�ꡣ
	 */
	public static Icon getFileBigIcon(String suffixName){
		try {
			File file = File.createTempFile("icon", "." + suffixName);
			return getBigIcon(file);
		} catch (IOException e) {
			System.out.println("�����ļ�ʱ��������:"+e.getMessage());
			return null;
		}
	}
	
	/**
	 * ��ȡĿ¼��Сͼ�ꡣ
	 * @return �����ļ��е�Сͼ�ꡣ
	 */
	public static Icon getDirectorySmallIcon(){
		File file = new File(System.getProperty("user.dir"));
		return getSmallIcon(file);
	}
	
	/**
	 * ��ȡĿ¼�Ĵ�ͼ�ꡣ
	 * @return �����ļ��еĴ�ͼ�ꡣ
	 */
	public static Icon getDirectoryBigIcon(){
		File file = new File(System.getProperty("user.dir"));
		return getBigIcon(file);
	}
	
	/**
	 * ��ȡ�ļ�file��Сͼ�ꡣ
	 * @param file �ļ���Ŀ¼��
	 * @return ���ظ��ļ���Ŀ¼�Ĵ�ͼ�ꡣ
	 */
	public static Icon getSmallIcon(File file){
		try {
			FileSystemView view = FileSystemView.getFileSystemView();
			Icon smallIcon = view.getSystemIcon(file);
			file.delete();
			return smallIcon;
		} catch (RuntimeException ioe) {
			System.out.println("��ȡСͼ��ʱ��������:"+ioe.getMessage());
			return null;
		}
	}
	
	/**
	 * ��ȡ�ļ�File�Ĵ�ͼ�ꡣ
	 * @param file �ļ���Ŀ¼��
	 * @return ���ظ��ļ���Ŀ¼�Ĵ�ͼ�ꡣ
	 */
	public static Icon getBigIcon(File file){
		try {
			ShellFolder shellFolder = ShellFolder.getShellFolder(file);
			Icon bigIcon = new ImageIcon(shellFolder.getIcon(true));
			file.delete();
			return bigIcon;
		} catch (RuntimeException ioe) {
			System.out.println("��ȡ��ͼ��ʱ��������:"+ioe.getMessage());
			return null;
		} catch (FileNotFoundException e) {
			System.out.println("��ȡ��ͼ��ʱ��������:"+e.getMessage());
			return null;
		}
	}
}
