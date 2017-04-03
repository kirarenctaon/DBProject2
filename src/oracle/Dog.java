//������ Ŭ������ �ν��ͽ��� ���� 1���� �����
/*
SingleTon Pattern ���� ����

JavaSE, 
JavaEE ��ޱ��(JavaSE�� �����Ͽ� ����� ���ø����̼� ���ۿ� ����)

�̱� ������ 4��, Gang of four  -->  90��� �� ���߼��� �Ⱓ
"Design Pattern"  --> 
 (	����	 ����  )
 
SingleTon : ��ü�� �ν��Ͻ��� ���� 1���� ����� ����
Command Pattern ��� 

*/



package oracle;

public class Dog {
	static private Dog instance;
	
	//new�� ���� ������ ����
	private Dog(){
		//private���� �����ڸ� ����� �ܺο����� ������� ���ο��� ���ѹ� ����
		//�̶� public Dog(){}���� �ø��� class�� Dog�� �ö󰡴� ����
	}
	
	//������ ���� ���� �޼��带 ���ؼ��� ����� �ϱ�
	static public Dog getInstance(){
		if(instance==null){
			instance=new Dog();//�̰� heap�� �ö�. �� �� �������� 
		}
		//single ton pattern 
		return instance; //������ static���������� �ν��Ͻ� �޸� ������ �Ⱥ���
	}
}
