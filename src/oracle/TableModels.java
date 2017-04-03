//emp 테이블의 데이터를 처리하는 컨트롤러
package oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class TableModels extends AbstractTableModel {
	//인터페이스는 강제하는 메서드가 너무 많아서 표의 최소 요건인 행, 열, 값 메서드 구현만 요구하는 AbstractTableModel을 마치 어탭터 처럼 사용함
	
	PreparedStatement pstmt;
	ResultSet rs;

	String[] column; //컬럼을 넣을 배열
	String[][] data; //데이터를 담을 2차원배열 새성
	
	public TableModels(Connection con, String sql) { 
	    // 1.드라이버로드 2.접속 3.쿼리문수행 4, 접속닫기
		try {
			 //3.쿼리문수행 4, 접속닫기
			if(con!=null){
				//아래의 pstmt에 의해 생성되는 rs는 커서가 자유로울 수 있다. 
				pstmt=con.prepareStatement(sql, 
						ResultSet.TYPE_SCROLL_INSENSITIVE, 
						ResultSet.CONCUR_READ_ONLY);
				//결과 집합 반환!
				rs=pstmt.executeQuery();
				
				//컬럼을 구해보자
				ResultSetMetaData meta=rs.getMetaData();
				int count=meta.getColumnCount();//컬럼의 갯수
				
				column=new String[count];//컬럼을 담을 배열을 준비
				//컬럼명을 채우자!
				for(int i=0;i<column.length;i++){
					column[i]=meta.getColumnName(i+1); //첫번째 컬럼은 1일라고 생각하기 때문에 i+1이라고 하자
				}
				
				rs.last(); //제일 마지막으로 보냄
				int total=rs.getRow();//레코드 번호 반환
				rs.beforeFirst();//for문 돌기 위해 다시 처음으로 가져옴
				
				//총 레코드수를 알았으니, 이차원배열을 생성해보자!
				data=new String[total][column.length];
				
				//레코드를 이차원 배열은 data에 채워넣기;

				for(int a=0;a<data.length;a++){//층수
					rs.next();
					for(int i=0;i<data[a].length;i++){//호수
						//data[층][호]
						data[a][i]=rs.getString(column[i]);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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
			/*
			if(con!=null){
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}*/ //con은 윈도우 창이 닫을 때 한번 닫아야 하므로 disConnect()를 부르지 않겠다. 
		}
	}
		
	@Override
	public int getColumnCount() {
		return column.length;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	} 
	
	@Override
	public String getColumnName(int column) {
		return this.column[column];
	}
	
}
