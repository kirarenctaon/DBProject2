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
	
	JPanel p_west;	//���� ���ǰ
	JPanel p_content; //���� ���� ��ü
	JPanel p_north;	//���� ���� ��� ����
	JPanel p_center;// Flow�� ����Ǿ� p_table, p_grid�� ��� ������ѳ��� �����̳� ����
	JPanel p_table; //JTable�� �ٿ��� �г�
	JPanel p_grid; //�׸��� ������� ������ �г�
	Choice ch_top;
	Choice ch_sub;
	JTextField t_name;
	JTextField t_price;
	Canvas can;
	JButton bt_regist;
	CheckboxGroup group;
	Checkbox ch_table, ch_grid;
	Toolkit kit=Toolkit.getDefaultToolkit();//�̰͵� �ϳ��� �̱�������
	Image img;
	JFileChooser chooser;
	File file;
	
	//html option�� �ٸ��Ƿ�, Choice ������Ʈ�� ���� �̸� �޾Ƴ���
	//String[][] subcategory;  ArrayList�� ����. �� �÷����� rs��ü�� ��ü�� ���̴�.
	//�׷����� ��� ����? rs.last, re.getRow���� �ʿ��������. 
	ArrayList<SubCategory> subcategory=new ArrayList<SubCategory>();
	
	public BookMain() {		
		//����
		p_west = new JPanel();
		p_content = new JPanel(); 
		p_north = new JPanel();
		p_center = new JPanel();
		
		//p_table= new JPanel(); 
		//p_grid= new JPanel(); 
		p_table= new TablePanel(); //���� con�� ������ �������̶� Ÿ�ֹ̹����� �߻��� 
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
		
		bt_regist= new JButton("���");
		group= new CheckboxGroup();
		ch_table= new Checkbox("���̺� ����", group, false);
		ch_grid= new Checkbox("������ ����", group, false);
	
		//���� �����ø���
		chooser = new JFileChooser("C:/html_workspace/images");
		
		//����
		p_west.setPreferredSize(new Dimension(150, 600));
		p_content.setPreferredSize(new Dimension(650, 600));
		p_north.setPreferredSize(new Dimension(650, 50));		
		ch_top.setPreferredSize(new Dimension(130, 45));
		ch_sub.setPreferredSize(new Dimension(130, 45));
		
		p_center.setBackground(Color.YELLOW);
		
		//����
		p_west.add(ch_top);
		p_west.add(ch_sub);
		p_west.add(t_name);
		p_west.add(t_price);
		p_west.add(can);
		p_west.add(bt_regist);
	
		p_north.add(ch_table); // ���� ���̺�� ���鶧 �������̾ƿ��� �� ����
		p_north.add(ch_grid); // ���� ���̺�� ���鶧 �������̾ƿ��� �� ����
		
		p_center.add(p_table);
		p_center.add(p_grid);

		p_content.add(p_north, BorderLayout.NORTH);
		p_content.add(p_center);

		add(p_west, BorderLayout.WEST);
		add(p_content);
		
		init();
		
		//������ ����
		ch_top.addItemListener(this);
		can.addMouseListener(new MouseAdapter() {//�������̵��� �޼��尡 ���Ƽ� �ƴ��͸� �����͸�����
			@Override
			public void mouseClicked(MouseEvent e) {
				openFile();
			}
		});
		
		bt_regist.addActionListener(this);
		
		ch_table.addItemListener(this); //���̽� ������Ʈ�� ����
		ch_grid.addItemListener(this);
		
		//������ ����
		pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}
	
	public void init(){
		//���̽� ������Ʈ�� �ֻ��� ��Ϻ��̱�
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
		//���̺� �гΰ� �׸��� �гο��� Conncection ����
		((TablePanel)p_table).setConnection(con); //�ڽ��� �ڷ������� �����ؼ� �־�����. 
		((GridPanel)p_grid).setConnection(con);
	}
	
	//���� ī�װ� ��������
	public void getSub(String v){
		//������ �̹� ü���� �������� �ִٸ� ������. 
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
			}*/ //����ī�׺����� ������ 2�����迭�� ���+���
			// subcategory=new String[���ڵ��][�÷���]
			
			//rs�� ����� ���ڵ� 1���� SubCategoryŬ���� �ν��Ͻ�1���� ����.
			
			
			//����!!
			while(rs.next()){
				SubCategory dto=new SubCategory();
				dto.setSubcategory_id(rs.getInt("subcategory_id"));
				dto.setCategory_name(rs.getString("category_name"));
				dto.setTopcategory_id(rs.getInt("topcategory_id"));
				
				subcategory.add(dto);//�÷��ǿ� ���.
				System.out.println();
				//���ķ� rs�� ������ �����Ƿ� ������ ��
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
	
	//��ǰ ��� �޼���
	public void regist(){
		//���� ���� ������ ���� ī�װ� ���̽��� index�� ���ؼ�, �� index�� ArrayList�� �����Ͽ�
		//��ü�� ��ȯ������ ������ �����ϰ� �� �� �ִ�. 
		int index=ch_sub.getSelectedIndex();
		SubCategory dto=subcategory.get(index);
		
		String book_name=t_name.getText();//å�̸�
		int price=Integer.parseInt(t_price.getText()); //String���� �ص� ��������� ����� ���ɼ��� ������ �����ϸ� �ڷ����� ��Ȯ�� ���������
		String img=file.getName();//���ϸ�
		
		StringBuffer sb=new StringBuffer();
		//����Ŭ ��ü������ �̹��� ������ ���������� ����뷮�� ���� ������ ���Ƿ� ��θ� ��������
		sb.append("insert into book(book_id, subcategory_id, book_name, price, img)");
		sb.append(" values(seq_book.nextval,"+dto.getSubcategory_id()+",'"+book_name+"', "+price+",'"+img+"')");
		
		System.out.println(sb.toString());
		
		PreparedStatement pstmt=null;
		try {
			pstmt=con.prepareStatement(sb.toString());
			int result=pstmt.executeUpdate(); //SQL���� DML(insert, delect, update) �϶��� executeUpdate()���
			
			//���� �޼���� ���ڰ� ��ȯ�ϸ�, �� ���ڰ��� �� ������ ���� ������ �޴� ���ڵ� ���� ��ȯ�Ѵ�. 
			//insert�� ��� ������ 1�� ��ȯ�ȴ�. 
			
			if(result!=0){
				//insert�� ������ ��� �׸��� copy()����
				copy();
				
				((TablePanel)p_table).init();
				((TablePanel)p_table).table.updateUI();
				
				((GridPanel)p_grid).setConnection(con);
			//	((GridPanel)p_grid).init();
				((GridPanel)p_grid).updateUI();
				
			}else{
				System.out.println(book_name+"��� ����");
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
	
	//�׸����� �ҷ�����
	public void openFile(){
		int result=chooser.showOpenDialog(this);
		if(result==JFileChooser.APPROVE_OPTION){
			//������ �̹����� canvas�� �׸����̴�. 
			file=chooser.getSelectedFile();
			img=kit.getImage(file.getAbsolutePath());
			can.repaint();
		}
	}
	
	//�̹��� �����ϱ�  ������ ������ �̹����� �����ڰ� ������ ��Ģ�� ���縦 �س���
	public void copy(){
		FileInputStream fis=null;
		FileOutputStream fos=null;	
		
		String despath="C:/java_workspace/DBProject2/data/"+file.getName();
		
		try {
			fis = new FileInputStream(file);
			fos = new FileOutputStream(despath);
			byte[] b = new byte[1024];
			int data;//�о���� �����Ͱ� �ƴ϶�, ������ ��� ����, data�� �ִ� ���ٸ� �Ǵ��ϰ� ���� �����ʹ� byte�� ��� ���� 
			while(true){		
				/*
				data = fis.read();//1����Ʈ �о�帲
				if(data==-1)break;
				fos.write(data);//1����Ʈ ������*/
				data = fis.read(b);
				if(data==-1)break;
				fos.write(b);
			}
			JOptionPane.showMessageDialog(this, "��� �Ϸ�");
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
