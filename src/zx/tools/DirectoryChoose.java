/**
  * @(#)tools.DirectoryChoose.java  2008-10-5  
  * Copy Right Information	: Tarena
  * Project					: FileShare
  * JDK version used		: jdk1.6.4
  * Comments				: Ŀ¼ѡ��Ի���
  * Version					: 1.0
  * Sr	Date		Modified By		Why & What is modified
  * 1.	2008-10-5 	С��     		�½�
  **/
package zx.tools;

import java.awt.Component;

import javax.swing.JFileChooser;

 /**
 * Ŀ¼ѡ��Ի���
 * 2008-10-5
 * @author		���ڿƼ�[Tarena Training Group]
 * @version	1.0
 * @since		JDK1.6(����) 
 */
@SuppressWarnings("serial")
public class DirectoryChoose extends JFileChooser {

	public DirectoryChoose(Component parent,String title) {
		super(System.getProperty("user.dir"));
		setFileSelectionMode(DIRECTORIES_ONLY );
		setDialogTitle(title);
		showOpenDialog(parent);
	}
}
