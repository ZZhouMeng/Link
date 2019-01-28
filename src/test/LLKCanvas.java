package test;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.*;

public class LLKCanvas extends Canvas {
	private int w = 50;// 动物方块图案的宽度
	private int GameSize;// 布局大小即行列数

	private Stack<Line> path;

	private int x1, y1;// 被选中第一块的地图坐标
	private int x2, y2;// 被选中第二块的地图坐标

	private int COL;
	private int ROW;
	private int[] map = new int[10 * 10];
	private int BLANK = -1;
	private int num;

	private GraphicsContext g = this.getGraphicsContext2D();
	private boolean check = false;// 是否已经选中第一块
	private final static String URL = "http://dx.sc.chinaz.com/Files/DownLoad/sound1/201405/4438.mp3";
	private final static String URLBase = "http://dx.sc.chinaz.com/Files/DownLoad/sound1/201711/9439.mp3";
	private final static String URLBase1 = "http://dx.sc.chinaz.com/Files/DownLoad/sound1/201501/5391.mp3";

	public LLKCanvas(int GameSize, int num) {

		this.GameSize = GameSize;
		this.COL = GameSize;
		this.ROW = GameSize;
		this.num = num;

		this.setWidth(75 * GameSize);
		this.setHeight(66 * GameSize);

		this.initMap(GameSize, num);

		playGame();
	}

