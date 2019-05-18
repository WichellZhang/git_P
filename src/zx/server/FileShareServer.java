/**
  * @(#)server.FileShareServer.java  2008-10-5  
  * Copy Right Information	: Tarena
  * Project					: FileShare
  * JDK version used		: jdk1.6.4
  * Comments				: �ļ���������������������
  * Version					: 1.0
  * Sr	Date		Modified By		Why & What is modified
  * 1.	2008-10-5 	С��     		�½�
  **/
package zx.server;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedInputStream;
import java.io.IOException;

import zx.tools.SetFont;

 /**
 * �ļ���������������������
 * 2008-10-5
 * @author		���ڿƼ�[Tarena Training Group]
 * @version	1.0
 * @since		JDK1.6(����) 
 */
public class FileShareServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT,new BufferedInputStream(FileShareServer.class.getResourceAsStream("/zx/tools/simsun.ttc")));
			font = font.deriveFont(Font.PLAIN, 12);
			SetFont.setFont(font);
		} catch (FontFormatException e) {
			System.out.println("����:"+e.getMessage());
		} catch (IOException e) {
			System.out.println("����:"+e.getMessage());
		}
		new FSServer();
	}

}
