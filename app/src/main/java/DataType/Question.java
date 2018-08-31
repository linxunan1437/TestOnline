package DataType;

public class Question {
	private String question,answer,type;//type��ѡ����,�͹������,����������
	private String id;
	private int score;//Ϊ��ʱ����,���Ծ�ͬ����ͬ
	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getId() {
		return id;
	}

	public String getQuestion() {
		return question;
	}
	public Question(String answer,int score){this.answer=answer;this.score=score;}
	public Question(String id,String question, String answer, String type,int score) {
		super();
		this.question = question;
		this.answer = answer;
		this.type = type;
		this.id = id;
		this.score=score;
	}
	public void setId(String id) {
		this.id = id;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}


	
	
}
