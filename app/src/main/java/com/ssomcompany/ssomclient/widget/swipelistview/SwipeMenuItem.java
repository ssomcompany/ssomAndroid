package com.ssomcompany.ssomclient.widget.swipelistview;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class SwipeMenuItem {
	private int id;
	private Context mContext;
	private String title;
	private Drawable icon;
	private Drawable background;
	private int marginBetweenIconAndText;  // sets if needs icon and text both, margin between icon and text
	private int titleStyle;
	private int titleColor;
	private int titleSize;
	private int width;
	private int type; // can be used when you have multiple menu types and you
						// need to know from which menu type comes the action

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public SwipeMenuItem(Context context) {
		mContext = context;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setMarginBetweenIconAndText(int margin) {
		this.marginBetweenIconAndText = margin;
	}

	public int getMarginBetweenIconAndText() {
		return marginBetweenIconAndText;
	}

	public void setTitleStyle(int titleStyle) {
		this.titleStyle = titleStyle;
	}

	public int getTitleStyle() {
		return titleStyle;
	}

	public int getTitleColor() {
		return titleColor;
	}

	public int getTitleSize() {
		return titleSize;
	}

	public void setTitleSize(int titleSize) {
		this.titleSize = titleSize;
	}

	public void setTitleColor(int titleColor) {
		this.titleColor = titleColor;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle(int resId) {
		setTitle(mContext.getString(resId));
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public void setIcon(int resId) {
		this.icon = mContext.getResources().getDrawable(resId);
	}

	public Drawable getBackground() {
		return background;
	}

	public void setBackground(Drawable background) {
		this.background = background;
	}

	public void setBackground(int resId) {
		this.background = mContext.getResources().getDrawable(resId);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

}
