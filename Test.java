/**
 * Created by weisuzhong on 2017/6/8.
 */

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Pattern;

class Kuohao {
    public int index;
    public char c;
    Kuohao(int t, char k) {
        index = t;
        c = k;
    }
}

public class Test {

    //检查表达式输入是否合法
    public boolean JudgeLegal(String s){
        for(int i = 0 ; i < s.length()-1; i++){
            if(JudgeFH(s.charAt(i)) && JudgeFH(s.charAt(i-1))){
                System.out.println("表达式输入非法!");
                return false;
            }
        }
        int left = 0;
        int right = 0;
        for(int i = 0 ; i < s.length() ; i++){
            if(s.charAt(i) == '(')
                left++;
            if(s.charAt(i) == ')')
                right++;
        }
        if(left != right){
            System.out.println("表达式输入非法!");
            return false;
        }
        System.out.print("表达式输入合法!\n结果为:");
        return true;
    }

    //1.判断*/运算符
    public static boolean JudgeCC(char c) {
        if (c == '*' || c == '/')
            return true;
        return false;
    }

    //2.判断+-符号
    public static boolean JudgeJJ(char c) {
        if (c == '+' || c == '-')
            return true;
        return false;
    }

    //3.判断是否为运算符号
    public static boolean JudgeFH(char c) {
        return JudgeCC(c) || JudgeJJ(c);
    }

    //4.判断是否为数字
    public static boolean JudgeNumAndDot(char c) {
        return Character.isDigit(c) || c == '.';
    }

    //5.将数字字符串转换为数字
    public static double StrToNum(String s) {  return Double.parseDouble(s); }

    //6.将数字转换为字符串
    public static String NumToStr(double t) { return t + ""; }

    //7.将两个数字进行运算
    public static double TwoCalu(double a, char c, double b) {
        if (c == '*')
            return a * b;
        if (c == '/')
            return a / b;
        if (c == '+')
            return a + b;
        if (c == '-')
            return a - b;
        return 0;
    }

