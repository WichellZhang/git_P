package zx;




import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;


public class TEST1 extends JFrame{
JTextField txtzhujiip=new JTextField();
JTextField txtyuanip=new JTextField();
JTextField txtmiwen=new JTextField();
JPasswordField txtjiexi=new JPasswordField();
JTextField txtfanmi=new JTextField();
JTextField txtfanmin=new JTextField();
JButton bl=new JButton("��¼");
JButton bg=new JButton("�ر�");

//�����޲ι���������Ҫ�ķ������ڹ�������,Ȼ����main���������
public TEST1(){
	bl.setVisible(false);
	bg.setVisible(false);
setBounds(30,25,300,300);
Container c = getContentPane();
c.setLayout(new GridLayout(4,2,10,10));
c.add(new JLabel("����IP"));
c.add(txtzhujiip);
//txtzhujiip.setText("test");
c.add(new JLabel("ԴIP"));
c.add(txtyuanip);
c.add(new JLabel("ԴIP����"));
c.add(txtmiwen);
c.add(new JLabel("ԴIP���Ľ���"));
c.add(txtjiexi);
c.add(new JLabel("������������"));
c.add(txtfanmi);
c.add(new JLabel("������������"));
c.add(txtfanmin);
c.add(bl);
c.add(bg);
setDefaultCloseOperation(EXIT_ON_CLOSE);
setVisible(true);
//ע�⣺�˴��������ڲ���
bg.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e) {
// TODO Auto-generated method stub
System.exit(0);
}
});

              //ע�⣺�˴��������ڲ���
bl.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e) {
// TODO Auto-generated method stub
String name = txtzhujiip.getText();
String pass = txtyuanip.getText();
if(name.equals("tom")&&pass.equals("123")){
System.out.println("��½�ɹ�");
}else{
System.out.println("��¼ʧ��");
}
}

});

}
public static void main(String[] args) {
new TEST1();
}
}