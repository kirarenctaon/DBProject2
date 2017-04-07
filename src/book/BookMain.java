package book;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BookMain extends JFrame implements ItemListener, ActionListener{
	DBManager manager=DBManager.getInstance();
	Connection con;
	
	JPanel p_west;	//좌측 등록품
	JPanel p_content; //우측 영역 전체
	JPanel p_north;	//우측 선택 모드 영억
	JPanel p_center;// Flow가 적용되어 p_table, p_grid를 모두 존재시켜놓을 컨테이너 역할
	JPanel p_table; //JTable이 붙여질 패널
	JPanel p_grid; //그리드 방식으로 보여질 패널
	Choice ch_top;
	Choice ch_sub;
	JTextField t_name;
	JTextField t_price;
	Canvas can;
	JButton bt_regist;
	CheckboxGroup group;
	Checkbox ch_table, ch_grid;
	Toolkit kit=Toolkit.getDefaultToolkit();//이것도 하나의 싱글톤기법임
	Image img;
	JFileChooser chooser;
	File file;
	
	//html option과 다르므로, Choice 컴포넌트의 값을 미리 받아놓자
	//String[][] subcategory;  ArrayList로 가자. 이 컬렉션은 rs객체를 대체할 것이다.
	//그럼으로 얻는 장점? rs.last, re.getRow등이 필요없어진다. 
	ArrayList<SubCategory> subcategory=new ArrayList<SubCategory>();
	
	public BookMain() {		
		//생성
		p_west = new JPanel();
		p_content = new JPanel(); 
		p_north = new JPanel();
		p_center = new JPanel();
		
		//p_table= new JPanel(); 
		//p_grid= new JPanel(); 
		p_table= new TablePanel(); //지금 con을 넣으면 생성전이라 타이밍문제가 발생함 
		p_grid = new GridPanel();
		
		ch_top = new Choice();
		ch_sub  = new Choice();
		t_name= new JTextField(12);
		t_price= new JTextField(12);
		
		URL url=this.getClass().getResource("/enemy1.png");
		try {
			img=ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		can=new Canvas(){
			@Override
			public void paint(Graphics g) {
				g.drawImage(img, 0, 0, 140, 140, this);
			}
		};
		can.setPreferredSize(new Dimension(150, 150));
		
		bt_regist= new JButton("등록");
		group= new CheckboxGroup();
		ch_table= new Checkbox("테이블 형식", group, false);
		ch_grid= new Checkbox("갤러리 형식", group, false);
	
		//파일 추져올리기
		chooser = new JFileChooser("C:/html_workspace/images");
		
		//설정
		p_west.setPreferredSize(new Dimension(150, 600));
		p_content.setPreferredSize(new Dimension(650, 600));
		p_north.setPreferredSize(new Dimension(650, 50));		
		ch_top.setPreferredSize(new Dimension(130, 45));
		ch_sub.setPreferredSize(new Dimension(130, 45));
		
		p_center.setBackground(Color.YELLOW);
		
		//부착
		p_west.add(ch_top);
		p_west.add(ch_sub);
		p_west.add(t_name);
		p_west.add(t_price);
		p_west.add(can);
		p_west.add(bt_regist);
	
		p_north.add(ch_table); // 각각 테이블로 만들때 보더레이아웃을 줄 예정
		p_north.add(ch_grid); // 각각 테이블로 만들때 보더레이아웃을 줄 예정
		
		p_center.add(p_table);
		p_center.add(p_grid);

		p_content.add(p_north, BorderLayout.NORTH);
		p_content.add(p_center);

		add(p_west, BorderLayout.WEST);
		add(p_content);
		
		init();
		
		//리스너 연결
		ch_top.addItemListener(this);
		can.addMouseListener(new MouseAdapter() {//오버라이드할 메서드가 많아서 아답터를 내부익명하자
			@Override
			public void mouseClicked(MouseEvent e) {
				openFile();
			}
		});
		
		bt_regist.addActionListener(this);
		
		ch_table.addItemListener(this); //초이스 컴포넌트와 연걸
		ch_grid.addItemListener(this);
		
		//윈도우 설정
		pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
	
	public void init(){
		//초이스 컴포넌트에 최상위 목록보이기
		con=manager.getConnect();
		String sql="select * from topcategory order by topcategory_id asc";
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
		
			while(rs.next()){
				ch_top.add(rs.getString("category_name"));
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
		}
		//테이블 패널과 그리드 패널에게 Conncection 전달
		((TablePanel)p_table).setConnection(con); //자식의 자료형으로 변신해서 넣어주자. 
		((GridPanel)p_grid).setConnection(con);
	}
	
	//하위 카테고리 가져오기
	public void getSub(String v){
		//기존에 이미 체워진 아이템이 있다면 지우자. 
		ch_sub.removeAll();
		
		StringBuffer sb=new StringBuffer();
		sb.append("select * from subcategory");
		sb.append(" where topcategory_id=(");
		sb.append(" select topcategory_id from");
		sb.append(" topcategory where category_name='"+v+"') order by subcategory_id asc");
		
		//System.out.println(sb.toString());
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		try {
			pstmt=con.prepareStatement(sb.toString());
			rs=pstmt.executeQuery();
			
			/*while(rs.next()){
				ch_sub.add(rs.getString("category_name"));
			}*/ //서브카테보리의 정보를 2차원배열에 담기+출력
			// subcategory=new String[레코드수][컬럼수]
			
			//rs에 담겨진 레코드 1개는 SubCategory클래스 인스턴스1개로 받자.
			
			
			//맵핑!!
			while(rs.next()){
				SubCategory dto=new SubCategory();
				dto.setSubcategory_id(rs.getInt("subcategory_id"));
				dto.setCategory_name(rs.getString("category_name"));
				dto.setTopcategory_id(rs.getInt("topcategory_id"));
				
				subcategory.add(dto);//컬렉션에 담기.
				System.out.println();
				//이후로 rs는 사용될일 없으므로 버려도 됨
				ch_sub.add(dto.getCategory_name());
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
		}//finally				
	}
	
	//상품 등록 메서드
	public void regist(){
		//내가 지금 선택한 서브 카테고리 초이스의 index를 구해서, 그 index로 ArrayList를 접근하여
		//객체를 반환받으면 정보를 유용하게 쓸 수 있다. 
		int index=ch_sub.getSelectedIndex();
		SubCategory dto=subcategory.get(index);
		
		String book_name=t_name.getText();//책이름
		int price=Integer.parseInt(t_price.getText()); //String으로 해도 상관없으나 계산이 사용될수도 있으니 가능하면 자료형을 명확히 지켜줘야함
		String img=file.getName();//파일명
		
		StringBuffer sb=new StringBuffer();
		//오라클 자체적으로 이미지 저장이 가능하지만 저장용량에 따라 가격이 들어가므로 경로만 저장하자
		sb.append("insert into book(book_id, subcategory_id, book_name, price, img)");
		sb.append(" values(seq_book.nextval,"+dto.getSubcategory_id()+",'"+book_name+"', "+price+",'"+img+"')");
		
		System.out.println(sb.toString());
		
		PreparedStatement pstmt=null;
		try {
			pstmt=con.prepareStatement(sb.toString());
			int result=pstmt.executeUpdate(); //SQL문이 DML(insert, delect, update) 일때는 executeUpdate()사용
			
			//위의 메서드는 숫자값 반환하며, 이 숫자값은 이 쿼리에 의해 영향을 받는 레코드 수를 반환한다. 
			//insert의 경우 언제나 1이 반환된다. 
			
			if(result!=0){
				//insert가 성공한 경우 그림을 copy()하자
				copy();
				
				((TablePanel)p_table).init();
				((TablePanel)p_table).table.updateUI();
				
				((GridPanel)p_grid).setConnection(con);
			//	((GridPanel)p_grid).init();
				((GridPanel)p_grid).updateUI();
				
			}else{
				System.out.println(book_name+"등록 실패");
			} 		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		}
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		Object obj=e.getSource();
		if(obj==ch_top){
			Choice ch=(Choice)e.getSource();
			getSub(ch.getSelectedItem());
		}else if(obj==ch_table){
			p_table.setVisible(true);
			p_grid.setVisible(false);
		}else if(obj==ch_grid){
			p_table.setVisible(false);
			p_grid.setVisible(true);
		}
	}
	
	//그림파일 불러오기
	public void openFile(){
		int result=chooser.showOpenDialog(this);
		if(result==JFileChooser.APPROVE_OPTION){
			//선택한 이미지를 canvas에 그릴것이다. 
			file=chooser.getSelectedFile();
			img=kit.getImage(file.getAbsolutePath());
			can.repaint();
		}
	}
	
	//이미지 복사하기  유저가 선택한 이미지를 개발자가 지정한 위칙로 복사를 해놓자
	public void copy(){
		FileInputStream fis=null;
		FileOutputStream fos=null;	
		
		String despath="C:/java_workspace/DBProject2/data/"+file.getName();
		
		try {
			fis = new FileInputStream(file);
			fos = new FileOutputStream(despath);
			byte[] b = new byte[1024];
			int data;//읽어들인 데이터가 아니라, 갯수만 들어 있음, data는 있다 없다만 판단하고 실제 데이터는 byte에 들어 있음 
			while(true){		
				/*
				data = fis.read();//1바이트 읽어드림
				if(data==-1)break;
				fos.write(data);//1바이트 복사함*/
				data = fis.read(b);
				if(data==-1)break;
				fos.write(b);
			}
			JOptionPane.showMessageDialog(this, "등록 완료");
		} catch (FileNotFoundException e) {		
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
		
	}
		
	@Override
	public void actionPerformed(ActionEvent e) {
		regist();
	}

	public static void main(String[] args) {
		new BookMain();
	}



}
