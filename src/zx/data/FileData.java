/**
  * @(#)data.FileData.java  2008-10-5  
  * Copy Right Information	: Tarena
  * Project					: FileShare
  * JDK version used		: jdk1.6.4
  * Comments				: �ļ�����Ϣ��
  * Version					: 1.0
  * Sr	Date		Modified By		Why & What is modified
  * 1.	2008-10-5 	С��     		�½�
  **/
package zx.data;

import java.io.File;
import java.io.Serializable;
import java.util.StringTokenizer;

 /**
 * �ļ�����Ϣ��
 * 2008-10-5
 * @author		���ڿƼ�[Tarena Training Group]
 * @version	1.0
 * @since		JDK1.6(����) 
 */
@SuppressWarnings("serial")
public class FileData implements Serializable {

	/** file��·�� */
	private String path = "";
	/** file��ȫ�� */
	private String fullName = "";
	/** file���ļ��� */
	private String fileName = "";
	/** file�ĺ�׺�� */
	private String suffixName = "";
	/** �Ƿ����ļ���Ŀ¼ */
	private boolean isFile = true;
	/** file�����·��,����ڹ���Ŀ¼��·�� */
	private String relativePath = "";
	
	/**
	 * ��file����ú�����
	 * @param file �ļ���
	 */
	public FileData(File file) {
		path = file.getAbsolutePath();
		fullName = file.getName();
		isFile = file.isFile();
		if(isFile){
			int end = fullName.lastIndexOf(".");
			if(end==-1)
				fileName = fullName;
			else{
				fileName = fullName.substring(0, end);
				suffixName = fullName.substring(end,fullName.length());
			}
		}else
			fileName = fullName;
	}
	
	/**
	 * ��pathΪ·������ú������ú���ּ�ڹ���..��һ��Ŀ¼�õ�Ŀ¼��
	 * @param path ·����һ����/��
	 * @param fullName ·������һ��..,��ʾ��һ��Ŀ¼��
	 */
	public FileData(String path,String fullName) {
		this.path = path;
		this.fullName = fullName;
		fileName = fullName;
		isFile = false;
	}
	
	public FileData(File file,String SharePath,String delim) {
		this(file);
		String xpath = ContainByShareDirectory(path, SharePath, delim);
		relativePath = xpath==null?"":xpath;
	}
	
	public String getFileName() {
		return fileName;
	}
	public String getFullName() {
		return fullName;
	}
	public String getSuffixName() {
		return suffixName;
	}
	public boolean isFile() {
		return isFile;
	}
	public String getPath() {
		return path;
	}
	public String getRelativePath() {
		return relativePath;
	}
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	
	@Override
	public String toString() {
		return fileName.equals("")?(fullName.equals("")?path:fullName):fileName;
	}
	
	/**
	 * ���path��·���Ƿ���SharePath�Ĺ���Ŀ¼������һ������Ŀ¼
	 * @param path ����path
	 * @param SharePath �����Ŀ¼
	 * @param delim ��delim�ָ�
	 * @return �������ع���Ŀ¼�����·�������򷵻�null
	 */
	public static String ContainByShareDirectory(String path,String SharePath,String delim){
		StringTokenizer tokenizer = new StringTokenizer(SharePath,delim);
		while(tokenizer.hasMoreTokens()){
			String xpath = tokenizer.nextToken();
			File file = new File(xpath);
			int n = path.indexOf(xpath);
			if(path.equals(xpath))
				return File.separator+file.getName();
			if(n != -1){
				//System.out.println(xpath.length()+"<-->"+path.length()+"==>"+File.separator+file.getName()+path.substring(xpath.length(), path.length()));
				return File.separator+file.getName()+path.substring(xpath.length(), path.length());
			}
		}
		return null;
	}
	
}
