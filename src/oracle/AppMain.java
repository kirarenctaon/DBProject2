package oracle;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class AppMain extends JFrame implements ItemListener{
	Connection con;//��� ��ü�� �����ϱ� ���� AppMain���� ����
	ConnectionManager manager;
	
	JTable table;
	JScrollPane scroll;
	JPanel p_west, p_center;
	Choice choice;
	String[][] item={
			{"�����̺��� �����ϼ���.",""}, 
			{"������̺�","emp"},
			{"�μ����̺�","dept"},
	};
	
	TableModel[] model=new TableModel[item.length]; //�������̽��� �ø��� �ƴ϶� �ø� �ڷ����� Ȯ���� �ڷ������� �ø���
	
	public AppMain() {
/*		
�����ΰ� ������ �и���Ű�� ���� �߰���(controller)�� �� ���簡 �ʿ��ϴ�
JTable������ �� ��Ʈ�ѷ��� ������ TableModel�� ���ش�. 
TabelModel�� ����� ���, JTable�� �ڽ��� ��������� �����͸� JTableModel�� ���� ������ ���� ����Ѵ�. 

getColumnCount() //�÷��� ������ ��ȯ�ϴ� �ż���
getRowCount()  //���ڵ��� ������ ��ȯ�ϴ� �ż���
getValueAt(int row, int col)  //Ư�� ��ġ�� ���� ��ȯ�ϴ� �޼���

�� �Լ����� ȣ�� ��ü�� JTable!!	
*/		
		//���� �����ɶ� ���ѹ� ������ �Ͼ�� �� 
		manager = ConnectionManager.getInstance();//�ƹ��� ���� ȣ���ص� if���� ���� �μ��Ͻ��� �ѹ��� ȣ��ȴ�. 
		con=manager.getConncetion();
		
		
		table=new JTable(3, 2);
		scroll=new JScrollPane(table);
		p_west = new JPanel();
		p_center=new JPanel();
		choice = new Choice();
		
		//���̺� ���� �÷�����.
		model[0] = new DefaultTableModel();
		model[1] = new TableModels(con, "select * from emp");
		model[2] = new TableModels(con, "select * from dept");
		
		//���̽� ����,
		/*
		choice.add("--���̺� ����--");
		choice.add("������̺�"); //select * from emp;
		choice.add("�μ����̺�"); //select * from dept;
		
		JavaScript��  Select - Option ����� �ڹٷ� �����ϱ� ���� text, value�� �ִ� �������迭�� �̿��ϵ��� ����
		*/
		for(int i=0;i<item.length;i++){
			choice.add(item[i][0]);
		}
				
		/* ���̺� ������ �ֱ�??
		 �̷��� �ϸ� ���� ���� ��� ��. (3, 2)���̺�� �����ؼ� �����ع���, ���ҽ� �ϵ��ڵ�
		 �ܺλ��׿� ����� �� �ִ� ���������� ���� ���� -> �����ΰ� ������ ���� ����
		table.setValueAt("���", 0, 0);
		table.setValueAt("��", 0, 1);
		
		table.setValueAt("���", 1, 0);
		table.setValueAt("ƫ��", 1, 1);
		
		table.setValueAt("�׾�", 2, 0);
		table.setValueAt("�ؾ�", 2, 1);
		
		�����θ� ���� ������ �и����Ѷ�!!
		*/
		
		p_west.add(choice);//west������ �гο� ���̽� ��Ź
		p_center.add(scroll);
		
		add(p_west, BorderLayout.WEST);
		add(p_center);
		
		pack();
		
		//���̽��� ������ ����
		choice.addItemListener(this);
		
		//������â ���� �� ����Ŭ ���� ����
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//Ŀ�ؼ� �ݱ�
				manager.disConnect(con); 
				//���α׷� ����
				System.exit(0);
				
				//setDefaultCloseOperation(EXIT_ON_CLOSE); �� ���� ������
			}
		});
		
		setVisible(true);
		//setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void showData(int index){
		System.out.println("����� ���Ե� ���̺��� :"+item[index][1]);
		table.setModel(model[index]);
		/*
		 �ش�Ǵ� ���̺� ���� ����ϸ� �ȴ�.
		 emp->EmpModel
		 dept->DeptModel
		 �ƹ��͵� �ƴϸ� default -> DefaultTableModel
		  */
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		
		int index=ch.getSelectedIndex();
		showData(index);
	}
	
	public static void main(String[] args) {
		new AppMain();
	}
}
