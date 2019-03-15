package DataType;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassAnswer {
	Test test;
	Classes classes;
	List<Answer> answer;
	public Test getTest() {
		return test;
	}
	public void setTest(Test test) {
		this.test = test;
	}
	public Classes getClasses() {
		return classes;
	}
	public void setClasses(Classes classes) {
		this.classes = classes;
	}
	public List<Answer> getAnswer() {
		return answer;
	}
	public void setAnswer(List<Answer> answer) {
		this.answer = answer;
	}
	public String getStatus(int qid){
		Question q=test.getQuestions().get(qid);
		int num=classes.getStudents().size();
		List<Answer> temp=new ArrayList<Answer>();
		for(int i=0;i<answer.size();i++) {
			if (answer.get(i).getQuestion().getId().equals(q.getId())) {
				temp.add(answer.get(i));
			}
		}
		int counter[]=new int[classes.getStudents().size()];//最多的五种答案计数器
		String mostAnswer[]=new String[classes.getStudents().size()];//五种答案记录器
		for(int i=0;i<classes.getStudents().size();i++){counter[i]=0;mostAnswer[i]="";}
		double average=0.0;
		for(int i=0;i<temp.size();i++){
			//遍历答案,找出出现最多的答案并计算平均分
			for(int j=0;j<num;j++){
				if(mostAnswer[j].equals("")){
					mostAnswer[j]=temp.get(i).getQuestion().getAnswer();//若此位置为空,插入此答案
					counter[j]++;
					break;
				}
				if(mostAnswer[j].equals(temp.get(i).getQuestion().getAnswer())){
					counter[j]++;//已有则自增
					break;
				}
			}
			average+=temp.get(i).getQuestion().getScore();
		}
		average/=num;

		String result="答案统计\n";
		DecimalFormat df = new DecimalFormat("#.00");
		for(int i=0;i<mostAnswer.length;i++)
		{
			if(counter[i]==0)continue;
			if(i>=4)break;//最多四个

			result += "答案" + mostAnswer[i] + " 人数" + counter[i] + " 比例" + df.format(counter[i] * 100 / num) + "%\n";

		}
		result+="平均分"+df.format(average);

		return result;
	}


}
