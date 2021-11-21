package spring.schedule.controller;

import org.joda.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.extern.slf4j.Slf4j;
import spring.schedule.constants.Constants;
import spring.schedule.entity.DayEntity;
import spring.schedule.exception.ExclusiveException;

/**
 * ControllerAdviceでアノテーションされたクラスは クラスパススキャンによって自動検出されます。
 * 
 * @author 2020007523
 *
 */
@ControllerAdvice
@Slf4j
public class ExceptionAdvisor {
	// 改行コード
	String lineCd = System.getProperty(Constants.SYSTEM_PROPETY_LINE_SEPARATOR);

	/**
	 * 例外ハンドラー
	 * 
	 * @param ex
	 * @param model
	 * @return
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public String handleException(Exception ex, Model model) {
		StringBuilder stackContent = new StringBuilder();
		for (StackTraceElement stack : ex.getStackTrace()) {
			stackContent.append("クラス名：" + stack.getClassName());
			stackContent.append(lineCd);
			stackContent.append("ファイル名：" + stack.getFileName());
			stackContent.append(lineCd);
			stackContent.append("行目：" + stack.getLineNumber());
			stackContent.append(lineCd);
			stackContent.append("メソッド名" + stack.getMethodName());
			stackContent.append(lineCd);
		}
		log.error(ex.getMessage(), ex);
		// 日付情報をexに格納する。
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(LocalDate.now().getYear());
		dayEntity.setCalendarMonth(LocalDate.now().getMonthOfYear());
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute(Constants.ERROR_MESSAGE, "\"予期せぬエラーが発生しました。");
		model.addAttribute("stackContent", stackContent.toString());
		return Constants.RETURN_ERROR;
	}

	/**
	 * 楽観的排他例外ハンドラー
	 * 
	 * @param ex
	 * @param model
	 * @return
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(ExclusiveException.class)
	// メソッド名を分ける
	public String handleExclusiveException(ExclusiveException ex, Model model) {
		StringBuilder stackContent = new StringBuilder();
		for (StackTraceElement stack : ex.getStackTrace()) {
			stackContent.append("クラス名：" + stack.getClassName());
			stackContent.append(lineCd);
			stackContent.append("ファイル名：" + stack.getFileName());
			stackContent.append(lineCd);
			stackContent.append("行目：" + stack.getLineNumber());
			stackContent.append(lineCd);
			stackContent.append("メソッド名" + stack.getMethodName());
			stackContent.append(lineCd);
		}
		log.error(ex.getMessage(), ex);

		// カレンダーに戻るボタン用の日付
		// 日付情報をexに格納する。
		DayEntity dayEntity = new DayEntity();
		dayEntity.setCalendarYear(ex.getCalendarYear());
		dayEntity.setCalendarMonth(ex.getCalendarMonth());
		model.addAttribute("stackContent", stackContent.toString());
		model.addAttribute("dayEntity", dayEntity);
		model.addAttribute(Constants.ERROR_MESSAGE, "\"他のユーザが更新しています。カレンダー表示画面に戻って再度更新してください。");
		return Constants.RETURN_ERROR;
	}
}