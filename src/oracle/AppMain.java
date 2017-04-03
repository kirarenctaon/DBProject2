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
	Connection con;//모든 객체간 공유하기 위해 AppMain에서 관리
	ConnectionManager manager;
	
	JTable table;
	JScrollPane scroll;
	JPanel p_west, p_center;
	Choice choice;
	String[][] item={
			{"▼테이블을 선택하세요.",""}, 
			{"사원테이블","emp"},
			{"부서테이블","dept"},
	};
	
	TableModel[] model=new TableModel[item.length]; //인터페이스를 올린게 아니라 올릴 자료형을 확보해 자료형으로 올린거
	
	public AppMain() {
/*		
디자인과 로직을 분리시키기 위해 중간자(controller)가 의 존재가 필요하다
JTable에서는 이 컨트롤러의 역할을 TableModel이 해준다. 
TabelModel을 사용할 경우, JTable은 자신이 보여줘야할 데이터를 JTableModel로 부터 정보를 얻어와 출력한다. 

getColumnCount() //컬럼의 갯수를 반환하는 매서드
getRowCount()  //레코드의 갯수를 반환하는 매서드
getValueAt(int row, int col)  //특정 위치의 값을 반환하는 메서드

이 함수들의 호출 주체는 JTable!!	
*/		
		//앱이 가동될때 딱한번 접속이 일어나게 함 
		manager = ConnectionManager.getInstance();//아무리 많이 호출해도 if문에 의해 인서턴스는 한번만 호출된다. 
		con=manager.getConncetion();
		
		
		table=new JTable(3, 2);
		scroll=new JScrollPane(table);
		p_west = new JPanel();
		p_center=new JPanel();
		choice = new Choice();
		
		//테이블 모델을 올려놓자.
		model[0] = new DefaultTableModel();
		model[1] = new TableModels(con, "select * from emp");
		model[2] = new TableModels(con, "select * from dept");
		
		//초이스 구성,
		/*
		choice.add("--테이블 선택--");
		choice.add("사원테이블"); //select * from emp;
		choice.add("부서테이블"); //select * from dept;
		
		JavaScript의  Select - Option 기능을 자바로 구현하기 위해 text, value가 있는 이차원배열을 이용하도록 하자
		*/
		for(int i=0;i<item.length;i++){
			choice.add(item[i][0]);
		}
				
		/* 테이블에 데이터 넣기??
		 이렇게 하면 옷을 몸에 꿰멘 꼴. (3, 2)테이블로 고정해서 부착해버림, 원소스 하드코딩
		 외부사항에 변경될 수 있는 유지보수가 힘든 로직 -> 디자인과 로직이 섞여 있음
		table.setValueAt("사과", 0, 0);
		table.setValueAt("배", 0, 1);
		
		table.setValueAt("장미", 1, 0);
		table.setValueAt("튤립", 1, 1);
		
		table.setValueAt("잉어", 2, 0);
		table.setValueAt("붕어", 2, 1);
		
		디자인만 빼고 로직을 분리시켜라!!
		*/
		
		p_west.add(choice);//west영역의 패널에 초이스 부탁
		p_center.add(scroll);
		
		add(p_west, BorderLayout.WEST);
		add(p_center);
		
		pack();
		
		//초이스와 리스너 연결
		choice.addItemListener(this);
		
		//윈도우창 닫을 때 오라클 접속 끊기
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//커넥션 닫기
				manager.disConnect(con); 
				//프로그램 종료
				System.exit(0);
				
				//setDefaultCloseOperation(EXIT_ON_CLOSE); 을 직접 구현함
			}
		});
		
		setVisible(true);
		//setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void showData(int index){
		System.out.println("당신이 보게될 테이블은 :"+item[index][1]);
		table.setModel(model[index]);
		/*
		 해당되는 테이블 모델을 사용하면 된다.
		 emp->EmpModel
		 dept->DeptModel
		 아무것도 아니면 default -> DefaultTableModel
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
