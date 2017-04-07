//JTable이 얹혀질 패널
package book;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class TablePanel extends JPanel{
	Connection con;
	JTable table;
	JScrollPane scroll;
	TableModel model;
	
	//Vector와 ArrayList는 둘다같다. 동기화 지원여부
	//ArrayList<Book> list=new Vector();
	
	Vector list=new Vector(); //2차원 벡터가 될예정
	Vector<String> columnName=new Vector<String>();
	//벡터는 동기화를 지원함 : 동시에 두개의 쓰레드가 접근하지 못하게 함
	//먼저 접근한게 쓰고 있으면 다른 자원이 접근못하는 기능이 내장됨
	//하지만 때문에 속도가 느림
	int cols;
	
	public TablePanel() {

		table=new JTable();
		scroll=new JScrollPane(table);
		this.setLayout(new BorderLayout());
		this.add(scroll);
		
		setBackground(Color.PINK);
		setPreferredSize(new Dimension(650, 600));
	}
	
	public void setConnection(Connection con){//생성자에서 호출할때 발생할 수 있는 타이밍 문제를 해결함
		this.con=con;
		
		init();
		
		//테이블 모델을 jtable에 적용
		model=new AbstractTableModel() {
			@Override
			public int getRowCount() {
				return list.size();
			}
			
			@Override
			public int getColumnCount() {
				return cols; //테이블은 고정도어 있기 때문에 컬럼수도 하드코하는 경우도 많음
			}
			
			@Override
			public Object getValueAt(int row, int col) {
				
				Vector vec=(Vector)list.get(row);
				return vec.elementAt(col);
			}
			
			@Override
			public String getColumnName(int col) {
		
				return columnName.elementAt(col);
			}
		};
		
		//테이블에 모델 적용
		table.setModel(model);
	
	}
	
	//book 테이블의 레코드 가져오기
	public void init(){
		String sql="select * from book order by book_id asc";
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			ResultSetMetaData meta=rs.getMetaData();
			cols=meta.getColumnCount();
			
			//기존 데이터 모두 지우기 
			list.removeAll(list);
			columnName.removeAll(columnName);
			
			for(int i=0;i<cols;i++){
				columnName.add(meta.getColumnName(i+1));
			}
			
			
			//rs의 정보를 컬렉션으로 DTO로 옮겨담자!!
			while(rs.next()){ //rs.next()에 의해 꼭대기로 올라온 커서가 한칸씩 내려감
				//그런데 rs는 한번에 담기 불편하니까 비어있는 공간을 Book형으로 만들자 
				Vector<String>data=new Vector<String>(); //1차원 배열
				//Book형으로 받으면 전체의 갯수를 알수가 없어서 String형으로 받아서 개수를 추산함. 이게 Arraylist를 안쓰고 Vector를 쓰는 이유임				
				data.add(Integer.toString(rs.getInt("book_id")));
				data.add(rs.getString("book_name"));
				data.add(rs.getString("img"));
				data.add(Integer.toString(rs.getInt("price")));
				data.add(Integer.toString(rs.getInt("subcategory_id")));
				
				list.add(data); //2차원 배열에 1차원 배여렝 넣음 
			}//while문으로 커서를 하나씩 내리자
			
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}//finally		
		
	}
}
