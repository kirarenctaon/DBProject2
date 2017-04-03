//emp ���̺��� �����͸� ó���ϴ� ��Ʈ�ѷ�
package oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class TableModels extends AbstractTableModel {
	//�������̽��� �����ϴ� �޼��尡 �ʹ� ���Ƽ� ǥ�� �ּ� ����� ��, ��, �� �޼��� ������ �䱸�ϴ� AbstractTableModel�� ��ġ ������ ó�� �����
	
	PreparedStatement pstmt;
	ResultSet rs;

	String[] column; //�÷��� ���� �迭
	String[][] data; //�����͸� ���� 2�����迭 ����
	
	public TableModels(Connection con, String sql) { 
	    // 1.����̹��ε� 2.���� 3.���������� 4, ���Ӵݱ�
		try {
			 //3.���������� 4, ���Ӵݱ�
			if(con!=null){
				//�Ʒ��� pstmt�� ���� �����Ǵ� rs�� Ŀ���� �����ο� �� �ִ�. 
				pstmt=con.prepareStatement(sql, 
						ResultSet.TYPE_SCROLL_INSENSITIVE, 
						ResultSet.CONCUR_READ_ONLY);
				//��� ���� ��ȯ!
				rs=pstmt.executeQuery();
				
				//�÷��� ���غ���
				ResultSetMetaData meta=rs.getMetaData();
				int count=meta.getColumnCount();//�÷��� ����
				
				column=new String[count];//�÷��� ���� �迭�� �غ�
				//�÷����� ä����!
				for(int i=0;i<column.length;i++){
					column[i]=meta.getColumnName(i+1); //ù��° �÷��� 1�϶�� �����ϱ� ������ i+1�̶�� ����
				}
				
				rs.last(); //���� ���������� ����
				int total=rs.getRow();//���ڵ� ��ȣ ��ȯ
				rs.beforeFirst();//for�� ���� ���� �ٽ� ó������ ������
				
				//�� ���ڵ���� �˾�����, �������迭�� �����غ���!
				data=new String[total][column.length];
				
				//���ڵ带 ������ �迭�� data�� ä���ֱ�;

				for(int a=0;a<data.length;a++){//����
					rs.next();
					for(int i=0;i<data[a].length;i++){//ȣ��
						//data[��][ȣ]
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
			}*/ //con�� ������ â�� ���� �� �ѹ� �ݾƾ� �ϹǷ� disConnect()�� �θ��� �ʰڴ�. 
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
