package book;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
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
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BookMain extends JFrame implements ItemListener, ActionListener{
	DBManager manager=DBManager.getInstance();
	Connection con;
	
	JPanel p_west;	//좌측 등록품
	JPanel p_content; //우측 영역 전체
	JPanel p_north;	//우측 선택 모드 영억
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
	
	public BookMain() {		
		//생성
		p_west = new JPanel();
		p_content = new JPanel(); 
		p_north = new JPanel();	
		p_table= new JPanel(); 
		p_grid = new JPanel();
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
		p_content.setPreferredSize(new Dimension(750, 600));
		p_north.setPreferredSize(new Dimension(750, 50));		
		p_table.setPreferredSize(new Dimension(750, 500));		
		p_grid.setPreferredSize(new Dimension(750, 500));		
		ch_top.setPreferredSize(new Dimension(130, 45));
		ch_sub.setPreferredSize(new Dimension(130, 45));
		
		//부착
		p_west.add(ch_top);
		p_west.add(ch_sub);
		p_west.add(t_name);
		p_west.add(t_price);
		p_west.add(can);
		p_west.add(bt_regist);
	
		p_north.add(ch_table);
		p_north.add(ch_grid);
		
		p_content.add(p_north, BorderLayout.NORTH);
		p_content.add(p_table);
		p_content.add(p_grid);
	
		add(p_west, BorderLayout.WEST);
		add(p_content);
		
		init();
		//리스너 설정
		ch_top.addItemListener(this);
		can.addMouseListener(new MouseAdapter() {//오버라이드할 메서드가 많아서 아답터를 내부익명하자
			@Override
			public void mouseClicked(MouseEvent e) {
				openFile();
			}
		});
		
	
		
		//윈도우 설정
		setSize(800, 600);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
	
	public void init(){
		//초이스 컴포넌트에 최상위 목록보이기
		con=manager.getConnect();
		String sql="select * from topcategory";
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
	}
	
	public void getSub(String v){
		//기존에 이미 체워진 아이템이 있다면 지우자. 
		ch_sub.removeAll();
		
		StringBuffer sb=new StringBuffer();
		sb.append("select * from subcategory");
		sb.append(" where topcategory_id=(");
		sb.append(" select topcategory_id from");
		sb.append(" topcategory where category_name='"+v+"')");
		
		//System.out.println(sb.toString());
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;

		try {
			pstmt=con.prepareStatement(sb.toString());
			rs=pstmt.executeQuery();
			while(rs.next()){
				ch_sub.add(rs.getString("category_name"));
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
		
	}
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		Choice ch=(Choice)e.getSource();
		getSub(ch.getSelectedItem());
	}
	
	//그림파일 불러오기
	public void openFile(){
		int result=chooser.showOpenDialog(this);
		if(result==JFileChooser.APPROVE_OPTION){
			//선택한 이미지를 canvas에 그릴것이다. 
			File file=chooser.getSelectedFile();
			img=kit.getImage(file.getAbsolutePath());
			can.repaint();
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj=e.getSource();
		System.out.println("나 눌렀어?");
	}

	public static void main(String[] args) {
		new BookMain();
	}



}
