package spring.schedule.constants;

import java.text.SimpleDateFormat;

/**
 * 定数クラス
 * @author thinh
 *
 */
public final class Constants {
	//ホームページ画面
	public static final String INDEX = "index";
	//return用定数 スケジュール情報の新規登録画面
	public static final String CREATE_SCHEDULE_FORM = "createScheduleForm";
	//return用定数 スケジュール情報の新規登録の結果表示画面
	public static final String CREATE_SCHEDULE_RESULT = "createScheduleResult";
	//return用定数 スケジュール情報の新規登録画面
	public static final String UPDATE_SCHEDULE_FORM = "updateScheduleForm";
	//return用定数 スケジュール情報の新規登録の結果表示画面
	public static final String UPDATE_SCHEDULE_RESULT = "updateScheduleResult";
	//return用定数 カレンダー表示画面．
	public static final String DISPLAY_CALENDAR = "displayCalendar";
	//return用定数 カレンダー表示画面．
	public static final String REDIRECT_DISPLAY_CALENDAR = "redirect:/displayCalendar";
	//return用定数 スケージュール詳細情報を表示する画面．
	public static final String SHOW_SCHEDULE_DETAIL = "showScheduleDetail";
	//return用定数 スケージュール情報を全件検索画面．
	public static final String SELECT_ALL = "selectAll";
	//return用定数 スケージュール情報を全件検索画面．
	public static final String SELECT_BY_ID = "selectById";
	//日付変換定数 : yyyy/MM/dd
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	//日付変換定数 : hh:mm
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
	//日付変換定数 : yyyy/MM/dd hh:mm:ss
	public static final SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	//スペース
	public static final String SPACE = " ";
}
