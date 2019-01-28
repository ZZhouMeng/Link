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
	private int w = 50;// ���﷽��ͼ���Ŀ��
	private int GameSize;// ���ִ�С��������

	private Stack<Line> path;

	private int x1, y1;// ��ѡ�е�һ��ĵ�ͼ����
	private int x2, y2;// ��ѡ�еڶ���ĵ�ͼ����

	private int COL;
	private int ROW;
	private int[] map = new int[10 * 10];
	private int BLANK = -1;
	private int num;

	private GraphicsContext g = this.getGraphicsContext2D();
	private boolean check = false;// �Ƿ��Ѿ�ѡ�е�һ��
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

				if (e.getButton() == MouseButton.PRIMARY) {// �������

					// �������ķ����λ������
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
								// ��ռ�¼�����ֵ
								map[y1 * COL + x1] = BLANK;
								map[y2 * COL + x2] = BLANK;

								new Thread(() -> {
									try {
										// ����������Ч
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

	// �ж�ѡ�е����������Ƿ��������
	protected boolean isCut(int x1, int y1, int x2, int y2) {
		// x��ͬ����ֱ������ͨ
		if (x1 == x2) {
			if (xLink(x1, y1, y2)) {
				// ����
				g.strokeLine(x1 * w + w * 1.5, y1 * w + w * 1.5, x1 * w + w * 1.5, y2 * w + w * 1.5);
				return true;
			}
		}
		// y��ͬ��ˮƽ������ͨ
		else if (y1 == y2) {
			if (yLink(x1, x2, y1)) {
				// ����
				g.strokeLine(x1 * w + w * 1.5, y1 * w + w * 1.5, x2 * w + w * 1.5, y1 * w + w * 1.5);
				return true;
			}
		}
		// һ��ת�䣨�۵㣩����ͨ��ʽ
		if (oneCornerLink(x1, y1, x2, y2)) {
			//
			return true;
		}
		// ����ת�䣨�۵㣩����ͨ��ʽ
		else if (twoCornerLink(x1, y1, x2, y2)) {
			return true;
		}
		return false;
	}

	// xֱ����ͨ����ֱ������ͨ
	private boolean xLink(int x, int y1, int y2) {
		int t;
		// ��֤y1��ֵС��y2
		if (y1 > y2) {// ���ݽ���
			t = y1;
			y1 = y2;
			y2 = t;
		}
		// ֱͨ
		for (int i = y1 + 1; i < y2; i++) {
			if (map[i * COL + x] != BLANK) {
				return false;
			}
		}

		return true;
	}

	// yֱ����ͨ��ˮƽ������ͨ
	private boolean yLink(int x1, int x2, int y) {
		int t;
		// ��֤x1��ֵС��x2
		if (x1 > x2) {// ���ݽ���
			t = x1;
			x1 = x2;
			x2 = t;
		}

		// ֱͨ
		for (int i = x1 + 1; i < x2; i++) {
			if (map[y * COL + i] != BLANK) {
				return false;
			}
		}

		return true;
	}

	// һ���۵���ͨ
	private boolean oneCornerLink(int x1, int y1, int x2, int y2) {
		// �жϾ����۵�(x2,y1)�Ƿ��
		if (map[y1 * COL + x2] == BLANK) {
			// �ж��۵㣨x2,y1)������Ŀ����Ƿ�ֱͨ
			if (yLink(x1, x2, y1) && xLink(x2, y1, y2)) {
				// ����
				g.strokeLine(x2 * w + w * 1.5, y1 * w + w * 1.5, x2 * w + w * 1.5, y2 * w + w * 1.5);
				g.strokeLine(x1 * w + w * 1.5, y1 * w + w * 1.5, x2 * w + w * 1.5, y1 * w + w * 1.5);
				return true;
			}
		}
		//// �жϾ����۵�(x1,y2)�Ƿ��
		if (map[y2 * COL + x1] == BLANK) {
			// �ж��۵㣨x1,y2)������Ŀ����Ƿ�ֱͨ
			if (yLink(x2, x1, y2) && xLink(x1, y2, y1)) {
				// ����
				g.strokeLine(x1 * w + w * 1.5, y2 * w + w * 1.5, x1 * w + w * 1.5, y1 * w + w * 1.5);
				g.strokeLine(x2 * w + w * 1.5, y2 * w + w * 1.5, x1 * w + w * 1.5, y2 * w + w * 1.5);
				return true;
			}
		}
		return false;
	}

	// �����۵���ͨ
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
		// ���ϲ��� �۵�
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
		// ���²��� �۵�
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
		// ������� �۵�
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
		// ���Ҳ��� �۵�
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
		// z=1ʱ�ж������Ƿ�Ϊ��
		// z=2�ж������Ƿ�Ϊ��
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
		// z=1ʱ�ж������Ƿ�Ϊ��
		// z=2�ж������Ƿ�Ϊ��
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

		// ���ſ�ʼ��Ч
		MediaPlayer mp1 = new MediaPlayer(new Media(URL));
		mp1.play();

		// ��ʼ����ͼ������ͼ�����з�������λ����Ϊ�շ���״̬
		for (int i = 0; i < (COL * ROW); i++) {
			map[i] = BLANK;
		}

		// ������ƥ��ɶԵĶ������ַŽ�һ����ʱ��ͼ��
		ArrayList temp = new ArrayList();
		for (int i = 0; i < (COL * ROW) / num; i++) {
			for (int j = 0; j < num; j++) {
				temp.add(i);
			}
		}

		// ���������ͼ
		Random random = new Random();
		// ÿ�δ��������ʱ��ͼ��ȡ�ߣ���ȡ������ʱ��ͼɾ����
		// һ������ŵ���ͼ�Ŀշ�����
		for (int i = 0; i < ROW * COL; i++) {
			// �����ѡһ��λ��
			int nIndex = random.nextInt(temp.size());
			// ��ȡ��ѡ�����ַŵ���ͼ�Ŀշ���
			map[i] = (Integer) temp.get(nIndex);

			// ����ʱ��ͼ��ȥ�ö���
			temp.remove(nIndex);
		}
		for (int i = 0; i < ROW * COL; i++) {
			g.drawImage(createImage(map[i]), w * (i % GameSize) + w, w * (i / GameSize) + w, w, w);
		}

	}

	// creatImage()����ʵ�ֱ��n�����ж���ͼ��ȡͼ
	private Image createImage(int n) {
		Image image = new Image("res/" + n % num + ".png");
		return image;
	}

	// ����Ϸ����
	protected void myRepaint() {
		for (int i = -1; i <= ROW; i++) {
			for (int j = -1; j <= COL; j++) {
				if (i == -1 || i == ROW || j == -1 || j == COL) {
					g.clearRect(i * w + w, j * w + w, w, w);
				}
			}
		}
		for (int i = 0; i < COL * ROW; i++) {
			if (map[i] == BLANK) {// �˴��ǿհ׿�
				g.clearRect(w * (i % GameSize) + w, w * (i / GameSize) + w, w, w);
			} else {
				g.drawImage(createImage(map[i]), w * (i % GameSize) + w, w * (i / GameSize) + w, w, w);
			}
		}

	}

}
