/* �� TableModel ���� ���� ������ ���Ӱ�ü�� �ΰ� �Ǹ�, 
���������� �ٲ� ��� Ŭ������ �ڵ嵵 �����ؾ��ϴ� ������������ ���� �Ӹ� �ƴ϶�
TableModel���� Connection�� �����ϱ� ������ ������ ������ �߻��Ѵ�. 
�ϳ��� ���ø����̼��� ����Ŭ�� �δ� ������ 1�������ε� ����ϴ�. 

���� ������ �������̸� �ϳ��� ������ �߻���Ű�� ���� DML�۾��� ���ϵ��� ���ϰ� �ȴ�. 
�� �ٸ� ������� �ν��ϰ� �ȴ�. 

--> ���� ���������� �ϴ� emp���� �˾Ƽ� �ݱ� ������ �����ʹ����� ������ ������
�����ڵ尡 �ݺ��ǹǷ� �и��ϴ� �ɷ� ����!!!

��ü�� �ν��Ͻ��� �޸����� 1���� ����� ���


*/

package oracle;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
	static private ConnectionManager instance;
	
	String driver="oracle.jdbc.driver.OracleDriver";
	String url="jdbc:oracle:thin:@localhost:1521:XE";
	String user="batman";
	String password="1234";
	
	Connection con;
	
	/*�����ڰ� �����ϴ� ����̿��� ������ �ƿ� ��������
	 * ����ڿ� ���� ���� ������ ����. �� new ����*/
	private ConnectionManager() {
		try {
			// 1.����̹��ε�
			Class.forName(driver);
		  //	2.���� 
			con=DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	//�ν��Ͻ��� �������̵�, �ܺο��� �޼��带 ȣ���Ͽ� �� ��ü�� �ν��Ͻ��� ������ �� �ֵ���
	//getter�� ����������
	static public ConnectionManager getInstance(){
		if(null==instance){
			instance = new ConnectionManager();
		}
		return instance;
	}
	
	//Ŀ���� ��ü�� �ϳ��� ����� ����
	//�� �޼��� ȣ�ڴ� Connection ��ü�� ��ȯ�ް� �ȴ�. 
	public Connection getConncetion(){
		return con;
	}
	
	//Ŀ�ؼ��� �� ����� �ݱ�
	public void disConnect(Connection con){
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