    //8.判断字符串中是否含有*/
    public static boolean JudgeStrcc(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (JudgeCC(s.charAt(i)))
                return true;
        }
        return false;
    }

    //9.判断字符串是否为纯数字(新)
    public static boolean JudgeStrnum(String s) {
        Pattern pattern = Pattern.compile("^-?[1-9][0-9]*\\.[0-9]*|^-?0\\.[0-9]*|^-?[1-9][0-9]*[^\\.]$|^0$");
        return pattern.matcher(s).matches();
    }

    //10.判断左括号
    public static boolean JudgeLeft(char c) {
        return c == '(';
    }

    //11.判断右括号
    public static boolean JudgeRight(char c) {
        return c == ')';
    }

    //12.处理纯符号(+-)字符串
    public static double HandleJJFH(String s) {
        int q = 0;
        int i = 0;

        Queue<Double> Q = new LinkedList<Double>();
        String strnum = null;
        String fuhao = new String("");
        while (i < s.length()) {
            if (JudgeNumAndDot(s.charAt(i)) && i + 1 != s.length()) {
                i++;
                continue;
            }
            if (JudgeFH(s.charAt(i)) || (i + 1 == s.length())) {
                //截取数字部分
                if (i + 1 == s.length())
                    strnum = s.substring(q, i + 1);
                if (JudgeFH(s.charAt(i))) {
                    fuhao += s.charAt(i);
                    strnum = s.substring(q, i);
                }
                Q.offer(StrToNum(strnum));
                q = i + 1;
                i++;
            }
        }

        //取出头部元素
        Double a = Q.poll();
        i = 0;
        while (!Q.isEmpty()) {
            a = TwoCalu(a, fuhao.charAt(i), Q.poll());
            i++;
        }
        return a;
    }

    //13.判断一个字符串里面有几对括号
    public int KuohaoNum(String s, Queue<Kuohao> left, Stack<Kuohao> right) {
        for (int i = 0; i < s.length(); i++) {
            if (JudgeLeft(s.charAt(i)))
                left.offer(new Kuohao(i, s.charAt(i)));
            if (JudgeRight(s.charAt(i)))
                right.push(new Kuohao(i, s.charAt(i)));
        }
        if (left.size() == right.size())
            return left.size();
        return -1;
    }

    //14.处理多种符号字符串(可能要做优化)
    public double HandleAll_NonBracket(String s) {
        if(s.charAt(0) == '-' && JudgeStrnum(s.substring(1,s.length())))//可能s为-3
            return StrToNum(s);

        int i = 0, q = 0, countJJ = 0;
        String CCstr = "", fuhao = "";

        Queue<Double> Q = new LinkedList<Double>();
        while (i < s.length()) {
            //数字或*/
            if (JudgeNumAndDot(s.charAt(i)) || JudgeCC(s.charAt(i))) {
                i++;
                continue;
            }

            //发现+/-运算符
            if (JudgeJJ(s.charAt(i))) {
                countJJ++;
                fuhao += s.charAt(i);
                //发现第一个+-
                if (countJJ == 1) {
                    CCstr = s.substring(0, i);
                    //如果含有*/
                    if (JudgeStrcc(CCstr))
                        Q.offer(HandleJJFH(CCstr));
                    else
                        Q.offer(StrToNum(CCstr));
                }
                //发现非第一个+-
                else {
                    //截取是否含有连续*/的字符串
                    CCstr = s.substring(q + 1, i);

                    //中间纯数字
                    if (JudgeStrnum(CCstr))
                        Q.offer(StrToNum(CCstr));

                    //中间包含*/
                    if (JudgeStrcc(CCstr))
                        Q.offer(HandleJJFH(CCstr));
                }
                q = i;
                i++;
            }
        }

        if (countJJ == 0)
            return HandleJJFH(s);
        CCstr = s.substring(q + 1, s.length());
        //如果含有*/
        if (JudgeStrcc(CCstr))
            Q.offer(HandleJJFH(CCstr));
        else//如果纯数字
            Q.offer(StrToNum(CCstr));

        //取出头部并且返回头部值
        double a = Q.poll();
        i = 0;
        while (!Q.isEmpty()) {
            a = TwoCalu(a, fuhao.charAt(i), Q.poll());
            i++;
        }
        return a;
    }

    //15.处理带( )的字符串
    public double HandleBracket(String s) {
        Queue<Kuohao> L = new LinkedList<Kuohao>();
        Stack<Kuohao> R = new Stack<Kuohao>();
        String t = "";
        int num = KuohaoNum(s, L, R);
        if (num == 0)
            return HandleAll_NonBracket(s);
        else {
            Kuohao q = L.poll();
            Kuohao p = R.pop();
            String str = s.substring(q.index + 1, p.index);//将最外层括号去掉
            t += s.substring(0, q.index);//括号左边部分字符串
            double temp = HandleBracket(str);//处理左右括号中间字符串
            String midres = NumToStr(temp);
            if (t.length() == 0)//例如(1+2*3),那么t=1+2*3
                t = t + midres;
            if (t.charAt(t.length() - 1) == '+' && midres.charAt(0) == '-')//可能会出现123+ -4的情况
                t = t.substring(0, t.length() - 1);
            if (!t.equals(midres))//例如(1+2.5*4),那么t=11,midres=11,但结果只能返回7而不是14
                t = t + midres;

            t = t + s.substring(p.index + 1, s.length());
            return HandleAll_NonBracket(t);
        }
    }

    //16.标准化输出，如果结果为4.0那么输出4；如果结果为3.8那么输出3.8
    public static void StandardPrint(double res){
        if(res == (int)res)
            System.out.println((int)res);
        else
            System.out.println(res);
    }

    public static void main(String args[]) {
        Test s = new Test();
        String input = "1+2.5*4";
        if(s.JudgeLegal(input))
            StandardPrint(s.HandleBracket(input));
    }
}
