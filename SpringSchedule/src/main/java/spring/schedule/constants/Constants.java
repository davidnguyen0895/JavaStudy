package spring.schedule.constants;

/**
 * 定数クラス
 * @author thinh
 *
 */
public final class Constants {
	//ホームページ画面
	public static final String RETURN_INDEX = "index";
	//return用定数 スケジュール情報の新規登録画面
	public static final String RETURN_CREATE_SCHEDULE_FORM = "createScheduleForm";
	//return用定数 スケジュール情報の新規登録画面
	public static final String RETURN_UPDATE_SCHEDULE_FORM = "updateScheduleForm";
	//return用定数 カレンダー表示画面．
	public static final String RETURN_DISPLAY_CALENDAR = "displayCalendar";
	//return用定数 カレンダー表示画面．
	public static final String REDIRECT_DISPLAY_CALENDAR = "redirect:/displayCalendar";
	//return用定数 スケージュール詳細情報を表示する画面．
	public static final String RETURN_SHOW_SCHEDULE_DETAIL = "showScheduleDetail";
	//return用定数 スケージュール情報を全件検索画面．
	public static final String RETURN_SELECT_ALL = "selectAll";
	//return用定数 スケージュール情報を全件検索画面．
	public static final String RETURN_SELECT_BY_ID = "selectById";
	//return用定数 スケージュール情報を全件検索画面．
	public static final String RETURN_SELECT_ALL_USER = "selectAllUser";
	//return用定数 エラー画面
	public static final String RETURN_ERROR = "error";
	//スペース
	public static final String SPACE = " ";
	//【操作】登録
	public static final String ACTION_REGIST = "regist";
	//【操作】更新
	public static final String ACTION_UPDATE = "update";
	//【操作】参照
	public static final String ACTION_SEARCH = "search";
	//今日の日付
	public static final java.time.LocalDate TODAY = java.time.LocalDate.now();
}
