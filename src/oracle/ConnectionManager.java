/* 각 TableModel 마다 접속 정보와 접속객체를 두게 되면, 
접속정보가 바뀔때 모든 클래스의 코드도 수정해야하는 유지보수상의 문제 뿐만 아니라
TableModel마다 Connection을 생성하기 때문에 접속이 여러개 발생한다. 
하나의 어플리케이션이 오라클과 맺는 접속은 1개만으로도 충분하다. 

또한 접속이 여러개이면 하나의 세션이 발생시키는 각종 DML작업이 통일되지 못하게 된다. 
즉 다른 사람으로 인식하게 된다. 

--> 오늘 수업에서는 일단 emp에서 알아서 닫기 때문에 데이터누수의 문제는 없지만
접속코드가 반복되므로 분리하는 걸로 하자!!!

객체의 인스턴스를 메모리힙에 1개만 만드는 방법


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
	
	/*개발자가 제공하는 방법이외의 접근을 아예 차단하자
	 * 사용자에 의한 임의 생성을 막자. 즉 new 막자*/
	private ConnectionManager() {
		try {
			// 1.드라이버로드
			Class.forName(driver);
		  //	2.접속 
			con=DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	//인스턴스의 생성없이도, 외부에서 메서드를 호출하여 이 객체의 인스턴스를 가져갈 수 있도록
	//getter를 제공해주자
	static public ConnectionManager getInstance(){
		if(null==instance){
			instance = new ConnectionManager();
		}
		return instance;
	}
	
	//커낵션 객체를 하나만 만들어 공유
	//이 메서드 호자는 Connection 객체를 반환받게 된다. 
	public Connection getConncetion(){
		return con;
	}
	
	//커넥션을 다 사용후 닫기
	public void disConnect(Connection con){
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
