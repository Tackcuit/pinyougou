package com.qf.gobang;

import com.sun.org.apache.bcel.internal.generic.BALOAD;

//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖镇楼           BUG辟易
//
//                             佛曰:
//
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
public class GoBang {
	//棋盘
	//在进行判断的时候要首先判断是不是当前位置被占下了
	private static String[][] face = new String[16][16];
	//创建一个标志着先后手的标识，默认false是黑子先行
	private boolean flag = false;
	//标志着有荆轲棋子是连续的
	private static int win = 0;

	public static void main(String[] args) {
		//创建一个五子棋程序，用输入来定位地方，用二位数组模拟棋盘
		showFace();
	}

	private static void showFace() {
		for (String[] strings : face) {
			for (String string : strings) {
				string = "  +  ";
				System.out.print(string);
			}
			System.out.println();
		}
	}

	private void whiteGo(String whitePosition) {
		String[] position = whitePosition.split(",");
		face[Integer.parseInt(position[0])][Integer.parseInt(position[1])] = "○";
	}

	private void BlackGo(String blackPositon) {
		String[] position = blackPositon.split(",");
		face[Integer.parseInt(position[0])][Integer.parseInt(position[1])] = "●";
	}

	private boolean winOrLose() {
		boolean result = false;
		//遍历数组，要是赢的话有三种可能性，第一种是竖直线，第二种是横线，第三种是斜线
		for (String[] strings : face) {
			for (String point : strings) {
				if ("○".equals(point)) {
					//先判断横线

				}
			}
		}
		return result;
	}

	private boolean findWhite() {
		//使用带有索引的循环遍历
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				if ("○".equals(face[i][j])) {
					System.out.println("找到一枚白子");
					win++;
				}
			}
		}
		return false;
	}

	private void findWinner(int row, int clomn) {
		if ("○".equals(face[row][++clomn])) {
			win++;
			if (win > 4) {
				System.out.println("白子已经获胜了");
			} else {
				findWinner(row, clomn);
			}
		} else if ("○".equals(face[++row][clomn])) {
			win++;
			if (win > 4) {
				System.out.println("白子已经获胜了");
			} else {
				findWinner(row, clomn);
			}
		} else if ("○".equals(face[++row][++clomn])) {
			win++;
			if (win > 4) {
				System.out.println("白子已经获胜了");
			} else {
				findWinner(row, clomn);
			}
		} else {
			win = 0;
		}

	}
}
