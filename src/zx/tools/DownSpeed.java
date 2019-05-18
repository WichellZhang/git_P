/**
  * @(#)tools.DownSpeed.java  2008-10-6  
  * Copy Right Information	: Tarena
  * Project					: FileShare
  * JDK version used		: jdk1.6.4
  * Comments				: �����ٶ��ࡣ
  * Version					: 1.0
  * Sr	Date		Modified By		Why & What is modified
  * 1.	2008-10-6 	С��     		�½�
  **/
package zx.tools;

import java.util.Vector;

 /**
 * �����ٶ��ࡣ
 * 2008-10-6
 * @author		���ڿƼ�[Tarena Training Group]
 * @version	1.0
 * @since		JDK1.6(����) 
 */
public class DownSpeed {

	private static Vector<String> v = new Vector<String>();
	static{
		v.add("B");
		v.add("K");
		v.add("M");
		v.add("G");
		v.add("T");
	}
	
	/**
	 * �������ص��ֽ��������ص�ʱ�䷵�غ��ʵ������ٶ�
	 * @param totalData ���ص����ֽ���
	 * @param beginTime ��ʼ����ʱ�䣬����Ϊ��λ
	 * @param endTime ��������ʱ�䣬����Ϊ��λ
	 * @return ���ݴ�С���غ��ʵ��ٶȣ���1M/s
	 */
	public static String getSpeed(long totalData,long beginTime,long endTime){
		int n = 0;
		if(endTime-beginTime==0) return "0B/s";
		long speed = totalData/(endTime-beginTime)*1000;
		//System.out.println("speed-->"+speed);
		//System.out.println("totalData-->"+totalData+"\nbegin-->"+beginTime+"\nend-->"+endTime);
		while(speed>1024){
			speed /=1024;
			n++;
		}
		if(n>4)
			n = 4;
		return ""+speed+v.get(n)+"/s";
	}
	
	/**
	 * ����1s���ڴ�����ٶ�
	 * @param totalData 1s�����ص����ֽ���
	 * @return ���ݴ�С���غ��ʵ��ٶȣ���1M/s
	 */
	public static String getSpeed(long totalData){
		int n = 0;
		long speed = totalData;
		while(speed>1024){
			speed /=1024;
			n++;
		}
		if(n>4)
			n = 4;
		return ""+speed+v.get(n)+"/s";
	}
}
