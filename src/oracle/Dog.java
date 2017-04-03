//강아지 클래스의 인스터스를 오직 1개만 만들기
/*
SingleTon Pattern 개발 패턴

JavaSE, 
JavaEE 고급기술(JavaSE를 포함하여 기업용 어플리케이션 제작에 사용됨)

미국 개발자 4명, Gang of four  -->  90년대 초 개발서적 출간
"Design Pattern"  --> 
 (	설계	 습관  )
 
SingleTon : 객체의 인스턴스를 오직 1개만 만드는 패턴
Command Pattern 등등 

*/



package oracle;

public class Dog {
	static private Dog instance;
	
	//new에 의한 생성을 막자
	private Dog(){
		//private으로 생성자를 만들면 외부에서는 못만들고 내부에서 딱한번 만듬
		//이때 public Dog(){}으로 올리면 class의 Dog이 올라가는 거임
	}
	
	//생성에 의해 막고 메서드를 통해서만 만들게 하기
	static public Dog getInstance(){
		if(instance==null){
			instance=new Dog();//이건 heap에 올라감. 이 이 강아지는 
		}
		//single ton pattern 
		return instance; //하지만 static영역에서는 인스턴스 메모리 영역이 안보이
	}
}