	public void playGame() {

		this.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {

				if (e.getButton() == MouseButton.PRIMARY) {// 左键单击

					// 计算点击的方块的位置坐标
					x2 = (int) ((e.getX() - w) / w);
					y2 = (int) ((e.getY() - w) / w);

					if (map[y2 * COL + x2] == BLANK || x2 >= GameSize) {
						return;
					}

					if (check == false) {
						x1 = x2;
						y1 = y2;
						g.setStroke(Color.RED);
						g.setLineWidth(3);
						g.strokeRoundRect(w * x1 + w, w * y1 + w, w, w, 4, 4);
					} else {
						myRepaint();

						if (isSame(x1, y1, x2, y2) && isCut(x1, y1, x2, y2) && (x1 != x2 || y1 != y2)
								&& map[y1 * COL + x1] != BLANK && map[y2 * COL + x2] != BLANK) {

							int grade = Integer.parseInt(MainFrame.textArea.getText()) + 2;
							MainFrame.textArea.setText(String.valueOf(grade));

							if (x1 != x2 || y1 != y2) {
								// 清空记录方块的值
								map[y1 * COL + x1] = BLANK;
								map[y2 * COL + x2] = BLANK;

								new Thread(() -> {
									try {
										// 播放消除音效
										MediaPlayer mp = new MediaPlayer(new Media(URLBase));
										mp.play();
										Thread.sleep(500);
										myRepaint();
										if (grade == ROW * COL) {
											MediaPlayer mp1 = new MediaPlayer(new Media(URLBase1));
											mp1.play();
											g.drawImage(new Image("res/su.png"), GameSize*12, GameSize*12, GameSize*50, GameSize*40);
											
										}
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
								}).start();
							}

						}

					}
					check = !check;
				}

			}
		});

	}

	protected boolean isSame(int x1, int y1, int x2, int y2) {
		if (map[y1 * COL + x1] == map[y2 * COL + x2] || map[y1 * COL + x1] % num == map[y2 * COL + x2] % num) {
			return true;
		} else {
			return false;
		}
	}

	// 判断选中的两个方块是否可以消除
	protected boolean isCut(int x1, int y1, int x2, int y2) {
		// x相同，垂直方向联通
		if (x1 == x2) {
			if (xLink(x1, y1, y2)) {
				// 划线
				g.strokeLine(x1 * w + w * 1.5, y1 * w + w * 1.5, x1 * w + w * 1.5, y2 * w + w * 1.5);
				return true;
			}
		}
		// y相同，水平方向连通
		else if (y1 == y2) {
			if (yLink(x1, x2, y1)) {
				// 划线
				g.strokeLine(x1 * w + w * 1.5, y1 * w + w * 1.5, x2 * w + w * 1.5, y1 * w + w * 1.5);
				return true;
			}
		}
		// 一个转弯（折点）的连通方式
		if (oneCornerLink(x1, y1, x2, y2)) {
			//
			return true;
		}
		// 两个转弯（折点）的连通方式
		else if (twoCornerLink(x1, y1, x2, y2)) {
			return true;
		}
		return false;
	}

	// x直接连通即垂直方向连通
	private boolean xLink(int x, int y1, int y2) {
		int t;
		// 保证y1的值小于y2
		if (y1 > y2) {// 数据交换
			t = y1;
			y1 = y2;
			y2 = t;
		}
		// 直通
		for (int i = y1 + 1; i < y2; i++) {
			if (map[i * COL + x] != BLANK) {
				return false;
			}
		}

		return true;
	}

	// y直接连通即水平方向连通
	private boolean yLink(int x1, int x2, int y) {
		int t;
		// 保证x1的值小于x2
		if (x1 > x2) {// 数据交换
			t = x1;
			x1 = x2;
			x2 = t;
		}

		// 直通
		for (int i = x1 + 1; i < x2; i++) {
			if (map[y * COL + i] != BLANK) {
				return false;
			}
		}

		return true;
	}

	// 一个折点连通
	private boolean oneCornerLink(int x1, int y1, int x2, int y2) {
		// 判断矩形折点(x2,y1)是否空
		if (map[y1 * COL + x2] == BLANK) {
			// 判断折点（x2,y1)与两个目标点是否直通
			if (yLink(x1, x2, y1) && xLink(x2, y1, y2)) {
				// 划线
				g.strokeLine(x2 * w + w * 1.5, y1 * w + w * 1.5, x2 * w + w * 1.5, y2 * w + w * 1.5);
				g.strokeLine(x1 * w + w * 1.5, y1 * w + w * 1.5, x2 * w + w * 1.5, y1 * w + w * 1.5);
				return true;
			}
		}
		//// 判断矩形折点(x1,y2)是否空
		if (map[y2 * COL + x1] == BLANK) {
			// 判断折点（x1,y2)与两个目标点是否直通
			if (yLink(x2, x1, y2) && xLink(x1, y2, y1)) {
				// 划线
				g.strokeLine(x1 * w + w * 1.5, y2 * w + w * 1.5, x1 * w + w * 1.5, y1 * w + w * 1.5);
				g.strokeLine(x2 * w + w * 1.5, y2 * w + w * 1.5, x1 * w + w * 1.5, y2 * w + w * 1.5);
				return true;
			}
		}
		return false;
	}

	// 两个折点连通
	private boolean twoCornerLink(int x1, int y1, int x2, int y2) {
		int t, x, y;
		if (x1 > x2) {
			t = x1;
			x1 = x2;
			x2 = t;

			t = y1;
			y1 = y2;
			y2 = t;
		}
		// 向上查找 折点
		for (y = y1 - 1; y >= -1; y--) {
			if (y == -1) {
				if (YThrough(x2, y2 - 1, 2)) {
					g.strokeLine(x1 * w + w * 1.5, y1 * w + w * 1.5, x1 * w + w * 1.5, y * w + w * 1.5);
					g.strokeLine(x1 * w + w * 1.5, y * w + w * 1.5, x2 * w + w * 1.5, y * w + w * 1.5);
					return true;
				} else {
					break;
				}
			}
			if (map[y * COL + x1] != BLANK) {
				break;
			}
			if (oneCornerLink(x1, y, x2, y2)) {
				g.strokeLine(x1 * w + w * 1.5, y * w + w * 1.5, x1 * w + w * 1.5, y1 * w + w * 1.5);
				return true;
			}
		}
		// 向下查找 折点
		for (y = y1 + 1; y <= ROW; y++) {
			if (y == ROW) {
				if (YThrough(x2, y2 + 1, 1)) {
					g.strokeLine(x1 * w + w * 1.5, y1 * w + w * 1.5, x1 * w + w * 1.5, y * w + w * 1.5);
					g.strokeLine(x1 * w + w * 1.5, y * w + w * 1.5, x2 * w + w * 1.5, y * w + w * 1.5);
					return true;
				} else {
					break;
				}
			}
			if (map[y * COL + x1] != BLANK) {
				break;
			}
			if (oneCornerLink(x1, y, x2, y2)) {
				g.strokeLine(x1 * w + w * 1.5, y * w + w * 1.5, x1 * w + w * 1.5, y1 * w + w * 1.5);
				return true;
			}
		}
		// 向左查找 折点
		for (x = x1 - 1; x >= -1; x--) {
			if (x == -1) {
				if (XThrough(x2 - 1, y2, 2)) {
					g.strokeLine(x1 * w + w * 1.5, y1 * w + w * 1.5, x * w + w * 1.5, y1 * w + w * 1.5);
					g.strokeLine(x * w + w * 1.5, y1 * w + w * 1.5, x * w + w * 1.5, y2 * w + w * 1.5);
					return true;
				} else {
					break;
				}
			}
			if (map[y1 * COL + x] != BLANK) {
				break;
			}
			if (oneCornerLink(x, y1, x2, y2)) {
				g.strokeLine(x * w + w * 1.5, y1 * w + w * 1.5, x1 * w + w * 1.5, y1 * w + w * 1.5);
				return true;
			}
		}
		// 向右查找 折点
		for (x = x1 + 1; x <= COL; x++) {
			if (x == COL) {
				if (XThrough(x2 + 1, y2, 1)) {
					g.strokeLine(x1 * w + w * 1.5, y1 * w + w * 1.5, x * w + w * 1.5, y1 * w + w * 1.5);
					g.strokeLine(x * w + w * 1.5, y1 * w + w * 1.5, x * w + w * 1.5, y2 * w + w * 1.5);
					return true;
				} else {
					break;
				}
			}
			if (map[y1 * COL + x] != BLANK) {
				break;
			}
			if (oneCornerLink(x, y1, x2, y2)) {
				g.strokeLine(x * w + w * 1.5, y1 * w + w * 1.5, x1 * w + w * 1.5, y1 * w + w * 1.5);
				return true;
			}
		}
		return false;
	}

	private boolean YThrough(int x, int y, int z) {
		// z=1时判断向上是否都为空
		// z=2判断向下是否都为空
		if (z == 1) {
			for (int i = y; i < ROW; i++) {
				if (map[i * COL + x] != BLANK) {
					return false;
				}
			}
		} else {
			for (int i = 0; i <= y; i++) {
				if (map[i * COL + x] != BLANK) {
					return false;
				}
			}
		}
		if (z == 1) {
			g.strokeLine(x * w + w * 1.5, (y - 1) * w + w * 1.5, x * w + w * 1.5, ROW * w + w * 1.5);
		} else {
			g.strokeLine(x * w + w * 1.5, (y + 1) * w + w * 1.5, x * w + w * 1.5, w * 0.5);
		}
		return true;
	}

	private boolean XThrough(int x, int y, int z) {
		// z=1时判断向右是否都为空
		// z=2判断向左是否都为空
		if (z == 1) {
			for (int i = x; i < COL; i++) {
				if (map[y * COL + i] != BLANK) {
					return false;
				}
			}
		} else {
			for (int i = 0; i <= x; i++) {
				if (map[y * COL + i] != BLANK) {
					return false;
				}
			}
		}
		if (z == 1) {
			g.strokeLine((x - 1) * w + w * 1.5, y * w + w * 1.5, COL * w + w * 1.5, y * w + w * 1.5);
		} else {
			g.strokeLine((x + 1) * w + w * 1.5, y * w + w * 1.5, w * 0.5, y * w + w * 1.5);
		}
		return true;
	}

	public void initMap(int gameSize, int num) {

		// 播放开始音效
		MediaPlayer mp1 = new MediaPlayer(new Media(URL));
		mp1.play();

		// 初始化地图，将地图中所有方块区域位置置为空方块状态
		for (int i = 0; i < (COL * ROW); i++) {
			map[i] = BLANK;
		}

		// 将所有匹配成对的动物物种放进一个临时地图中
		ArrayList temp = new ArrayList();
		for (int i = 0; i < (COL * ROW) / num; i++) {
			for (int j = 0; j < num; j++) {
				temp.add(i);
			}
		}

		// 生成随机地图
		Random random = new Random();
		// 每次从上面的临时地图中取走（获取后并在临时地图删除）
		// 一个动物放到地图的空方块上
		for (int i = 0; i < ROW * COL; i++) {
			// 随机挑选一个位置
			int nIndex = random.nextInt(temp.size());
			// 获取该选定物种放到地图的空方块
			map[i] = (Integer) temp.get(nIndex);

			// 在临时地图除去该动物
			temp.remove(nIndex);
		}
		for (int i = 0; i < ROW * COL; i++) {
			g.drawImage(createImage(map[i]), w * (i % GameSize) + w, w * (i / GameSize) + w, w, w);
		}

	}

	// creatImage()方法实现标号n从所有动物图中取图
	private Image createImage(int n) {
		Image image = new Image("res/" + n % num + ".png");
		return image;
	}

	// 画游戏界面
	protected void myRepaint() {
		for (int i = -1; i <= ROW; i++) {
			for (int j = -1; j <= COL; j++) {
				if (i == -1 || i == ROW || j == -1 || j == COL) {
					g.clearRect(i * w + w, j * w + w, w, w);
				}
			}
		}
		for (int i = 0; i < COL * ROW; i++) {
			if (map[i] == BLANK) {// 此处是空白块
				g.clearRect(w * (i % GameSize) + w, w * (i / GameSize) + w, w, w);
			} else {
				g.drawImage(createImage(map[i]), w * (i % GameSize) + w, w * (i / GameSize) + w, w, w);
			}
		}

	}

}
